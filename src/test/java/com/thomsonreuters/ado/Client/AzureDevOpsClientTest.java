package com.thomsonreuters.ado.Client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Exceptions.InvalidTokenException;
import com.thomsonreuters.ado.Exceptions.UserNotFoundException;

@SuppressWarnings("unchecked")
class AzureDevOpsClientTest {

    private AzureDevOpsAuthenticator authenticator;
    private HttpClient httpClient;
    private AzureDevOpsClient azureDevOpsClient;

    @BeforeEach
    void setUp() throws Exception {
        authenticator = mock(AzureDevOpsAuthenticator.class);
        httpClient = mock(HttpClient.class);
        azureDevOpsClient = new AzureDevOpsClient(
                "https://dev.azure.com/fakeOrganization",
                authenticator,
                "https://analytics.dev.azure.com/fakeOrganization"
        );

        Field clientField = AzureDevOpsClient.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(azureDevOpsClient, httpClient);
    }

    @Test
    void testGetWorkItemsSuccess() throws Exception {
        when(authenticator.getLocalAzureUserID(123L)).thenReturn("456");
        when(authenticator.getAuthHeaderById(123L)).thenReturn("Basic fakeAuthHeader");

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{ \"workItems\": [] }");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        String response = azureDevOpsClient.getWorItems("789", 123L, "/fakeBoard");
        assertEquals("{ \"workItems\": [] }", response);
    }

    @Test
    void testGetWorkItemsTimeout() throws Exception {
        when(authenticator.getLocalAzureUserID(anyLong())).thenReturn("456");
        when(authenticator.getAuthHeaderById(anyLong())).thenReturn("Basic fakeAuthHeader");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new HttpTimeoutException("Timeout"));

        assertThrows(HttpTimeoutException.class, () ->
                azureDevOpsClient.getWorItems("789", 123L, "/fakeBoard"));
    }

    @Test
    void testGetWorkItemsOtherException() throws Exception {
        when(authenticator.getLocalAzureUserID(123L)).thenReturn("456");
        when(authenticator.getAuthHeaderById(anyLong())).thenReturn("Basic fakeAuthHeader");

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("Failed");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(Exception.class, () ->
                azureDevOpsClient.getWorItems("789", 123L, "/fakeBoard"));

        assertEquals("Failed to execute WIQL query: Failed", exception.getMessage());
    }

    @Test
    void testGetAzureUserIDByEmailSuccess() throws Exception {
        String fakeResponse = "{ \"value\": [ { \"UserSK\": \"999\" } ] }";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fakeResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String userId = azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken");
        assertEquals("999", userId);
    }

    @Test
    void testGetAzureUserIDByEmailNotFound() throws Exception {
        String fakeResponse = "{ \"value\": [] }";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fakeResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertThrows(UserNotFoundException.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));
    }

    @Test
    void testGetAzureUserIDByEmailInvalidToken() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(401);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertThrows(InvalidTokenException.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));
    }

    @Test
    void testGetAzureUserIDByEmailOtherException() throws Exception {
        when(authenticator.getAuthHeaderByEmail(anyString())).thenReturn("Basic fakeAuthHeader");
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("Failed");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(Exception.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));

        assertEquals("Falha ao recuperar o AzureUserID: Failed", exception.getMessage());
    }

    @Test
    void testGetAzureUserIDByEmailEmptyUserSK() throws Exception {
        String fakeResponse = "{ \"value\": [ { \"UserSK\": \"\" } ] }";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fakeResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertThrows(UserNotFoundException.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));
    }

    @Test
    void testGetAzureUserIDByEmailValueNotArray() throws Exception {
        String fakeResponse = "{ \"value\": \"notAnArray\" }";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fakeResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertThrows(UserNotFoundException.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));
    }

    @Test
    void testGetAzureUserIDByEmailTimeout() throws Exception {
        when(authenticator.getAuthHeaderByEmail(anyString())).thenReturn("Basic fakeAuthHeader");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new HttpTimeoutException("Timeout"));

        HttpTimeoutException exception = assertThrows(HttpTimeoutException.class, () ->
                azureDevOpsClient.getAzureUserIDByEmail("test@example.com", "fakeToken"));

        assertEquals("Request to Azure DevOps API timed out.", exception.getMessage());
    }

    @Test
    void testUpdateWorkItemSuccess() throws Exception {
        when(authenticator.getAuthHeaderById(123L)).thenReturn("Basic fakeAuthHeader");
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("OK");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() ->
                azureDevOpsClient.updateWorkItem(100, "[{}]", 123L, "/fakeBoard"));

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(1)).send(captor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest sentRequest = captor.getValue();
        assertEquals("PATCH", sentRequest.method());
        assertTrue(sentRequest.uri().toString().contains("/_apis/wit/workitems/100?api-version=7.0"));
    }

    @Test
    void testUpdateWorkItemFailure() throws Exception {
        when(authenticator.getAuthHeaderById(123L)).thenReturn("Basic fakeAuthHeader");
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("Failed");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception ex = assertThrows(Exception.class, () ->
                azureDevOpsClient.updateWorkItem(100, "[{}]", 123L, "/fakeBoard"));
        assertTrue(ex.getMessage().contains("Failed to execute WIQL query"));
    }

    @Test
    void testUpdateWorkItemQueryCompletedAndRemaining() {
    double remainingWork = 3.5;
    double completedWork = 1.2;
    String expectedJson = """
        [
            {
                "op": "add",
                "path": "/fields/Microsoft.VSTS.Scheduling.RemainingWork",
                "value": 3.50
            },
            {
                "op": "add",
                "path": "/fields/Microsoft.VSTS.Scheduling.CompletedWork",
                "value": 1.20
            }
        ]
        """.stripIndent();

        String actualJson = AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(remainingWork, completedWork);
        assertEquals(expectedJson, actualJson);
        }

        @Test
        void testUpdateWorkItemQueryCompleted() {
            double completedWork = 4.5;
            String expectedJson = """
                [
                    {
                        "op": "add",
                        "path": "/fields/Microsoft.VSTS.Scheduling.CompletedWork",
                        "value": 4.50
                    }
                ]
                """.stripIndent();
        
            String actualJson = AzureDevOpsClient.UpdateWorkItemQueryCompleted(completedWork);
            assertEquals(expectedJson, actualJson);
        }
}