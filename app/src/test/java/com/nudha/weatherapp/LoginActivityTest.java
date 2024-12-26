package com.nudha.weatherapp;

import static org.junit.Assert.*;

import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nudha.weatherapp.activities.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.work.SystemClock;

import org.mockito.MockedStatic;

import java.util.regex.Pattern;

public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Mock
    private FirebaseAuth mockAuth;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginActivity = new LoginActivity();
        mockAuth = mock(FirebaseAuth.class);
        loginActivity.auth = mockAuth;
    }

    @Test
    public void testIsUsernameValid() {
        assertTrue(LoginActivity.check("username", "validUser")); // Valid username("validUser"));
        assertFalse(LoginActivity.check("username", "abs")); // Too short
        assertFalse(LoginActivity.check("username", "thisIsTooLongUsername")); // Too long
    }

    @Test
    public void testIsPasswordValid() {
        assertTrue(LoginActivity.check("password","Passw0rd!")); // Valid password
        assertFalse(LoginActivity.check("password", "password")); // Missing digit and special character
        assertFalse(LoginActivity.check("password","12345678")); // Missing special character
        assertFalse(LoginActivity.check("password","pass!")); // Too short
    }

    @Test
    public void testIsEmailValid() {
        assertTrue(LoginActivity.check("email","test@example.com")); // Valid email
        assertFalse(LoginActivity.check("email","invalidEmail")); // Missing @ and domain
        assertFalse(LoginActivity.check("email","test@.com")); // Missing domain name
        assertFalse(LoginActivity.check("email","@example.com")); // Missing local part
    }

    @Test
    public void testRegisterUser_Success() {
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);

        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenAnswer(invocation -> {
            captor.capture();
            return mockTask;
        });

        loginActivity.auth.createUserWithEmailAndPassword("test@example.com", "Passw0rd!");

        captor.getValue().onComplete(mockTask);

        verify(mockAuth).createUserWithEmailAndPassword("test@example.com", "Passw0rd!");
        assertTrue(mockTask.isSuccessful());
    }

    @Test
    public void testLoginUser_Success() {
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);

        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenAnswer(invocation -> {
            captor.capture();
            return mockTask;
        });

        loginActivity.auth.signInWithEmailAndPassword("test@example.com", "Passw0rd!");

        captor.getValue().onComplete(mockTask);

        verify(mockAuth).signInWithEmailAndPassword("test@example.com", "Passw0rd!");
        assertTrue(mockTask.isSuccessful());
    }

    @Test
    public void testLoginUser_Failure() {
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(false);

        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);

        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenAnswer(invocation -> {
            captor.capture();
            return mockTask;
        });

        loginActivity.auth.signInWithEmailAndPassword("test@example.com", "wrongPassword");

        captor.getValue().onComplete(mockTask);

        verify(mockAuth).signInWithEmailAndPassword("test@example.com", "wrongPassword");
        assertFalse(mockTask.isSuccessful());
    }
}
