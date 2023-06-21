package com.example.backgroundlocationtracker

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var decorView: View

    @Before
    fun setUp() {
        activityRule.scenario.onActivity {
            decorView = it.window.decorView
        }
    }

    @Test
    fun onTapHello_ToastIsShown() {
        onView(withId(R.id.id_text_view)).perform(clickClickableSpan("Hello"))
        onView(withText("Click on Hello!"))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))
    }
}


