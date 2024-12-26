package com.nudha.weatherapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import com.nudha.weatherapp.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testLoginWithValidCredentials() throws InterruptedException {
        onView(withId(R.id.emailEditText)).perform(replaceText("nas5@gmail.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("Sn1zhe4ok!"));
        onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(2000);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        boolean toastFound = device.findObject(new UiSelector().textContains("Login successful!")).exists();

        assertTrue("Expected toast was not displayed.", toastFound);
    }

    @Test
    public void testLoginWithInvalidCredentials() throws InterruptedException {
        onView(withId(R.id.emailEditText)).perform(replaceText("invalid@example.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("wrongPass"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(2000);

        onView(withText("Incorrect login or password")).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterWithValidCredentials() throws InterruptedException {
        onView(withId(R.id.registerTextView)).perform(click());
        onView(withId(R.id.usernameEditText)).perform(replaceText("PiroGor"));
        onView(withId(R.id.emailEditText)).perform(replaceText("nas5@gmail.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("Sn1zhe4ok!"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(2000);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        boolean toastFound = device.findObject(new UiSelector().textContains("Registration successful!")).exists();

        assertTrue("Expected toast was not displayed.", toastFound);
    }

    @Test
    public void testRegisterWithInvalidUsername() throws InterruptedException {
        onView(withId(R.id.registerTextView)).perform(click());
        onView(withId(R.id.usernameEditText)).perform(replaceText("abc"));
        onView(withId(R.id.emailEditText)).perform(replaceText("nas5@gmail.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("Sn1zhe4ok!"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(2000);

        onView(withText("Incorrect name format")).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterWithInvalidPassword() throws InterruptedException {
        onView(withId(R.id.registerTextView)).perform(click());
        onView(withId(R.id.usernameEditText)).perform(replaceText("PiroGor"));
        onView(withId(R.id.emailEditText)).perform(replaceText("nas5@gmail.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("short"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(2000);

        onView(withText("Incorrect password format: minimum 8 characters, 1 digit and 1 special. symbol"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmailValidation() throws InterruptedException {
        onView(withId(R.id.emailEditText)).perform(replaceText("nas5@.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("Sn1zhe4ok!"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(2000);

        onView(withText("Incorrect email format")).check(matches(isDisplayed()));
    }
}
