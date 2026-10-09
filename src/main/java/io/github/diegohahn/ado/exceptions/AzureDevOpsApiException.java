package io.github.diegohahn.ado.exceptions;

/**
 * Raised when the Azure DevOps API answers with an unexpected status code.
 */
public class AzureDevOpsApiException extends Exception {
    public AzureDevOpsApiException(String message) {
        super(message);
    }
}
