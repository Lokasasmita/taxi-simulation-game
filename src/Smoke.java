import java.util.Properties;

/**
 * The Smoke class represents a smoke effect in the game, which has a limited time-to-live (TTL).
 * It moves downwards over time and disappears after its TTL expires.
 */
public class Smoke extends GameEntity{
    private int ttl; // time to live

    /**
     * Constructs a new Smoke instance with the given position and game properties.
     * Initializes the smoke image and TTL based on the provided game properties.
     *
     * @param x The initial x-coordinate of the smoke.
     * @param y The initial y-coordinate of the smoke.
     * @param gameProps Properties containing the smoke's image path and TTL value.
     */
    public Smoke(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.smoke.image"));
        this.ttl = Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl"));
    }

    /**
     * Renders the smoke on the screen, moving it down by 5 units per frame.
     * The smoke is rendered only if its TTL is greater than zero.
     */
    public void render() {
        if (ttl > 0) {
            super.render();
            this.setY(this.getY() + 5); // move down by 5 each frame
            ttl--;
        }
    }
}
