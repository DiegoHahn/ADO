package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.AzureUserIDRequest;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Model.UserInformationRequest;
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

    @Autowired
    public UserInformationController(AzureDevOpsClient azureDevOpsClient, UserInformationService userInformationService) {
        this.azureDevOpsClient = azureDevOpsClient;
        this.userInformationService = userInformationService;
    }

    @PostMapping
    public ResponseEntity<UserInformation> saveUserInformation(@RequestBody UserInformation userInformation) {
        try {
            UserInformation savedUserInformation = userInformationService.saveUserInformation(userInformation);
            return new ResponseEntity<>(savedUserInformation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/details")
    public ResponseEntity<UserInformation> getCurrentUserInformation(@RequestBody AzureUserIDRequest request) {
        try {
            UserInformation userInformation = userInformationService.getUserInformationByUserEmail(request.getEmail());
            if (userInformation != null) {
                return ResponseEntity.ok(userInformation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/saveOrUpdate")
    public ResponseEntity<String> saveOrUpdateUserInformation(@RequestBody UserInformationRequest request) {
        try {
            UserInformation existingUser = userInformationService.getUserInformationByUserEmail(request.getEmail());

            if (existingUser != null) {
                existingUser.setBoard(request.getBoard());

                // Se o token foi fornecido no formulário, atualize também o token
                if (request.getToken() != null && !request.getToken().isEmpty()) {
                    existingUser.setPersonalAccessToken(request.getToken());
                }

                userInformationService.saveUserInformation(existingUser);
                return ResponseEntity.ok("Dados do usuário atualizados com sucesso!");
            }  else {
                // Se o registro não existe, verifica se o token está presente
                if (request.getToken() != null && !request.getToken().isEmpty()) {
                    String azureUserID = azureDevOpsClient.getAzureUserIDByEmail(request.getEmail(), request.getToken());

                    UserInformation newUser = new UserInformation();
                    newUser.setEmail(request.getEmail());
                    newUser.setBoard(request.getBoard());
                    newUser.setAzureUserID(azureUserID);
                    newUser.setPersonalAccessToken(request.getToken());

                    userInformationService.saveUserInformation(newUser);
                    return ResponseEntity.ok("Novo usuário criado e azureUserID salvo com sucesso!");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token necessário para criação de novo registro.");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar os dados do usuário.");
        }
    }

}