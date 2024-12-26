package com.nudha.weatherapp;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import com.nudha.weatherapp.activities.LoginActivity;


public class UIAdaptiveTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testUIElementsOnDifferentScreens() {
        Espresso.onView(withId(R.id.icon))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.loginButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEditTextInputResponseTime(){
        long startTime = System.currentTimeMillis();

        Espresso.onView(withId(R.id.passwordEditText))
                .perform(ViewActions.typeText("password"));

        long endTime = System.currentTimeMillis();
        long inputTime = endTime - startTime;

        System.out.println("Input time: " + inputTime);

        assertTrue("Text input took too long", inputTime < 1000);
    }

    @Test
    public void textButtonClickResponseTime(){
        long startTime = System.currentTimeMillis();

        Espresso.onView(withId(R.id.loginButton))
                .perform(ViewActions.click());

        long endTime = System.currentTimeMillis();
        long clickTime = endTime - startTime;

        System.out.println("Click time: " + clickTime);

        assertTrue("Button click took too long", clickTime < 1000);
    }

    @Test
    public void testActivityLoadTime(){
        long startTime = System.currentTimeMillis();

        activityRule.launchActivity(new Intent());

        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        System.out.println("Activity load time: " + loadTime);

        assertTrue("Activity load time took too long", loadTime < 1000);
    }
}
