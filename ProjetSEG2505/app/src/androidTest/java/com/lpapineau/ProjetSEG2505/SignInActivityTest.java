package com.lpapineau.ProjetSEG2505;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.gms.tasks.Tasks.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.TextView;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignInActivityTest {
    @Rule
    public ActivityTestRule<LoginPage> mActivityTestRule = new ActivityTestRule<>(LoginPage.class);


    @Test
    public void check1PermanentDisabled() throws Exception{
        onView(withId(R.id.txtEmail)).perform(typeText("cuisinier4@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("testtest"), closeSoftKeyboard());

        onView(withId(R.id.btnSignin)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.txtxErrorLogin)).check(matches(withText("Le compte est disabled pour toujours...")));
    }

    @Test
    public void check2TemporarilyDisabled() throws Exception{
        onView(withId(R.id.txtEmail)).perform(typeText("cuisinier5@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("testtest"), closeSoftKeyboard());

        onView(withId(R.id.btnSignin)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.txtxErrorLogin)).check(matches(withText("Le compte est disabled jusqu'a: Tue Jan 31 22:40:10 EST 2023")));
    }

    @Test
    public void check3WrongUsernameOrPassword1() throws Exception{
        onView(withId(R.id.txtEmail)).perform(typeText("userexistepas@null.null"), closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("userexistepastest"), closeSoftKeyboard());

        onView(withId(R.id.btnSignin)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.txtxErrorLogin)).check(matches(withText("Mauvais username ou password!")));
    }


    @Test
    public void check4LoginCorrect() throws Exception{
        onView(withId(R.id.txtEmail)).perform(typeText("cuisinier6@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("testtest"), closeSoftKeyboard());

        onView(withId(R.id.btnSignin)).perform(click());

        onView(withId(R.id.txtxErrorLogin)).check(matches(withText("")));
    }
}
