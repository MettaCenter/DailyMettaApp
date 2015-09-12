package org.mettacenter.dailymettaapp;

/**
 * Created by Latter
 */

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;

public class NavigationTest extends ActivityInstrumentationTestCase2{
    private Solo nRobotium;

    public NavigationTest () throws ClassNotFoundException {
        //type in package name, and class to launch
        super("org.mettacenter.dailymettaapp", ArticleActivityC.class);
    }

    public void setUp () throws Exception {
        super.setUp();
        nRobotium = new Solo(getInstrumentation(), getActivity());

    }

    public void testScroll (){
        nRobotium.assertCurrentActivity("Running correct class", ArticleActivityC.class);
        nRobotium.scrollDown();
        nRobotium.scrollUp();
    }

    //Unfinished
    public void testChangeAcitivity (){
        nRobotium.assertCurrentActivity("Running correct class", ArticleActivityC.class);
        nRobotium.clickOnButton("Calendar");

    }

}
