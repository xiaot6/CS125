package edu.illinois.cs.cs125.spring2019.mp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.illinois.cs.cs125.spring2019.mp2.lib.ConnectN;

/**
 * The initial activity that configures the game.
 * <p>
 * MP2 provides our first example of an app with <em>multiple</em> Activitys, each representing a different screen.
 * When the app launches we open the SetupActivity Activity, which solicits initial game configuration parameters
 * from the user. It then launches the GameActivity which is responsible for soliciting moves and rendering the board
 * as game play proceeds.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 */
public final class SetupActivity extends AppCompatActivity {

    /** Tag for logging. Differentiate app output by Activity. */
    private static final String TAG = "MP2:Setup";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout and title for this Activity.
        setContentView(R.layout.activity_setup);
        setTitle(R.string.setup_screen_title);

        /*
         * Don't show the start button until the parameters are valid.
         *
         * The following code sets up a TextWatcher to monitor the width, height, and n fields in the UI. Once they
         * are set to a valid combination of values, we show the start game button. Until that point that button is
         * hidden.
         *
         * From a user interface perspective this is more intuitive than having a button which can fail when it is
         * pressed. Essentially we don't show the start button until the user has configured valid parameters. Until
         * they do we also show an invalid parameters dialog so that the user knows what is wrong.
         */
        TextWatcher textWatcher = new TextWatcher() {
            // Unused parts of the TextWatcher class.
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) { }
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) { }

            @Override
            public void afterTextChanged(final Editable s) {
                /*
                 * Each time the width, height, or n values are changed we check to see if they represent a valid
                 * combination of values. If they do, we enable the start button. Otherwise, we show the invalid
                 * parameters message.
                 */
                Log.i(TAG, "Text changed");
                Button startButton = findViewById(R.id.start_button);
                TextView errorNotice = findViewById(R.id.invalid_setup_label);
                if (validSetup()) {
                    startButton.setEnabled(true);
                    errorNotice.setVisibility(View.GONE);
                } else {
                    startButton.setEnabled(false);
                    errorNotice.setVisibility(View.VISIBLE);
                }
            }
        };
        /*
         * Attach the TextWatcher to the board width, height, and n input fields.
         */
        for (int id : new int[]{R.id.edit_board_width, R.id.edit_board_height, R.id.edit_board_n}) {
            ((EditText) findViewById(id)).addTextChangedListener(textWatcher);
        }

        /*
         * Set up our listener for the start game button.
         */
        findViewById(R.id.start_button).setOnClickListener(v -> startGame());
    }

    /**
     * Convenience method for getting the text in an edit control.
     *
     * @param editor the ID of the edit control.
     * @return the text in the edit control.
     */
    String getTextIn(final int editor) {
        return ((EditText) findViewById(editor)).getText().toString();
    }

    /**
     * Convenience method for getting the number in an edit control.
     *
     * @param editor the ID of the edit control.
     * @return the number in the edit control.
     */
    private int getNumberIn(final int editor) {
        return Integer.parseInt(getTextIn(editor));
    }

    /**
     * Checks the board setup part of the configuration.
     *
     * @return Whether the configuration is valid for ConnectN.
     */
    boolean validSetup() {
        Log.i(TAG, "Testing setup validity");
        try {
            int width = getNumberIn(R.id.edit_board_width);
            int height = getNumberIn(R.id.edit_board_height);
            int n = getNumberIn(R.id.edit_board_n);

            /*
             * We check for validity using the ConnectN factory constructor, which should return null if the combination
             * of parameters is invalid.
             */
            return ConnectN.create(width, height, n) != null;
        } catch (Exception e) {
            // This probably means we couldn't parse a text field as a number. Return false in that case.
            return false;
        }
    }

    /**
     * Starts the game by launching GameActivity with the board setup in the Intent's extras.
     */
    void startGame() {

        /*
         * In Android we launch another screen using a so-called <em>Intent</em>. The Intent has to be configured
         * with which Activity we want to launch: in this case GameActivity.class.
         */
        Intent intent = new Intent(this, GameActivity.class);

        /*
         * Frequently when we launch another screen we also want to pass some extra information, similar to how we
         * would when calling a function. Intents allow us to add extra fields with custom names and information. As
         * long as the sender and receiver of the Intent agree on the name and format of these fields, we can use
         * this to pass certain types of information similar to how we would using function arguments.
         *
         * Here we use the extra fields of the intent to tell the game activity how the game has been configured:
         * what the width, height, and n value are, and the names of the two players.
         */
        intent.putExtra("width", getNumberIn(R.id.edit_board_width));
        intent.putExtra("height", getNumberIn(R.id.edit_board_height));
        intent.putExtra("n", getNumberIn(R.id.edit_board_n));
        intent.putExtra("player1", getTextIn(R.id.edit_player1_name));
        intent.putExtra("player2", getTextIn(R.id.edit_player2_name));

        // Actually start the GameActivity Activity, causing that screen to launch.
        startActivity(intent);

        /*
         * At that point the SetupActivity is no longer needed and can exit. In Android we do this using finish,
         * which cleans up and then destroys this activity.
         */
        finish();
    }
}
