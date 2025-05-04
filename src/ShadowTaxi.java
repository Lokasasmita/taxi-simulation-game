import bagel.*;
import java.util.Properties;

/**
 * Code for SWEN20003 Project 2, Semester 2, 2024
 * Bryan Alexander Lokasasmita - 1429095
 */
public class ShadowTaxi extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Game Screens
    private final HomeScreen homeScreen;
    private PlayerInfoScreen playerInfoScreen;
    private GamePlayScreen gamePlayScreen;
    private GameEndScreen gameEndScreen;

    private enum ScreenState {
        HOME, PLAYER_INFO, GAME_PLAY, GAME_END
    }

    private ScreenState currentScreen;

    /**
     * Constructs a new ShadowTaxi game instance with the given game and message properties.
     * Initializes the home screen and sets the initial screen state to HOME.
     *
     * @param gameProps Properties related to the game such as window size.
     * @param messageProps Properties related to in-game messages such as screen titles.
     */
    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // Initialize game screens
        homeScreen = new HomeScreen(gameProps, messageProps);

        // Start at the home screen
        currentScreen = ScreenState.HOME;
    }

    /**
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void
    update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // Switch between game screens
        switch (currentScreen) {
            case HOME:
                // Render the HomeScreen. Checks if ENTER is pressed. If so, switch to Player Info
                if (homeScreen.update(input)) {
                    playerInfoScreen = new PlayerInfoScreen(GAME_PROPS, MESSAGE_PROPS);
                    currentScreen = ScreenState.PLAYER_INFO;
                }
                break;

            case PLAYER_INFO:
                // Render the Player Info Screen. Checks if ENTER is pressed. If so, switch to Game Play Screen
                if (playerInfoScreen.update(input)) {
                    gamePlayScreen = new GamePlayScreen(GAME_PROPS, MESSAGE_PROPS, playerInfoScreen.getPlayerName());
                    currentScreen = ScreenState.GAME_PLAY;
                }
                break;

            case GAME_PLAY:
                if (gamePlayScreen.update(input)) {
                    // Render the Game Play Screen and handle the game end and transition to the Game End Screen
                    boolean won = gamePlayScreen.didPlayerWin();
                    gameEndScreen = new GameEndScreen(GAME_PROPS, MESSAGE_PROPS, won);
                    currentScreen = ScreenState.GAME_END;
                }
                break;

            case GAME_END:
                // Render the Game End Screen. If SPACE is pressed, go back to the Home Screen
                if (gameEndScreen.update(input)) {
                    currentScreen = ScreenState.HOME;
                }
                break;
        }
    }

    /**
     * The main method for launching the game.
     * Reads the game and message properties files and starts the game loop.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}
