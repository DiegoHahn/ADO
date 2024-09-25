package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
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

    @GetMapping("/userSK/{email}")
    public ResponseEntity<Map<String, String>> getUserSKByEmail(@PathVariable String email) throws Exception {
        String userSK = azureDevOpsClient.getUserSKByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("userSK", userSK);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserInformation> getCurrentUserInformation() {
        // como obter o usuário atual? um token de autenticação, uma sessão? LocalStorage?
        try {
            String userSK = "5cfc3fc9-fb2c-4809-9586-44ffce7c24ca"; // Temporário
            UserInformation userInformation = userInformationService.getUserInformationByUserSK(userSK);
            return ResponseEntity.ok(userInformation);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
