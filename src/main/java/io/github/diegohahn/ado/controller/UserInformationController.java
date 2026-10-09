package io.github.diegohahn.ado.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.diegohahn.ado.client.AzureDevOpsClient;
import io.github.diegohahn.ado.exceptions.AzureDevOpsApiException;
import io.github.diegohahn.ado.exceptions.InvalidTokenException;
import io.github.diegohahn.ado.exceptions.UserNotFoundException;
import io.github.diegohahn.ado.model.AzureUserIDRequest;
import io.github.diegohahn.ado.model.UserInformation;
import io.github.diegohahn.ado.model.UserInformationRequest;
import io.github.diegohahn.ado.model.UserInformationResponse;
import io.github.diegohahn.ado.service.ActivityRecordService;
import io.github.diegohahn.ado.service.UserInformationService;

@RestController
@RequestMapping("/userInformation")
public class UserInformationController {
    private static final Logger logger = LoggerFactory.getLogger(UserInformationController.class);

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
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado para o email fornecido.");
        } catch (HttpTimeoutException e) {
            logger.warn("Azure DevOps timed out while saving user information: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("O Azure DevOps não respondeu a tempo.");
        } catch (AzureDevOpsApiException | IOException | URISyntaxException e) {
            logger.error("Azure DevOps call failed while saving user information", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Falha ao se comunicar com o Azure DevOps.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Requisição interrompida.");
        } catch (RuntimeException e) {
            logger.error("Unexpected error while saving user information", e);
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

    private ResponseEntity<String> createNewUser(UserInformationRequest request)
            throws InvalidTokenException, UserNotFoundException, AzureDevOpsApiException, IOException, InterruptedException, URISyntaxException {
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
