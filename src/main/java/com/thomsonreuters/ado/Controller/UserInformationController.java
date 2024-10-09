package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.EmailRequest;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Service.UserInformationService;
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

    @PostMapping("/azureUserID")
    public ResponseEntity<Map<String, String>> getAzureUserID(@RequestBody EmailRequest request) throws Exception {
        String azureUserID = azureDevOpsClient.getAzureUserIDByEmail(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("azureUserID", azureUserID);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/details")
    public ResponseEntity<UserInformation> getCurrentUserInformation(@RequestBody EmailRequest request) {
        try {
            UserInformation userInformation = userInformationService.getUserInformationByUserEmail(request.getEmail());
            if (userInformation != null) {
                return ResponseEntity.ok(userInformation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

