import bagel.*;
import bagel.util.Colour;
import java.util.Properties;

/**
 * The PlayerInfoScreen class represents the screen where the player enters their name
 * before starting the game. It allows the player to input their name and displays instructions
 * for starting the game.
 */
public class PlayerInfoScreen {
    private final Image BACKGROUND;
    private final Font FONT;
    private final String ENTER_NAME_TEXT;
    private final String START_INSTRUCTIONS;
    private String playerName;
    private final double ENTER_NAME_Y;
    private final double PLAYER_NAME_Y;
    private final double START_INSTRUCTIONS_Y;

    /**
     * Constructs a new PlayerInfoScreen instance with the given game and message properties.
     * Initializes the background, fonts, and instructions displayed to the player.
     *
     * @param gameProps Properties containing game-specific configurations such as background image and font size.
     * @param messageProps Properties containing text messages such as instructions and name prompt.
     */
    public PlayerInfoScreen(Properties gameProps, Properties messageProps) {

        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.playerInfo"));
        FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("playerInfo.fontSize")));

        ENTER_NAME_TEXT = messageProps.getProperty("playerInfo.playerName");
        START_INSTRUCTIONS = messageProps.getProperty("playerInfo.start");

        ENTER_NAME_Y = Double.parseDouble(gameProps.getProperty("playerInfo.playerName.y"));
        PLAYER_NAME_Y = Double.parseDouble(gameProps.getProperty("playerInfo.playerNameInput.y"));
        START_INSTRUCTIONS_Y = Double.parseDouble(gameProps.getProperty("playerInfo.start.y"));

        playerName = "";
    }

    /**
     * Show the player info screen with the input entered for the player name and the start game message.
     * @param input The current mouse/keyboard input.
     * @return true if ENTER key is pressed, false otherwise.
     */
    public boolean update(Input input) {
        BACKGROUND.draw(Window.getWidth()/2.0, Window.getHeight() /2.0);

        FONT.drawString(ENTER_NAME_TEXT, (Window.getWidth() - FONT.getWidth(ENTER_NAME_TEXT)) / 2.0, ENTER_NAME_Y);
        FONT.drawString(START_INSTRUCTIONS, (Window.getWidth() - FONT.getWidth(START_INSTRUCTIONS)) / 2.0,
                START_INSTRUCTIONS_Y);

        // Render the player name input
        DrawOptions blackText = new DrawOptions().setBlendColour(Colour.BLACK);
        double playerNameX = (Window.getWidth() - FONT.getWidth(playerName)) / 2.0;
        FONT.drawString(playerName, playerNameX, PLAYER_NAME_Y, blackText);

        // Handle user input for player name using MiscUtils
        if (input.wasPressed(Keys.BACKSPACE) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1);
        } else {
            String keyPressed = MiscUtils.getKeyPress(input);
            if (keyPressed != null && playerName.length() < 20) { // Limit name length to 20 characters
                playerName += keyPressed;
            }
        }

        return input.wasPressed(Keys.ENTER) && !playerName.isEmpty();
    }

    /**
     * Returns the player's name that was entered on the screen.
     *
     * @return The player's name as a string.
     */
    public String getPlayerName() {
        return playerName;
    }
}