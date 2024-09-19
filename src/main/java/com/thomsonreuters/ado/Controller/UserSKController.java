package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userSK")
public class UserSKController {
    private final AzureDevOpsClient azureDevOpsClient;

    public UserSKController(AzureDevOpsClient azureDevOpsClient) {
        this.azureDevOpsClient = azureDevOpsClient;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> getUserSKByEmail(@PathVariable String email) throws Exception {
        String userSK = azureDevOpsClient.getUserSKByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("userSK", userSK);
        return ResponseEntity.ok(response);
    }
}
