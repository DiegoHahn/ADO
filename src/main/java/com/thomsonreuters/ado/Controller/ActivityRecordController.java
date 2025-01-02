package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Exceptions.InvalidTokenException;
import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.ActivityRecordResponseDTO;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Service.ActivityRecordService;
import com.thomsonreuters.ado.Service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/activityRecord")
public class ActivityRecordController {

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

        } catch (Exception e) {
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

