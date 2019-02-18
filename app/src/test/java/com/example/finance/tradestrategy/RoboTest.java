package com.example.finance.tradestrategy;

import android.view.MenuItem;

import com.example.finance.tradestrategy.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import static org.junit.Assert.assertTrue;

/**
 * Created by Administrator on 2017/8/8.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RoboTest {

    MainActivity mainActivity;

    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
    }


    @Test
    public void turnOnService() {
        MenuItem startMenuItem = new RoboMenuItem(R.id.startOrStop);
        boolean result = mainActivity.onOptionsItemSelected(startMenuItem);

        assertTrue(result);
    }
}
