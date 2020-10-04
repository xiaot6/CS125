package edu.illinois.cs.cs125.spring2019.mp2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;

import edu.illinois.cs.cs125.spring2019.mp2.lib.ConnectN;

/**
 * Test suite for the ConnectN app.
 * <p>
 * For these tests to function properly, ConnectN's constructors, dimension getters, and N getter
 * need to be implemented. Other functionality, including setBoardAt and getWinner, is mocked
 * here, so you do not need to have those functions working to pass the app tests.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public class GameAppTest {

    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 6;
    private static final int BOARD_N = 4;

    /**
     * Creates and starts a GameActivity for testing.
     * @param savedState The Bundle that the activity will load game state from.
     * @param player1Name Player 1's name.
     * @param player2Name Player 2's name.
     * @return A started activity.
     */
    GameActivity startActivity(final Bundle savedState, final String player1Name, final String player2Name) {
        Intent intent = new Intent();
        intent.putExtra("width", BOARD_WIDTH);
        intent.putExtra("height", BOARD_HEIGHT);
        intent.putExtra("n", BOARD_N);
        intent.putExtra("player1", player1Name);
        intent.putExtra("player2", player2Name);
        ActivityController<GameActivity> activityController = //
                Robolectric.buildActivity(GameActivity.class, intent).create(savedState).start();
        if (savedState != null) {
            activityController = activityController.restoreInstanceState(savedState);
        }
        return activityController.get();
    }

    /**
     * Creates the bundle used by {@link #startActivity(Bundle, String, String) startActivity}.
     * @param plays The positions of the successful plays so far.
     * @return A bundle representing the game.
     */
    Bundle createGameStateBundle(int... plays) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("scores", new int[2]);
        ArrayList<Integer> playsList = new ArrayList<>();
        for (int p : plays) {
            playsList.add(p);
        }
        bundle.putIntegerArrayList("game", playsList);
        return bundle;
    }

    /**
     * Triggers the "on ready for sizing" notification, which leads to the UI updating.
     * @param activity The GameActivity to notify.
     */
    void fireLayout(GameActivity activity) {
        activity.findViewById(R.id.game_container).getViewTreeObserver().dispatchOnGlobalLayout();
    }

    @Test
    public void testTileClicks() throws IllegalAccessException {
        // Set up the activity and board
        GameActivity activity = startActivity(null, "Rebo", "Zooty");
        GridLayout board = activity.findViewById(R.id.board);
        fireLayout(activity);

        // Instrument setBoardAt
        ConnectN spy = PowerMockito.spy(activity.getGame());
        int[] lastSetBoardAtCoords = {-1, -1}; // X, Y
        Mockito.when(spy.setBoardAt(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(iom -> {
            lastSetBoardAtCoords[0] = iom.getArgumentAt(1, int.class);
            lastSetBoardAtCoords[1] = iom.getArgumentAt(2, int.class);
            return true;
        });
        Mockito.when(spy.getBoardAt(0, 0)).thenReturn(activity.getPlayer(0));
        activity.setGame(spy);

        // Test clicking on the lower left tile
        final String wrongXMessage = "Tile clicks don't affect the right X position";
        final String wrongYMessage = "Tile clicks don't affect the right Y position";
        ImageView[][] tiles = (ImageView[][]) FieldUtils.readField(activity, "tiles", true);
        ImageView lowerLeftTile = tiles[0][0];
        Drawable emptyTile = lowerLeftTile.getDrawable();
        lowerLeftTile.performClick();
        Assert.assertNotEquals("Clicking tiles does nothing", -1, lastSetBoardAtCoords[0]);
        Assert.assertEquals(wrongXMessage, 0, lastSetBoardAtCoords[0]);
        Assert.assertEquals(wrongYMessage, 0, lastSetBoardAtCoords[1]);
        boolean anyUiChange = false;
        for (int v = 0; v < board.getChildCount(); v++) {
            if (((ImageView) board.getChildAt(v)).getDrawable() != emptyTile) {
                anyUiChange = true;
                break;
            }
        }
        Assert.assertTrue("Clicking tiles doesn't update the UI", anyUiChange);
        Assert.assertNotEquals("Clicking tiles doesn't update the correct tile's image", emptyTile, lowerLeftTile.getDrawable());

        // Test clicking on some other empty tiles
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            tiles[1][y].performClick();
            //board.getChildAt(y * BOARD_WIDTH + 1).performClick(); // Second column
            Assert.assertEquals(wrongXMessage, 1, lastSetBoardAtCoords[0]);
            Assert.assertEquals(wrongYMessage, y, lastSetBoardAtCoords[1]);
        }
        for (int x = 2; x < BOARD_WIDTH; x++) {
            tiles[x][0].performClick();
            Assert.assertEquals(wrongXMessage, x, lastSetBoardAtCoords[0]);
            Assert.assertEquals(wrongYMessage, 0, lastSetBoardAtCoords[1]);
        }
    }

    @Test
    public void testWinnerLabel() {
        // A game that just started
        GameActivity activity = startActivity(null, "Chuchu", "Xyz");
        fireLayout(activity);
        TextView winnerLabel = activity.findViewById(R.id.winner);
        Assert.assertEquals("The winner label should be gone initially",
                View.GONE, winnerLabel.getVisibility());

        // A game with some plays but no winner
        Bundle gameState = createGameStateBundle(0, 1, 2);
        activity = startActivity(gameState, "Chuchu", "Xyz");
        fireLayout(activity);
        winnerLabel = activity.findViewById(R.id.winner);
        final String earlyWinnerMessage = "The winner label should be gone while there's no winner";
        Assert.assertEquals(earlyWinnerMessage, View.GONE, winnerLabel.getVisibility());

        // A game where player 1 wins
        int[] plays = new int[7];
        for (int m = 0; m < 7; m++) {
            plays[m] = (m / 2) * BOARD_WIDTH + (m % 2);
        }
        gameState = createGameStateBundle(plays);
        activity = startActivity(null, "Stamets", "Culber");
        winnerLabel = activity.findViewById(R.id.winner);
        fireLayout(activity);
        Assert.assertEquals(earlyWinnerMessage, View.GONE, winnerLabel.getVisibility());
        activity.onRestoreInstanceState(gameState);
        activity.updateDisplay();
        ConnectN game = activity.getGame();
        ConnectN gameSpy = Mockito.spy(game);
        Mockito.when(gameSpy.getWinner()).thenReturn(activity.getPlayer(0));
        activity.setGame(gameSpy);
        final String labelGoneMessage = "The winner label should be visible when there's a winner";
        final String wrongLabelMessage = "The winner label has the wrong text";
        final String wrongColorMessage = "The winner label should be the winner's color";
        Assert.assertEquals(labelGoneMessage, View.VISIBLE, winnerLabel.getVisibility());
        Assert.assertEquals(wrongLabelMessage, "Stamets wins!", winnerLabel.getText().toString());
        int desiredLabelColor = activity.getResources().getColor(R.color.player1Color, activity.getTheme());
        Assert.assertEquals(wrongColorMessage, desiredLabelColor, winnerLabel.getCurrentTextColor());

        // A game where player 2 wins
        plays = new int[8];
        for (int m = 1; m < 8; m++) {
            plays[m] = ((m - 1) / 2) * BOARD_WIDTH + 1 + (m % 2);
        }
        gameState = createGameStateBundle(plays);
        activity = startActivity(gameState, "Stamets", "Culber");
        game = activity.getGame();
        gameSpy = Mockito.spy(game);
        Mockito.when(gameSpy.getWinner()).thenReturn(activity.getPlayer(1));
        activity.setGame(gameSpy);
        fireLayout(activity);
        winnerLabel = activity.findViewById(R.id.winner);
        Assert.assertEquals(labelGoneMessage, View.VISIBLE, winnerLabel.getVisibility());
        Assert.assertEquals(wrongLabelMessage, "Culber wins!", winnerLabel.getText().toString());
        desiredLabelColor = activity.getResources().getColor(R.color.player2Color, activity.getTheme());
        Assert.assertEquals(wrongColorMessage, desiredLabelColor, winnerLabel.getCurrentTextColor());
    }

}
