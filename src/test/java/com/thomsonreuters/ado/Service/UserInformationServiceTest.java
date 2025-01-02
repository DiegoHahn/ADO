package com.thomsonreuters.ado.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;

@ExtendWith(MockitoExtension.class)
public class UserInformationServiceTest {

    @Mock
    private UserInformationRepository userInformationRepository;

    @InjectMocks
    private UserInformationService userInformationService;

    @Test
    void testGetUserInformationByUserEmail() {
        String email = "test@example.com";
        UserInformation user = new UserInformation();
        when(userInformationRepository.findByEmail(email)).thenReturn(user);

        UserInformation result = userInformationService.getUserInformationByUserEmail(email);
        assertNotNull(result);
        verify(userInformationRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserInformationByUserId() {
        Long userId = 1L;
        UserInformation user = new UserInformation();
        when(userInformationRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<UserInformation> result = userInformationService.getUserInformationByUserId(userId);
        assertTrue(result.isPresent());
        verify(userInformationRepository, times(1)).findById(userId);
    }

    @Test
    void testSaveUserInformation() {
        UserInformation user = new UserInformation();
        when(userInformationRepository.save(user)).thenReturn(user);

        UserInformation result = userInformationService.saveUserInformation(user);
        assertNotNull(result);
        verify(userInformationRepository, times(1)).save(user);
    }
}