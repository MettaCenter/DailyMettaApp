package org.mettacenter.dailymettaapp;

/**
 * Created by sunyata o
 */

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;

@SuppressWarnings("rawtypes")
public class RobotiumExTest extends ActivityInstrumentationTestCase2 {
    private Solo mRobotium;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "org.mettacenter.dailymettaapp.ArticleActivityC";
    private static final String LAUNCHER_ACTIVITY_PARTIAL_CLASSNAME = "ArticleActivityC";

    private static Class<?> launcherActivityClass;
    static{
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public RobotiumExTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        mRobotium = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        mRobotium.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        //Wait for activity: 'com.example.ExampleActivty'
        mRobotium.waitForActivity(LAUNCHER_ACTIVITY_PARTIAL_CLASSNAME, 2000);

        mRobotium.scrollDown();

        /*
        //Clear the EditText editText1
        mRobotium.clearEditText((android.widget.EditText) mRobotium.getView("editText1"));
        mRobotium.enterText((android.widget.EditText) mRobotium.getView("editText1"), "This is an example text");
        */
    }
}