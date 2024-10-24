package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.AzureUserIDRequest;
import com.thomsonreuters.ado.Model.UserInformation;
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

    @PostMapping("/azureUserID")
    public ResponseEntity<String> validateAndSaveAzureUserID(@RequestBody AzureUserIDRequest request) {
        try {
            String azureUserID = azureDevOpsClient.getAzureUserIDByEmail(request.getEmail(), request.getToken());
            UserInformation existingUser = userInformationService.getUserInformationByUserEmail(request.getEmail());

            if (existingUser == null) {
                UserInformation newUser = new UserInformation();
                newUser.setEmail(request.getEmail());
                newUser.setAzureUserID(azureUserID);
                newUser.setPersonalAccessToken(request.getToken());

                userInformationService.saveUserInformation(newUser);
            } else {
                existingUser.setAzureUserID(azureUserID);
                existingUser.setPersonalAccessToken(request.getToken());

                userInformationService.saveUserInformation(existingUser);
            }

            return ResponseEntity.ok("AzureUserID salvo com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
}