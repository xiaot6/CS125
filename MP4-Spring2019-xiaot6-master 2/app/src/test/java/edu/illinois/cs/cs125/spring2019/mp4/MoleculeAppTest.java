package edu.illinois.cs.cs125.spring2019.mp4;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class MoleculeAppTest {
    @Test
    public void testSharedPreferences() {
        MoleculeData.setTesting();
        MoleculeActivity activity = Robolectric.buildActivity(MoleculeActivity.class).create().start().get();
        Assert.assertEquals("If no SharedPreferences entry is present, molecule 0 should be default",
                0, activity.getCurrentMoleculeIndex());

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int moleculeIndex = random.nextInt(7);
            SharedPreferences prefs = ApplicationProvider.getApplicationContext().getSharedPreferences(
                    "CS125_MP4_Spring2019", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("LastMoleculeIndex", moleculeIndex);
            editor.apply();
            activity = Robolectric.buildActivity(MoleculeActivity.class).create().start().get();
            Assert.assertEquals("onCreate didn't load the last-viewed molecule index from SharedPreferences",
                    moleculeIndex, activity.getCurrentMoleculeIndex());
        }
    }
}
