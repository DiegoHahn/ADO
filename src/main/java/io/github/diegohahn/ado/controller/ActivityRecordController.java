package io.github.diegohahn.ado.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.diegohahn.ado.client.AzureDevOpsClient;
import io.github.diegohahn.ado.exceptions.AzureDevOpsApiException;
import io.github.diegohahn.ado.exceptions.InvalidTokenException;
import io.github.diegohahn.ado.exceptions.UserNotFoundException;
import io.github.diegohahn.ado.model.ActivityRecordDTO;
import io.github.diegohahn.ado.model.ActivityRecordResponseDTO;
import io.github.diegohahn.ado.model.UserInformation;
import io.github.diegohahn.ado.service.ActivityRecordService;
import io.github.diegohahn.ado.service.UserInformationService;

@RestController
@RequestMapping("/activityRecord")
public class ActivityRecordController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityRecordController.class);

    private final ActivityRecordService activityRecordService;
    private final UserInformationService userInformationService;
    private final AzureDevOpsClient azureDevOpsClient;

    @Autowired
    public ActivityRecordController(ActivityRecordService activityRecordService, UserInformationService userInformationService, AzureDevOpsClient azureDevOpsClient) {
        this.activityRecordService = activityRecordService;
        this.userInformationService = userInformationService;
        this.azureDevOpsClient = azureDevOpsClient;
    }

    @PostMapping
    public ResponseEntity<?> createActivityRecord(@RequestBody ActivityRecordDTO activityRecordDTO) throws UserNotFoundException {

        try {
            activityRecordService.saveActivityRecord(activityRecordDTO);

            Optional<UserInformation> existingUserOpt = userInformationService.getUserInformationByUserId(activityRecordDTO.getUserId());

            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuário não encontrado com ID: " + activityRecordDTO.getUserId());
            }

            UserInformation existingUser = existingUserOpt.get();
            String email = existingUser.getEmail();
            String token = existingUser.getToken();

            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token necessário para verificação.");
            }

            try {
                azureDevOpsClient.getAzureUserIDByEmail(email, token);
            } catch (InvalidTokenException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido ou expirado.");
            } catch (UserNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuário não encontrado para o email fornecido.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(activityRecordDTO);

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (HttpTimeoutException e) {
            logger.warn("Azure DevOps timed out while validating the token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body("O Azure DevOps não respondeu a tempo.");
        } catch (AzureDevOpsApiException | IOException | URISyntaxException e) {
            logger.error("Azure DevOps call failed while creating an activity record", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Falha ao se comunicar com o Azure DevOps.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Requisição interrompida.");
        } catch (RuntimeException e) {
            logger.error("Unexpected error while creating an activity record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o registro de atividade.");
        }
    }

    @GetMapping("/byDate")
    public ResponseEntity <List<ActivityRecordResponseDTO>> getActivityRecordsByDate(
            @RequestParam Long userId,
            @RequestParam String date) {
        List<ActivityRecordResponseDTO> activityRecords = activityRecordService.getActivityRecordsByDate(userId, date);
        return new ResponseEntity<>(activityRecords, HttpStatus.OK);
    }

    @GetMapping("/byWorkItemId")
    public ResponseEntity<List<ActivityRecordResponseDTO>> getActivityRecordsByWorkItemID(
            @RequestParam Long userId,
            @RequestParam int workItemId) {
        List<ActivityRecordResponseDTO> activityRecords = activityRecordService.getActivityRecordsByWorkItemID(userId, workItemId);
        return new ResponseEntity<>(activityRecords, HttpStatus.OK);
    }
}

