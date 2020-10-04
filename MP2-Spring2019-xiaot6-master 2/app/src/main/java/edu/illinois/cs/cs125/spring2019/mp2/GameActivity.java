package edu.illinois.cs.cs125.spring2019.mp2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import edu.illinois.cs.cs125.spring2019.mp2.lib.ConnectN;
import edu.illinois.cs.cs125.spring2019.mp2.lib.Player;

/**
 * The main activity that renders the ConnectN game and solicits moves from users.
 * <p>
 * MP2 provides our first example of an app with <em>multiple</em> Activitys, each representing a different screen.
 * When the app launches we open the SetupActivity Activity, which solicits initial game configuration parameters
 * from the user. It then launches the GameActivity which is responsible for soliciting moves and rendering the board
 * as game play proceeds.
 * <p>
 * The code below is <em>mostly</em> working, but as usual you have a few small changes to make before you code will
 * pass all of the test cases.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 */
public final class GameActivity extends AppCompatActivity {

    /** Tag for logging. Differentiate app output by Activity. */
    private static final String TAG = "MP2:Game";

    /*
     * Constants.
     */

    /** How many players can play the game. */
    private static final int NUM_PLAYERS = 2;

    /** How wide the tile borders are relative to the size of a tile. */
    private static final double TILE_BORDER_WIDTH_MULTIPLIER = 0.1;

    /*
     * Interface-related private variables.
     */

    /** The LinearLayout that contains all the UI for this screen. */
    private View gameContainer;

    /** The GridLayout that represents the board. */
    private GridLayout board;

    /** The ImageViews in the cells of the GridLayout. */
    private ImageView[][] tiles;

    /** The image for empty slots. */
    private GradientDrawable emptyTile;

    /** Images for the players' tiles. */
    private GradientDrawable[] playerPieces;

    /** IDs of the color resource for each player's tiles. */
    private int[] playerColorIds = {R.color.player1Color, R.color.player2Color};

    /** IDs of the color resource for the border of each player's tiles. */
    private int[] playerBorderColorIds = {R.color.player1BorderColor, R.color.player2BorderColor};

    /** The "to play" TextView for each player. */
    private View[] toPlayLabels;

    /** The label that displays the winner. */
    private TextView winnerLabel;

    /*
     * Game state related private variables.
     */

    /** Which player's turn it is: 0 for player 1, 1 for player 2. */
    private int playerToMove = 0;

    /** The ConnectN instance managing this game. */
    private ConnectN game;

    /** The two Player instances. */
    private Player[] players;

    /** The list of moves. Used to store the game state so that it can be reconstituted if the Activity is restarted. */
    private ArrayList<Integer> successfulPlays = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.activity_game);

        /*
         * Extract game parameters from the Intent that launched the activity.
         *
         * This information is passed to this activity from the Setup activity defined in SetupActivity.java. This is
         * a good example of how to use multiple activities in a single app.
         *
         * See startGame in SetupActivity to see where these parameters get set.
         */
        Intent intent = getIntent();
        game = new ConnectN(intent.getIntExtra("width", 0),
                intent.getIntExtra("height", 0),
                intent.getIntExtra("n", 0));
        // Set the title appropriately based on how many tiles are needed to win
        setTitle(getResources().getString(R.string.game_screen_title, game.getN()));

        // Create player instances
        players = new Player[NUM_PLAYERS];
        players[0] = new Player(intent.getStringExtra("player1"));
        players[1] = new Player(intent.getStringExtra("player2"));

        /*
         * Initialize fields for UI elements.
         *
         * We set these as private instance variables so that they are available throughout the rest of the Activity.
         */
        gameContainer = findViewById(R.id.game_container);
        board = findViewById(R.id.board);
        toPlayLabels = new View[NUM_PLAYERS];
        toPlayLabels[0] = findViewById(R.id.player1_turn);
        toPlayLabels[1] = findViewById(R.id.player2_turn);
        winnerLabel = findViewById(R.id.winner);
        winnerLabel.setVisibility(View.GONE);

        // Add button handlers
        findViewById(R.id.new_game_button).setOnClickListener(v -> {
            game = new ConnectN(game);
            setupBoard();
            playerToMove = 0;
            successfulPlays.clear();
            updateDisplay();
        });

        /*
         * The return to setup button also launches an Intent, but this one is used to bring is back to the
         * SetupActivity defined in SetupActivity.java.
         */
        findViewById(R.id.back_setup_button).setOnClickListener(v -> {
            Intent setupIntent = new Intent(this, SetupActivity.class);
            startActivity(setupIntent);
            finish();
        });

        /*
         * Register an observer to get notified when view sizes are available.
         *
         * The purpose of this code is to respond appropriately when the size of the display changes so that we can
         * redraw the board appropriately.
         */
        final ViewTreeObserver viewTreeObserver = gameContainer.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onReadyForSizing();
            }
        });
    }

    /*
     * The following two functions demonstrate how to save state across the Activity lifecycle.
     *
     * This allows a part of the app to remember things that happened to it so that when it reappears it can adjust
     * the UI accordingly. In our case we save the state of the game and restore it when the GameActivity is restored.
     */

    /**
     * Saves the game state to a Bundle when the activity instance is being destroyed.
     * <p>
     * Activity instances actually have a shorter lifetime than one would expect from
     * using an Android app. When the screen is rotated, for example, the activity is destroyed
     * and recreated. The state of some UI controls like on the setup screen is automatically
     * saved and restored, but it's up to the application to reconstitute any Java variables.
     * <p>
     * We save the list of moves made and the pre-game scores.
     *
     * @param outState the Bundle to write to.
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("game", successfulPlays);
        int[] preGameScores = new int[NUM_PLAYERS];
        for (int p = 0; p < preGameScores.length; p++) {
            preGameScores[p] = players[p].getScore();
            if (getPlayerIndex(game.getWinner()) == p) {
                // Decrementing the winner's score calculates its pre-game score
                // This is needed because the playback will cause the player to "win" again
                preGameScores[p]--;
            }
        }
        outState.putIntArray("scores", preGameScores);
    }

    /**
     * Restores the game (player scores and moves made) from a Bundle.
     * <p>
     * Players are reconstituted first so that the ConnectN instance and app are referring to
     * the same player instances.
     *
     * @param savedInstanceState the Bundle that was written before the activity was last terminated.
     */
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int[] preGameScores = savedInstanceState.getIntArray("scores");
        assert preGameScores != null;
        for (int p = 0; p < preGameScores.length; p++) {
            players[p] = new Player(players[p].getName(), preGameScores[p]);
        }
        for (Integer idx : Objects.requireNonNull(savedInstanceState.getIntegerArrayList("game"))) {
            int y = idx / game.getWidth();
            int x = idx % game.getWidth();
            tryPlayAt(x, y);
        }
    }

    /**
     * Called when the view hierarchy has been created and sizes are available.
     * <p>
     * This function dynamically creates the layout grid depending on the size of the current display. One approach
     * to UI design is to create a static layout using the layout designer, but that would not allow us to support
     * multiple board sizes. Instead, this function dynamically creates the layout elements of appropriate size
     * depending on the size of the display and the board configuration.
     */
    void onReadyForSizing() {
        // Figure out how large a tile should be. This is some rough math, but it works!
        Log.i(TAG, String.format("Layout is %d by %d", gameContainer.getWidth(), gameContainer.getHeight()));
        int bottomSpace = findViewById(R.id.game_control_container).getBottom()
                - findViewById(R.id.players_info_container).getTop();
        double horizontalConstraint = gameContainer.getWidth() / (double) game.getWidth();
        double verticalConstraint = (gameContainer.getHeight() - bottomSpace) / (double) game.getHeight();
        int tileSize = (int) Math.floor(Math.min(horizontalConstraint, verticalConstraint));
        Log.i(TAG, String.format("Tiles are %d pixels", tileSize));
        int tileBorderWidth = (int) (tileSize * TILE_BORDER_WIDTH_MULTIPLIER);

        // Create drawables for tiles.
        emptyTile = new GradientDrawable();
        emptyTile.setShape(GradientDrawable.OVAL);
        emptyTile.setSize(tileSize, tileSize);
        emptyTile.setStroke(2, Color.GRAY);
        playerPieces = new GradientDrawable[NUM_PLAYERS];
        for (int p = 0; p < playerPieces.length; p++) {
            playerPieces[p] = new GradientDrawable();
            playerPieces[p].setShape(GradientDrawable.OVAL);
            playerPieces[p].setSize(tileSize, tileSize);
            playerPieces[p].setStroke(tileBorderWidth, getResources().getColor(playerBorderColorIds[p], getTheme()));
            playerPieces[p].setColor(getResources().getColor(playerColorIds[p], getTheme()));
        }

        // Render the board.
        setupBoard();
        updateDisplay();
    }

    /**
     * Sets up the board GridLayout for a new game.
     * <p>
     * At this point no pieces have been played.
     */
    void setupBoard() {
        Log.i(TAG, "setupBoard");

        // Clear the existing board.
        board.removeAllViews();

        // Configure the GridLayout that represents the board.
        board.setColumnCount(game.getWidth());
        board.setRowCount(game.getHeight());

        /*
         * Set up a 2D array of ImageViews with the same dimensions as the board. These are initially set to the
         * empty space image and then get filled in as the game progresses.
         */
        tiles = new ImageView[game.getWidth()][game.getHeight()];

        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                // Configure the location within the GridLayout.
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.columnSpec = GridLayout.spec(x);
                layoutParams.rowSpec = GridLayout.spec(y);

                // Create an image view for this cell using our empty tile image and add it to the layout.
                ImageView cell = new ImageView(this);
                cell.setImageDrawable(emptyTile);
                cell.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                board.addView(cell, layoutParams);

                // Game coordinates are different than tile coordinates, so carefully set up listener for this cell.
                final int gameX = x;
                final int gameY = game.getHeight() - y - 1;
                cell.setOnClickListener(v -> tileClicked(gameX, gameY));
                tiles[gameX][gameY] = cell;
            }
        }
    }
    /**
     * the code I create.
     * @param setX - x value
     * @param setY - y value
     *
     *
     */
    void tileClicked(final int setX, final int setY) {
        tryPlayAt(setX, setY);
        updateDisplay();

    }

    /**
     * Gets the array index for the player who is not currently to move.
     *
     * @return 0 if it's Player 2's turn now, 1 otherwise.
     */
    int getPlayerNotMoving() {
        if (playerToMove == 1) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Gets the array index for the given player.
     *
     * @param player the Player instance.
     * @return the index for use in various arrays.
     */
    int getPlayerIndex(final Player player) {
        if (player == null) {
            return -1;
        }
        for (int p = 0; p < players.length; p++) {
            if (player.equals(players[p])) {
                return p;
            }
        }
        return -1;
    }

    /**
     * Tries to make the play that results from clicking the tile at the given coordinates.
     * <p>
     * Successful plays are recorded so that the game can be replayed when the activity is restarted.
     * <p>
     * This function <i>should not</i> update the UI, since it can be called before sizing is ready.
     *
     * @param x the move's x position in the game.
     * @param y the move's y position in the game.
     * @return Whether the play was successful.
     */
    boolean tryPlayAt(final int x, final int y) {
        Player toPlay = players[playerToMove];

        boolean didPlay = game.setBoardAt(toPlay, x, y);
        if (!didPlay && y == game.getHeight() - 1) {
            // The move is in the top drop-in row, so don't specify a y position.
            didPlay = game.setBoardAt(toPlay, x);
        }

        if (didPlay) {
            // Swap players.
            playerToMove = getPlayerNotMoving();

            // Record the move for later playback.
            successfulPlays.add(y * game.getWidth() + x);
        }

        return didPlay;
    }

    /**
     * Updates the UI according to the game state.
     */
    void updateDisplay() {
        /*
         * Loop through the board and redraw all tiles to reflect which player has played there.
         */
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                Player playedHere = game.getBoardAt(x, y);
                // If no player has moved here we don't need to redraw the base board.
                if (playedHere == null) {
                    continue;
                }

                // Otherwise figure out which player moved here and redraw the board appropriately.
                for (int p = 0; p < players.length; p++) {
                    if (playedHere.equals(players[p])) {
                        tiles[x][y].setImageDrawable(playerPieces[p]);
                    }
                }
            }
        }

        /*
         * Update the winner and who's turn is next.
         */
        Player winner = game.getWinner();
        if (winner == null) {
            /*
             * The game continues!
             *
             * Adjust the UI so that the player to play next is visible and make sure that the winner label is not
             * visible.
             */
            toPlayLabels[playerToMove].setVisibility(View.VISIBLE);
            toPlayLabels[getPlayerNotMoving()].setVisibility(View.GONE);
        } else {
            /*
             * The game is over!
             *
             * Make all the to play labels invisible, make the winner label visible, set the text to "(Player's
             * name) wins!", and set the text color of the winner label to the appropriate color depending on who won.
             */
            winnerLabel.setText(winner.getName() + " wins!");
            winnerLabel.setVisibility(View.VISIBLE);
            winnerLabel.setTextColor(getResources().getColor(playerColorIds[getPlayerIndex(winner)], getTheme()));
            for (View v : toPlayLabels) {
                v.setVisibility(View.GONE);
            }
        }

        // Update player scores.
        ((TextView) findViewById(R.id.player1)).setText(
                getResources().getString(R.string.player1_info_text, players[0].getName(), players[0].getScore()));
        ((TextView) findViewById(R.id.player2)).setText(
                getResources().getString(R.string.player2_info_text, players[1].getName(), players[1].getScore()));
    }

    /*
     * The following methods are used during testing only.
     */

    /**
     * Gets the ConnectN instance managing this game.
     * <p>
     * For testing purposes only.
     *
     * @return the ConnectN game.
     */
    public ConnectN getGame() {
        return game;
    }

    /**
     * Sets the game manager for this activity.
     * <p>
     * For testing purposes only.
     *
     * @param setGame the new ConnectN instance.
     */
    public void setGame(final ConnectN setGame) {
        game = setGame;
    }

    /**
     * Gets the Player instance for the given index.
     * <p>
     * For testing purposes only.
     *
     * @param index 0 for the first player, 1 for the second.
     * @return the desired Player instance.
     */
    public Player getPlayer(final int index) {
        return players[index];
    }
}
