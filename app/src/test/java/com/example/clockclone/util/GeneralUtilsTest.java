package com.example.clockclone.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GeneralUtilsTest {

    ArrayList<Integer> snoozeTestVector = new ArrayList<>();
    ArrayList<String> snoozeTestExpected = new ArrayList<>();
    @Before
    public void setUp() throws Exception {

        snoozeTestVector.add(Constants.Snooze.SNOOZE_FLAG_ENABLED | Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES | Constants.Snooze.SNOOZE_REPEAT_3_TIMES);
        snoozeTestExpected.add("5 minutes, 3 times");

        snoozeTestVector.add(0 | Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES | Constants.Snooze.SNOOZE_REPEAT_FOREVER);
        snoozeTestExpected.add("Off");

        snoozeTestVector.add(Constants.Snooze.SNOOZE_FLAG_ENABLED | Constants.Snooze.SNOOZE_INTERVAL_30_MINUTES | Constants.Snooze.SNOOZE_REPEAT_FOREVER);
        snoozeTestExpected.add("30 minutes, Forever");
    }

    @Test
    public void snoozeIntToString() {
        int length = snoozeTestVector.size();
        for(int i = 0; i < length; i++) {
            int snoozeSetting = snoozeTestVector.get(i);
            assertEquals(snoozeTestExpected.get(i), GeneralUtils.snoozeIntToString(snoozeSetting));
        }
    }
}