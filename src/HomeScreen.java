import bagel.*;
import java.util.Properties;

/**
 * The HomeScreen class represents the starting screen of the game. It displays the
 * title, instructions, and background image, allowing the player to start the game by pressing ENTER.
 */
public class HomeScreen {

    private final Image BACKGROUND;
    private final Font TITLE_FONT;
    private final Font INSTRUCTION_FONT;
    private final String TITLE_TEXT;
    private final String INSTRUCTION_TEXT;
    private final double TITLE_Y;
    private final double INSTRUCTION_Y;

    /**
     * Constructs a new HomeScreen instance, initializing the background image, fonts,
     * and text for the title and instructions based on the provided game and message properties.
     *
     * @param gameProps Properties containing game-specific configurations such as background image and fonts.
     * @param messageProps Properties containing text messages such as title and instructions.
     */
    public HomeScreen(Properties gameProps, Properties messageProps) {

        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.home"));

        TITLE_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.title.fontSize")));
        TITLE_TEXT = messageProps.getProperty("home.title");
        TITLE_Y = Double.parseDouble(gameProps.getProperty("home.title.y"));

        INSTRUCTION_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.instruction.fontSize")));
        INSTRUCTION_TEXT = messageProps.getProperty("home.instruction");
        INSTRUCTION_Y = Double.parseDouble(gameProps.getProperty("home.instruction.y"));
    }

    /**
     * Show the home screen with the title and the background.
     * @param input The current mouse/keyboard input.
     * @return true if ENTER key is pressed, false otherwise.
     */
    public boolean update(Input input) {
        BACKGROUND.draw(Window.getWidth() /2.0, Window.getHeight() /2.0);
        TITLE_FONT.drawString(TITLE_TEXT, (Window.getWidth() - TITLE_FONT.getWidth(TITLE_TEXT)) / 2.0, TITLE_Y);
        INSTRUCTION_FONT.drawString(INSTRUCTION_TEXT, (Window.getWidth() -
                INSTRUCTION_FONT.getWidth(INSTRUCTION_TEXT)) / 2.0, INSTRUCTION_Y);

        return input.wasPressed(Keys.ENTER);
    }
}