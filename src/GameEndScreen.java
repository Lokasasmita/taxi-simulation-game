import bagel.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * The GameEndScreen class represents the screen shown at the end of the game,
 * indicating whether the player has won or lost, and displaying the top scores.
 */
public class GameEndScreen {
    private final Image BACKGROUND;
    private final Font SCORES_FONT;
    private final Font STATUS_FONT;
    private final String STATUS_TEXT;
    private final String SCORES_FILE;
    private final String SCORES_TITLE;
    private final int SCORES_Y;
    private final int STATUS_Y;

    /**
     * Constructs a new GameEndScreen instance with the given game properties and message properties.
     * Initializes the background, fonts, and text to display based on whether the player won or lost.
     *
     * @param gameProps Properties containing game configuration such as fonts, background, and scores file.
     * @param messageProps Properties containing text messages such as win/loss status.
     * @param won A boolean indicating whether the player won or lost the game.
     */
    public GameEndScreen(Properties gameProps, Properties messageProps, boolean won) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.gameEnd"));

        SCORES_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize")));
        SCORES_TITLE = messageProps.getProperty("gameEnd.highestScores");
        SCORES_Y = Integer.parseInt(gameProps.getProperty("gameEnd.scores.y"));

        STATUS_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameEnd.status.fontSize")));
        STATUS_Y = Integer.parseInt(gameProps.getProperty("gameEnd.status.y"));

        this.SCORES_FILE = gameProps.getProperty("gameEnd.scoresFile");

        if (won) {
            STATUS_TEXT = messageProps.getProperty("gameEnd.won");
        } else {
            STATUS_TEXT = messageProps.getProperty("gameEnd.lost");
        }

    }

    /**
     * Show whether the game is won or lost and the top 5 scores.
     * @param input The current mouse/keyboard input.
     * @return true if SPACE key is pressed, false otherwise.
     */
    public boolean update(Input input) {
        BACKGROUND.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        SCORES_FONT.drawString(SCORES_TITLE, (Window.getWidth() - SCORES_FONT.getWidth(SCORES_TITLE)) / 2.0,
                SCORES_Y);

        // Render the top scores from the CSV file
        renderTopScores();

        // Render the end game message (win/loss)
        STATUS_FONT.drawString(STATUS_TEXT, (Window.getWidth() - STATUS_FONT.getWidth(STATUS_TEXT)) / 2.0, STATUS_Y);

        return input.wasPressed(Keys.SPACE);
    }

    private void renderTopScores() {
        String[][] allScores = IOUtils.readCommaSeparatedFile(SCORES_FILE);
        List<String[]> sortedScores = getTopScores(allScores);

        // Render each score in the format "i - j"
        for (int i = 0; i < sortedScores.size(); i++) {
            String[] scoreEntry = sortedScores.get(i);
            String scoreLine = (i + 1) + " - " + scoreEntry[0] + " - " + scoreEntry[1];
            double scoreLineX = (Window.getWidth() - SCORES_FONT.getWidth(scoreLine)) / 2.0;

            int SCORE_LINE_SPACING = 40;
            double scoreLineY = SCORES_Y + (i + 1) * SCORE_LINE_SPACING;
            SCORES_FONT.drawString(scoreLine, scoreLineX, scoreLineY);
        }
    }

    private List<String[]> getTopScores(String[][] allScores) {
        // Convert array to list for easier manipulation
        List<String[]> scoreList = new ArrayList<>(Arrays.asList(allScores));

        // Sort scores by the numeric value (assuming the format is "name,score")
        scoreList.sort((a, b) -> {
            double scoreA = Double.parseDouble(a[1]);
            double scoreB = Double.parseDouble(b[1]);
            return Double.compare(scoreB, scoreA);  // Descending order
        });

        // Collect the top maxTopScores entries
        int MAX_TOP_SCORES = 5;
        if (scoreList.size() > MAX_TOP_SCORES) {
            return scoreList.subList(0, MAX_TOP_SCORES);
        } else {
            return scoreList;
        }
    }
}
