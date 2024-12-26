package com.nudha.weatherapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import com.nudha.weatherapp.activities.MainActivity;

@RunWith(AndroidJUnit4.class)
public class RecycleViewScrollTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testRecyclerViewSmoothScroll(){
        long startTime = System.currentTimeMillis();

        onView(withId(R.id.view1))
                .perform(RecyclerViewActions.scrollToPosition(10));

        long endTime = System.currentTimeMillis();
        long scrollTime = endTime - startTime;

        System.out.println("Scroll time: " + scrollTime);

        assertTrue("RecyclerView scroll took too long", scrollTime < 1000);
    }


}
