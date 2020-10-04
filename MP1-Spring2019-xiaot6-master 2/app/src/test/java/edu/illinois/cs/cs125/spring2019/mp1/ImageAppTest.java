package edu.illinois.cs.cs125.spring2019.mp1;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import edu.illinois.cs.cs125.spring2019.mp1.lib.RGBAPixel;
import edu.illinois.cs.cs125.spring2019.mp1.lib.Transform;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Transform.class})
public class ImageAppTest {

    // This allows us to mock static classes
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testFlipButtons() {
        // Instrument the Transform class
        PowerMockito.mockStatic(Transform.class);
        int[] calls = {0, 0};
        Mockito
            .when(Transform.flipHorizontal(Mockito.any()))
            .then(invocation -> {
                calls[0]++;
                return invocation.getArgumentAt(0, RGBAPixel[][].class);
            });
        Mockito
            .when(Transform.flipVertical(Mockito.any()))
            .then(invocation -> {
                calls[1]++;
                return invocation.getArgumentAt(0, RGBAPixel[][].class);
            });

        // Set up the activity
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        activity.setForegroundBitmap(BitmapFactory.decodeResource(
            activity.getApplicationContext().getResources(), R.drawable.cornfield_background)
        );

        // Click the flip-horizontal button
        activity.findViewById(R.id.flipHorizontal).performClick();
        Robolectric.flushBackgroundThreadScheduler();
        Assert.assertEquals("The flip-horizontal button isn't working", 1, calls[0]);

        // Click the flip-vertical button
        activity.findViewById(R.id.flipVertical).performClick();
        Robolectric.flushBackgroundThreadScheduler();
        Assert.assertEquals("The flip-vertical button isn't working", 1, calls[1]);
    }

    @Test
    public void testExpandIcon() {
        // Start the activity
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        // Make sure the expand button has the correct icon
        ImageButton expandButton = activity.findViewById(R.id.expand);
        Drawable buttonIcon = expandButton.getDrawable();
        Assert.assertNotNull("The expand button has no icon", buttonIcon);
        Assert.assertEquals("The expand button has the wrong icon",
                R.drawable.ic_open_with_black_24dp, Shadows.shadowOf(buttonIcon).getCreatedFromResId()
        );
    }

}
