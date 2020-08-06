package edu.illinois.cs.cs125.spring2019.mp2.lib;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Test suite for the ConnectN class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it. You will need to augment or write test suites for later MPs.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/2/">MP2 Documentation</a>
 */
@SuppressWarnings({"checkstyle:magicnumber"})
public class ConnectNTest {
    private final Random random = new Random();

    /** Timeout for all tests. These should be quite quick. */
    private static final int TEST_TIMEOUT = 100;

    /**
     * Test simple width getters and setters.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testGetAndSetWidth() {
        final String setBoardWidth = "Should be able to set board width";
        final String notSetCorrectly = "Board width not set correctly";
        final String invalidBoardWidth = "Should not be able to set invalid board width";
        final String invalidReset = "Invalid set should not reset previous width";
        final String widthIsStatic = "Board width should not be static";
        final String widthIsPublic = "Board width should not be public";

        ConnectN board = new ConnectN();
        Assert.assertEquals("Read the spec, MAX_WIDTH is wrong", 16, ConnectN.MAX_WIDTH);


        /*
         * Test valid widths.
         */
        Assert.assertTrue(setBoardWidth, board.setWidth(7));
        Assert.assertEquals(notSetCorrectly, 7, board.getWidth());
        Assert.assertTrue(setBoardWidth, board.setWidth(13));
        Assert.assertEquals(notSetCorrectly, 13, board.getWidth());


        /*
         * Test invalid widths.
         */
        Assert.assertFalse(invalidBoardWidth, board.setWidth(0));
        Assert.assertEquals(invalidReset, 13, board.getWidth());
        Assert.assertFalse(invalidBoardWidth, board.setWidth(-9));
        Assert.assertEquals(invalidReset, 13, board.getWidth());
        Assert.assertFalse(invalidBoardWidth, board.setWidth(3));
        Assert.assertEquals(invalidReset, 13, board.getWidth());
        Assert.assertFalse(invalidBoardWidth, board.setWidth(2019));
        Assert.assertEquals(invalidReset, 13, board.getWidth());


        /*
         * Make sure width still works.
         */
        Assert.assertTrue(setBoardWidth, board.setWidth(7));
        Assert.assertEquals(notSetCorrectly, 7, board.getWidth());

        /*
         * Make sure width is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        Assert.assertTrue(setBoardWidth, anotherBoard.setWidth(9));
        Assert.assertEquals(notSetCorrectly,9, anotherBoard.getWidth());
        Assert.assertEquals(widthIsStatic,7, board.getWidth());
        Assert.assertTrue(setBoardWidth, board.setWidth(11));
        Assert.assertEquals(notSetCorrectly,9, anotherBoard.getWidth());
        Assert.assertEquals(notSetCorrectly,11, board.getWidth());

        /*
         * Make sure width is not public.
         */
        for (Field field : ConnectN.class.getFields()) {
            try {
                int publicWidth = field.getInt(board);
                Assert.assertNotEquals(widthIsPublic, 11, publicWidth);
                publicWidth = field.getInt(anotherBoard);
                Assert.assertNotEquals(widthIsPublic, 9, publicWidth);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test simple height getters and setters.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testGetAndSetHeight() {
        final String setBoardHeight = "Should be able to set board height";
        final String notSetCorrectly = "Board height not set correctly";
        final String invalidBoardHeight = "Should not be able to set invalid board height";
        final String invalidReset = "Invalid set should not reset previous height";
        final String heightIsStatic = "Board height should not be static";
        final String heightIsPublic = "Board height should not be public";

        /*
         * Test MAX_HEIGHT.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals("Read the spec, MAX_HEIGHT is wrong", 16, ConnectN.MAX_HEIGHT);

        /*
         * Test valid heights.
         */
        Assert.assertTrue(setBoardHeight, board.setHeight(6));
        Assert.assertEquals(notSetCorrectly, 6, board.getHeight());
        Assert.assertTrue(setBoardHeight, board.setHeight(12));
        Assert.assertEquals(notSetCorrectly, 12, board.getHeight());

        /*
         * Test invalid heights.
         */
        Assert.assertFalse(invalidBoardHeight, board.setHeight(0));
        Assert.assertEquals(invalidReset, 12, board.getHeight());
        Assert.assertFalse(invalidBoardHeight, board.setHeight(-1));
        Assert.assertEquals(invalidReset, 12, board.getHeight());
        Assert.assertFalse(invalidBoardHeight, board.setHeight(4));
        Assert.assertEquals(invalidReset, 12, board.getHeight());
        Assert.assertFalse(invalidBoardHeight, board.setHeight(1000));
        Assert.assertEquals(invalidReset, 12, board.getHeight());

        /*
         * Make sure height still works.
         */
        Assert.assertTrue(setBoardHeight, board.setHeight(6));
        Assert.assertEquals(notSetCorrectly, 6, board.getHeight());

        /*
         * Make sure height is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        Assert.assertTrue(setBoardHeight, anotherBoard.setHeight(8));
        Assert.assertEquals(notSetCorrectly, 8, anotherBoard.getHeight());
        Assert.assertEquals(heightIsStatic, 6, board.getHeight());
        Assert.assertTrue(setBoardHeight, board.setHeight(10));
        Assert.assertEquals(heightIsStatic, 8, anotherBoard.getHeight());
        Assert.assertEquals(notSetCorrectly, 10, board.getHeight());

        /*
         * Make sure height is not public.
         */
        for (Field field : ConnectN.class.getFields()) {
            try {
                int publicHeight = field.getInt(board);
                Assert.assertNotEquals(heightIsPublic, 10, publicHeight);
                publicHeight = field.getInt(anotherBoard);
                Assert.assertNotEquals(heightIsPublic, 8, publicHeight);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test simple N getters and setters.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testGetAndSetN() {
        final String earlySetN = "Should not be able to set board N value before width and height";
        final String setBoardN = "Should be able to set board N value";
        final String notSetCorrectly = "Board N value not set correctly";
        final String invalidBoardN = "Should not be able to set invalid board N value";
        final String invalidReset = "Invalid set should not reset previous N value";
        final String invalidDimensionReset = "Changing width and height should not reset N";
        final String missingDimensionReset = "Changing width and height should reset N";
        final String nIsStatic = "Board N value should not be static";
        final String nIsPublic = "Board N value should not be public";

        /*
         * Test MAX_WIDTH.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals("Read the spec, MIN_N is wrong", 4, ConnectN.MIN_N);


        /*
         * Test setting N before width and height.
         */
        Assert.assertFalse(earlySetN, board.setN(4));
        Assert.assertTrue(board.setWidth(8));
        Assert.assertFalse(earlySetN, board.setN(4));
        Assert.assertTrue(board.setHeight(8));

        /*
         * Test valid N.
         */
        Assert.assertTrue(setBoardN, board.setN(4));
        Assert.assertEquals(notSetCorrectly, 4, board.getN());
        Assert.assertTrue(setBoardN, board.setN(6));
        Assert.assertEquals(notSetCorrectly, 6, board.getN());
        Assert.assertTrue(setBoardN, board.setN(7));
        Assert.assertEquals(notSetCorrectly, 7, board.getN());

        /*
         * Test invalid N.
         */
        Assert.assertFalse(invalidBoardN, board.setN(10));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertFalse(invalidBoardN, board.setN(1));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertFalse(invalidBoardN, board.setN(0));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertFalse(invalidBoardN, board.setN(-1));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertFalse(invalidBoardN, board.setN(8));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertTrue(board.setWidth(9));
        Assert.assertFalse(invalidBoardN, board.setN(9));
        Assert.assertEquals(invalidReset, 7, board.getN());
        Assert.assertTrue(board.setWidth(8));

        /*
         * Make sure that changing widths and heights resets N as needed.
         */
        Assert.assertEquals(7, board.getN());
        Assert.assertTrue(board.setWidth(9));
        Assert.assertEquals(9, board.getWidth());
        Assert.assertEquals(invalidDimensionReset, 7, board.getN());
        Assert.assertTrue(board.setHeight(9));
        Assert.assertEquals(9, board.getHeight());
        Assert.assertEquals(invalidDimensionReset, 7, board.getN());
        Assert.assertTrue(board.setWidth(7));
        Assert.assertEquals(7, board.getWidth());
        Assert.assertEquals(invalidDimensionReset, 7, board.getN());
        Assert.assertTrue(board.setHeight(6));
        Assert.assertEquals(6, board.getHeight());
        Assert.assertEquals(missingDimensionReset, 0, board.getN());
        Assert.assertTrue(board.setWidth(10));
        Assert.assertTrue(board.setHeight(10));
        Assert.assertTrue(setBoardN, board.setN(6));
        Assert.assertEquals(notSetCorrectly, 6, board.getN());
        Assert.assertTrue(board.setHeight(6));
        Assert.assertTrue(board.setWidth(6));
        Assert.assertEquals(6, board.getHeight());
        Assert.assertEquals(6, board.getWidth());
        Assert.assertEquals(0, board.getN());
        Assert.assertTrue(board.setHeight(7));
        Assert.assertTrue(board.setWidth(7));
        Assert.assertEquals(7, board.getHeight());
        Assert.assertEquals(7, board.getWidth());
        Assert.assertTrue(board.setN(6));
        Assert.assertEquals(6, board.getN());
        Assert.assertTrue(board.setWidth(6));
        Assert.assertTrue(board.setHeight(6));
        Assert.assertEquals(6, board.getWidth());
        Assert.assertEquals(6, board.getHeight());
        Assert.assertEquals(0, board.getN());
        Assert.assertTrue(board.setN(4));
        Assert.assertEquals(4, board.getN());



        /*
         * Make sure N is not static.
         */
        ConnectN anotherBoard = new ConnectN();
        anotherBoard.setWidth(10);
        anotherBoard.setHeight(10);
        Assert.assertTrue(setBoardN, anotherBoard.setN(8));
        Assert.assertEquals(notSetCorrectly, 8, anotherBoard.getN());
        Assert.assertEquals(nIsStatic, 4, board.getN());
        Assert.assertTrue(setBoardN, board.setN(5));
        Assert.assertEquals(nIsStatic, 8, anotherBoard.getN());
        Assert.assertEquals(notSetCorrectly, 5, board.getN());

        /*
         * Make sure N is not public.
         */
        for (Field field : ConnectN.class.getFields()) {
            try {
                int publicN = field.getInt(board);
                Assert.assertNotEquals(nIsPublic, 5, publicN);
                publicN = field.getInt(anotherBoard);
                Assert.assertNotEquals(nIsPublic, 8, publicN);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Test ConnectN constructors.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testConstructors() {
        final String emptyConstructorSetFields = //
                "Empty constructor should not initialize width, height, or N";
        final String widthHeightConstructorMissedFields = //
                "Width and height constructor should initialize width and height";
        final String widthHeightConstructorInvalidFields = //
                "Width and height constructor should ignore invalid values";
        final String widthHeightConstructorSetN = //
                "Width and height constructor should not set N";
        final String completeConstructorMissedFields = //
                "Complete constructor should initialize width, height, and N";
        final String completeConstructorInvalidFields = //
                "Complete constructor should ignore invalid values";
        final String copyConstructorMissedFields = //
                "Copy constructor should copy width, height, and N";

        /*
         * Test empty constructor.
         */
        ConnectN board = new ConnectN();
        Assert.assertEquals(emptyConstructorSetFields, 0, board.getWidth());
        Assert.assertEquals(emptyConstructorSetFields, 0, board.getHeight());
        Assert.assertEquals(emptyConstructorSetFields, 0, board.getN());

        /*
         * Test width and height constructor with valid values.
         */
        board = new ConnectN(6, 8);
        Assert.assertEquals(widthHeightConstructorMissedFields, 6, board.getWidth());
        Assert.assertEquals(widthHeightConstructorMissedFields, 8, board.getHeight());
        Assert.assertEquals(widthHeightConstructorSetN, 0, board.getN());

        /*
         * Test width and height constructor with invalid values.
         */
        board = new ConnectN(-1, 8);
        Assert.assertEquals(widthHeightConstructorInvalidFields, 0, board.getWidth());
        Assert.assertEquals(widthHeightConstructorMissedFields, 8, board.getHeight());
        Assert.assertEquals(widthHeightConstructorSetN, 0, board.getN());
        board = new ConnectN(8, 1000);
        Assert.assertEquals(widthHeightConstructorMissedFields, 8, board.getWidth());
        Assert.assertEquals(widthHeightConstructorInvalidFields, 0, board.getHeight());
        Assert.assertEquals(widthHeightConstructorSetN, 0, board.getN());
        board = new ConnectN(1000, -1);
        Assert.assertEquals(widthHeightConstructorInvalidFields, 0, board.getWidth());
        Assert.assertEquals(widthHeightConstructorInvalidFields, 0, board.getHeight());
        Assert.assertEquals(widthHeightConstructorSetN, 0, board.getN());

        /*
         * Test complete constructor with valid values.
         */
        board = new ConnectN(6, 8, 4);
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 8, board.getHeight());
        Assert.assertEquals(completeConstructorMissedFields, 4, board.getN());
        board = new ConnectN(6, 6, 5);
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getHeight());
        Assert.assertEquals(completeConstructorMissedFields, 5, board.getN());

        /*
         * Test complete constructor with invalid values.
         */
        board = new ConnectN(6, 8, -1);
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 8, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());
        board = new ConnectN(6, -1, 2);
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getWidth());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());
        board = new ConnectN(-1, 10, 6);
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 10, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());
        board = new ConnectN(7, -1, 100);
        Assert.assertEquals(completeConstructorMissedFields, 7, board.getWidth());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());
        board = new ConnectN(-1, 13, 1001);
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 13, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());
        board = new ConnectN(10, 9, 10);
        Assert.assertEquals(completeConstructorMissedFields, 10, board.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 9, board.getHeight());
        Assert.assertEquals(completeConstructorInvalidFields, 0, board.getN());

        /*
         * Test copy constructor with valid values.
         */
        board = new ConnectN(8, 7, 6);
        ConnectN anotherBoard = new ConnectN(board);
        Assert.assertEquals(completeConstructorMissedFields, 8, board.getWidth());
        Assert.assertEquals(copyConstructorMissedFields, 8, anotherBoard.getWidth());
        Assert.assertEquals(completeConstructorMissedFields, 7, board.getHeight());
        Assert.assertEquals(copyConstructorMissedFields, 7, anotherBoard.getHeight());
        Assert.assertEquals(completeConstructorMissedFields, 6, board.getN());
        Assert.assertEquals(copyConstructorMissedFields, 6, anotherBoard.getN());
    }

    /**
     * Test getting and setting the board at a specific position.
     */
    @Test(timeout = TEST_TIMEOUT)
    @SuppressWarnings("checkstyle:methodlength")
    public void testGetAndSetBoard() {
        final String validSet = "Set at this position should succeed";
        final String validGet = "Get at this position should return a player";
        final String nullGet = "Get at this position should return null";
        final String invalidSet = "Set at this position should fail";
        final String gameBoardInitialized = "Game board was not properly initialized";
        final String gameBoardCopy = "getBoard should return an independent copy";
        final String changeAfterStart = "Can't change dimensions after start";
        final String uninitializedBoardGet = //
                "Calls to getBoard before initialization should return null";

        /*
         * Test valid sets and gets.
         */
        ConnectN board = new ConnectN(10, 9, 6);
        Player player = new Player("Chuchu");
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 9; y++) {
                Assert.assertNull(nullGet, board.getBoardAt(x, y));
            }
        }
        Assert.assertNull(nullGet, board.getBoardAt(0, 0));
        Assert.assertTrue(validSet, board.setBoardAt(player, 0, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(0, 0));

        Assert.assertFalse(changeAfterStart, board.setWidth(9));
        Assert.assertEquals(changeAfterStart, 10, board.getWidth());
        Assert.assertFalse(changeAfterStart, board.setHeight(8));
        Assert.assertEquals(changeAfterStart, 9, board.getHeight());
        Assert.assertFalse(changeAfterStart, board.setN(7));
        Assert.assertEquals(changeAfterStart, 6, board.getN());

        Assert.assertNull(nullGet, board.getBoardAt(9, 0));
        Assert.assertTrue(validSet, board.setBoardAt(player, 9, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(9, 0));

        Assert.assertNull(nullGet, board.getBoardAt(7, 0));
        Assert.assertTrue(validSet, board.setBoardAt(player, 7, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(7, 0));

        Assert.assertNull(nullGet, board.getBoardAt(5, 0));
        Assert.assertTrue(validSet, board.setBoardAt(player, 5, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(5, 0));

        Assert.assertNull(nullGet, board.getBoardAt(7, 1));
        Assert.assertTrue(validSet, board.setBoardAt(player, 7, 1));
        Assert.assertEquals(validGet, player, board.getBoardAt(7, 1));

        Assert.assertNull(nullGet, board.getBoardAt(7, 2));
        Assert.assertTrue(validSet, board.setBoardAt(player, 7, 2));
        Assert.assertEquals(validGet, player, board.getBoardAt(7, 2));

        /*
         * Test invalid dimensions.
         */
        Assert.assertNull(nullGet, board.getBoardAt(-1, 0));
        Assert.assertNull(nullGet, board.getBoardAt(0, -1));
        Assert.assertNull(nullGet, board.getBoardAt(100, 4));
        Assert.assertNull(nullGet, board.getBoardAt(5, 99));

        /*
         * Test invalid sets.
         */
        board = new ConnectN(8, 7, 4);
        player = new Player("Chuchu");
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Assert.assertNull(nullGet, board.getBoardAt(x, y));
            }
        }
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 0, 1));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 9, 0));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 7, 6));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 7));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 1));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 2));
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Assert.assertNull(nullGet, board.getBoardAt(x, y));
            }
        }

        Assert.assertTrue(validSet, board.setBoardAt(player, 3, 0));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(3, 0));

        Assert.assertTrue(validSet, board.setBoardAt(player, 3, 1));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 0));
        Assert.assertEquals(validGet, player, board.getBoardAt(3, 0));
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 3, 1));
        Assert.assertEquals(validGet, player, board.getBoardAt(3, 1));

        /*
         * Test drop sets.
         */
        board = new ConnectN(10, 6, 8);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertNull(nullGet, board.getBoardAt(x, y));
            }
        }
        player = new Player("Chuchu");
        Player otherPlayer = new Player("Xyz");
        for (int y = 0; y < 6; y++) {
            Assert.assertNull(nullGet, board.getBoardAt(0, y));
            Assert.assertTrue(validSet, board.setBoardAt(player, 0));
            Assert.assertEquals(validGet, player, board.getBoardAt(0, y));
        }
        Assert.assertFalse(invalidSet, board.setBoardAt(player, 0));
        for (int y = 0; y < 6; y++) {
            Assert.assertFalse(invalidSet, board.setBoardAt(otherPlayer, 0, y));
            Assert.assertEquals(validGet, player, board.getBoardAt(0, y));
        }
        for (int y = 0; y < 6; y++) {
            Assert.assertNull(nullGet, board.getBoardAt(5, y));
            Assert.assertTrue(validSet, board.setBoardAt(player, 5));
            Assert.assertEquals(validGet, player, board.getBoardAt(5, y));
        }
        for (int y = 0; y < 6; y++) {
            Assert.assertFalse(invalidSet, board.setBoardAt(otherPlayer, 5, y));
            Assert.assertEquals(validGet, player, board.getBoardAt(5, y));
        }

        /*
         * Test valid board getters.
         */
        ConnectN game = new ConnectN();
        Assert.assertNull(uninitializedBoardGet, game.getBoard());
        game.setWidth(10);
        Assert.assertNull(uninitializedBoardGet, game.getBoard());
        game.setHeight(10);
        Assert.assertNotNull("After dimensions are set getBoard should succeed", game.getBoard());

        game = new ConnectN(10, 6, 8);
        player = new Player("Chuchu");
        Player[][] gameBoard = game.getBoard();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertNull(gameBoardInitialized, gameBoard[x][y]);
            }
        }
        Assert.assertTrue(game.setBoardAt(player, 0));
        Assert.assertEquals(player, game.getBoardAt(0, 0));
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                Assert.assertNull(gameBoardCopy, gameBoard[x][y]);
            }
        }
        gameBoard = game.getBoard();
        Assert.assertEquals(player, gameBoard[0][0]);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 6; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                Assert.assertNull(gameBoardCopy, gameBoard[x][y]);
            }
        }
        gameBoard[0][0].setName("Xyz");
        Player[][] realBoard = game.getBoard();
        Assert.assertEquals(gameBoardCopy, "Chuchu", realBoard[0][0].getName());

        gameBoard[0][1] = player;
        realBoard = game.getBoard();
        Assert.assertNull(gameBoardCopy, realBoard[0][1]);

        gameBoard[0][0] = null;
        realBoard = game.getBoard();
        Assert.assertEquals(gameBoardCopy, player, realBoard[0][0]);

        /*
         * Test bad moves.
         */
        Assert.assertFalse("setX cannot be < 0", game.setBoardAt(player, -1, 0));
        Assert.assertFalse("setY cannot be < 0", game.setBoardAt(player, 0, -1));
        Assert.assertFalse("setX cannot be < 0", game.setBoardAt(player, -1));
    }

    /**
     * Test that the game detects and returns a winner properly.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testWinner() {
        final String gameShouldNotBeOver = "No winner should be declared yet";
        final String gameShouldBeOver = "The game should be over now";
        final String scoreCount = "Game should increase the winner's score count";
        final String afterGame = "No moves are allowed after a game ends";

        Player chuchu = new Player("Chuchu");
        Player xyz = new Player("xyz");

        /*
         * Test uninitialized games.
         */
        ConnectN board = new ConnectN();
        Assert.assertNull(gameShouldNotBeOver, board.getWinner());
        board.setWidth(10);
        Assert.assertNull(gameShouldNotBeOver, board.getWinner());
        board.setHeight(8);
        Assert.assertNull(gameShouldNotBeOver, board.getWinner());
        board.setN(4);
        Assert.assertNull(gameShouldNotBeOver, board.getWinner());

        /*
         * Test simple games with a winner.
         */
        for (int count = 0; count < 32; count++) {
            board = new ConnectN(10, 10, 5);
            int randomX = random.nextInt(10);
            for (int i = 0; i < 5; i++) {
                Assert.assertNull(gameShouldNotBeOver, board.getWinner());
                Assert.assertTrue(board.setBoardAt(chuchu, randomX));
            }
            Assert.assertEquals(gameShouldBeOver, chuchu, board.getWinner());
            Assert.assertEquals(scoreCount, count + 1, chuchu.getScore());
            Assert.assertEquals(scoreCount, 0, xyz.getScore());
            Assert.assertFalse(afterGame, board.setBoardAt(chuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(xyz, 1));
        }
        for (int count = 0; count < 32; count++) {
            board = new ConnectN(10, 10, 5);
            int randomX = random.nextInt(5);
            for (int i = 0; i < 5; i++) {
                Assert.assertNull(gameShouldNotBeOver, board.getWinner());
                Assert.assertTrue(board.setBoardAt(chuchu, randomX + i, 0));
            }
            Assert.assertEquals(gameShouldBeOver, chuchu, board.getWinner());
            Assert.assertEquals(scoreCount, 32 + count + 1, chuchu.getScore());
            Assert.assertEquals(scoreCount, 0, xyz.getScore());
            Assert.assertFalse(afterGame, board.setBoardAt(chuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(xyz, 1));
        }

        /*
         * Test a game with no winner.
         */
        board = new ConnectN(8, 6, 7);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 6; y++) {
                if (x % 2 == 0) {
                    Assert.assertTrue(board.setBoardAt(chuchu, x, y));
                    Assert.assertEquals(chuchu, board.getBoardAt(x, y));
                } else {
                    Assert.assertTrue(board.setBoardAt(xyz, x, y));
                    Assert.assertEquals(xyz, board.getBoardAt(x, y));
                }
                Assert.assertNull(gameShouldNotBeOver, board.getWinner());
            }
        }
        Assert.assertEquals(scoreCount, 2 * 32, chuchu.getScore());
        Assert.assertEquals(scoreCount, 0, xyz.getScore());

        /*
         * Test corner cases.
         */
        board = new ConnectN(8, 8, 4);
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 1));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 2));
        Assert.assertTrue(board.setBoardAt(xyz, 0, 3));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 4));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 5));
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 6));

        board = new ConnectN(8, 8, 4);
        Assert.assertTrue(board.setBoardAt(chuchu, 0, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 1, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 2, 0));
        Assert.assertTrue(board.setBoardAt(xyz, 3, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 4, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 5, 0));
        Assert.assertTrue(board.setBoardAt(chuchu, 6, 0));

        /*
         * Test simple games with a winner and duplicate players.
         */
        for (int count = 0; count < 32; count++) {
            Player anotherChuchu = new Player(chuchu);
            Assert.assertNotNull(anotherChuchu);
            board = new ConnectN(10, 10, 5);
            int randomX = random.nextInt(10);
            for (int i = 0; i < 5; i++) {
                Assert.assertNull(gameShouldNotBeOver, board.getWinner());
                if (random.nextBoolean()) {
                    Assert.assertTrue(board.setBoardAt(chuchu, randomX));
                } else {
                    Assert.assertTrue(board.setBoardAt(anotherChuchu, randomX));
                }
            }
            Assert.assertEquals(gameShouldBeOver, chuchu, board.getWinner());
            Assert.assertEquals(scoreCount, 0, xyz.getScore());
            Assert.assertFalse(afterGame, board.setBoardAt(chuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(anotherChuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(xyz, 1));
        }
        for (int count = 0; count < 32; count++) {
            Player anotherChuchu = new Player(chuchu);
            board = new ConnectN(10, 10, 5);
            int randomX = random.nextInt(5);
            for (int i = 0; i < 5; i++) {
                Assert.assertNull(gameShouldNotBeOver, board.getWinner());
                if (random.nextBoolean()) {
                    Assert.assertTrue(board.setBoardAt(chuchu, randomX + i, 0));
                } else {
                    Assert.assertTrue(board.setBoardAt(anotherChuchu, randomX + i, 0));
                }
            }
            Assert.assertEquals(gameShouldBeOver, chuchu, board.getWinner());
            Assert.assertEquals(scoreCount, 0, xyz.getScore());
            Assert.assertFalse(afterGame, board.setBoardAt(chuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(anotherChuchu, 0));
            Assert.assertFalse(afterGame, board.setBoardAt(xyz, 1));
        }
    }

    /**
     * Test static methods, including create and compare.
     */
    @Test(timeout = TEST_TIMEOUT)
    public void testStaticMethods() {
        final String validCreate = "Factory methods should create boards given valid parameters";
        final String invalidCreate = //
                "Factory methods should not create boards given invalid parameters";
        final String invalidCompare = "Factory methods should compare boards properly";

        /*
         * Test simple create with valid parameters.
         */
        ConnectN board = ConnectN.create(8, 10, 4);
        Assert.assertNotNull(validCreate, board);
        Assert.assertEquals(validCreate, ConnectN.class, board.getClass());
        Assert.assertEquals(validCreate, 8, board.getWidth());
        Assert.assertEquals(validCreate, 10, board.getHeight());
        Assert.assertEquals(validCreate, 4, board.getN());

        /*
         * Test simple create with invalid parameters.
         */
        board = ConnectN.create(4, 10, 4);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(10, 4, 6);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(10, 10, 2);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(10, 10, -1);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(-1, 10, 4);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(8, -1, 6);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(10, 8, 10);
        Assert.assertNull(invalidCreate, board);
        board = ConnectN.create(8, 10, 10);
        Assert.assertNull(invalidCreate, board);

        /*
         * Test multi create with valid parameters.
         */
        ConnectN[] boards = ConnectN.createMany(6, 8, 10, 4);
        Assert.assertNotNull(validCreate, boards);
        Assert.assertEquals(validCreate, ConnectN[].class, boards.getClass());
        for (ConnectN arrayBoard : boards) {
            Assert.assertEquals(validCreate, ConnectN.class, arrayBoard.getClass());
            Assert.assertEquals(validCreate, 8, arrayBoard.getWidth());
            Assert.assertEquals(validCreate, 10, arrayBoard.getHeight());
            Assert.assertEquals(validCreate, 4, arrayBoard.getN());
        }

        /*
         * Test multi create with invalid parameters.
         */
        boards = ConnectN.createMany(10, 4, 10, 4);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(4, 10, 4, 6);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(3, 10, 10, 2);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(7, 10, 10, -1);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(9, -1, 10, 4);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(20, 8, -1, 6);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(1, 10, 8, 10);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(5, 8, 10, 10);
        Assert.assertNull(invalidCreate, boards);
        boards = ConnectN.createMany(0, 8, 10, 4);
        Assert.assertNull(invalidCreate, boards);

        /*
         * Test simple compare boards.
         */
        board = new ConnectN(10, 8, 6);
        ConnectN anotherBoard = new ConnectN(10, 8, 8);
        Assert.assertTrue(ConnectN.compareBoards(board, board));
        Assert.assertTrue(ConnectN.compareBoards(board, new ConnectN(board)));
        Assert.assertFalse(ConnectN.compareBoards(board, null));
        Assert.assertFalse(ConnectN.compareBoards(null, board));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(), new ConnectN()));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 0), new ConnectN(10, 0)));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 6, 0), new ConnectN(10, 6, 0)));
        Assert.assertTrue(ConnectN.compareBoards(new ConnectN(10, 6, 4), new ConnectN(10, 6, 4)));
        Assert.assertFalse(invalidCompare, ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(anotherBoard.setN(6));
        Assert.assertTrue(invalidCompare, ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(board.setHeight(10));
        Assert.assertFalse(invalidCompare, ConnectN.compareBoards(board, anotherBoard));

        /*
         * Test slightly more complex compare boards.
         */
        board = new ConnectN(10, 8, 6);
        anotherBoard = new ConnectN(10, 8, 6);
        Player firstPlayer = new Player("Chuchu");
        Player secondPlayer = new Player("Xyz");
        Assert.assertTrue(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(board.setBoardAt(firstPlayer, 0, 0));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(anotherBoard.setBoardAt(firstPlayer, 0, 0));
        Assert.assertTrue(ConnectN.compareBoards(board, anotherBoard));
        Assert.assertTrue(board.setBoardAt(firstPlayer, 1, 0));
        Assert.assertTrue(anotherBoard.setBoardAt(secondPlayer, 1, 0));
        Assert.assertFalse(ConnectN.compareBoards(board, anotherBoard));

        /*
         * Test multi compare boards.
         */
        boards = ConnectN.createMany(6, 10, 10, 4);
        Assert.assertNotNull(boards);
        Assert.assertTrue(invalidCompare, ConnectN.compareBoards(boards[0]));
        Assert.assertTrue(invalidCompare, ConnectN.compareBoards(boards));
        Assert.assertTrue(invalidCompare, ConnectN.compareBoards(boards[0], boards[2], boards[3]));
        boards[1] = ConnectN.create(8, 8, 4);
        Assert.assertFalse(invalidCompare, ConnectN.compareBoards(boards));
        Assert.assertTrue(invalidCompare, ConnectN.compareBoards(boards[0], boards[2], boards[3]));
    }
}
