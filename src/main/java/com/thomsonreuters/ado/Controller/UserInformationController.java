package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Exceptions.InvalidTokenException;
import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.AzureUserIDRequest;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Model.UserInformationRequest;
import com.thomsonreuters.ado.Model.UserInformationResponse;
import com.thomsonreuters.ado.Service.ActivityRecordService;
import com.thomsonreuters.ado.Service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userInformation")
public class UserInformationController {
    private final AzureDevOpsClient azureDevOpsClient;
    private final UserInformationService userInformationService;
    private final ActivityRecordService activityRecordService;

    @Autowired
    public UserInformationController(AzureDevOpsClient azureDevOpsClient, UserInformationService userInformationService, ActivityRecordService activityRecordService) {
        this.azureDevOpsClient = azureDevOpsClient;
        this.userInformationService = userInformationService;
        this.activityRecordService = activityRecordService;
    }

    @PostMapping("/details")
    public ResponseEntity<UserInformationResponse> getCurrentUserInformation(@RequestBody AzureUserIDRequest request) {
        try {
            UserInformation userInformation = userInformationService.getUserInformationByUserEmail(request.getEmail());
            if (userInformation != null) {
                UserInformationResponse response = new UserInformationResponse(
                        userInformation.getUserId(),
                        userInformation.getEmail(),
                        userInformation.getBoard(),
                        userInformation.getAzureUserID(),
                        userInformation.getToken() != null
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> saveOrUpdateUserInformation(@RequestBody UserInformationRequest request) {
        try {
            UserInformation existingUser = userInformationService.getUserInformationByUserEmail(request.getEmail());

            String tokenToUse = isTokenProvided(request) ? request.getToken() : existingUser != null ? existingUser.getToken() : null;

            if (tokenToUse != null) {
                try {
                    azureDevOpsClient.getAzureUserIDByEmail(request.getEmail(), tokenToUse);
                } catch (InvalidTokenException e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
                } catch (UserNotFoundException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado para o email fornecido.");
                }
            }

            if (existingUser != null) {
                return updateUser(existingUser, request);
            } else if (isTokenProvided(request)) {
                return createNewUser(request);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token necessário para criação de novo registro.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar os dados do usuário.");
        }
    }

    private ResponseEntity<String> updateUser(UserInformation existingUser, UserInformationRequest request) {
        existingUser.setBoard(request.getBoard());

        if (isTokenProvided(request)) {
            existingUser.setToken(request.getToken());
        }

        userInformationService.saveUserInformation(existingUser);

        activityRecordService.updateActivityRecordsStatus(existingUser.getUserId(), 2, 1);

        return ResponseEntity.ok("Dados do usuário atualizados com sucesso!");
    }

    private ResponseEntity<String> createNewUser(UserInformationRequest request) throws Exception {
        String azureUserID = azureDevOpsClient.getAzureUserIDByEmail(request.getEmail(), request.getToken());

        UserInformation newUser = new UserInformation();
        newUser.setEmail(request.getEmail());
        newUser.setBoard(request.getBoard());
        newUser.setAzureUserID(azureUserID);
        newUser.setToken(request.getToken());

        userInformationService.saveUserInformation(newUser);
        return ResponseEntity.ok("Novo usuário criado e azureUserID salvo com sucesso!");
    }

    private boolean isTokenProvided(UserInformationRequest request) {
        return request.getToken() != null && !request.getToken().isEmpty();
    }
}
