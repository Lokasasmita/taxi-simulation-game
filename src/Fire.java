import java.util.Properties;

/**
 * The Fire class represents the fire that appears when a car takes
 * critical damage and dies on collision.
 * It extends GameEntity and includes a time-to-live (ttl) property,
 * determining how long the fire will remain on the screen before disappearing.
 */
public class Fire extends GameEntity {
    private int ttl; // time to live

    /**
     * Constructs a new Fire instance
     * Initializes the fire's image and ttl from the provided properties.
     *
     * @param x The initial x-coordinate of the fire.
     * @param y The initial y-coordinate of the fire.
     * @param gameProps Properties containing the fire's image path and time-to-live (ttl) value.
     */
    public Fire(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.fire.image"));
        this.ttl = Integer.parseInt(gameProps.getProperty("gameObjects.fire.ttl"));
    }

    /**
     * Renders the fire on the screen if its time-to-live (ttl) is greater than zero.
     * Moves the fire down by 5 units per frame and decreases its ttl.
     */
    @Override
    public void render() {
        if (ttl > 0) {
            super.render();
            this.setY(this.getY() + 5); // move down by 5 each frame
            ttl--;
        }
    }
}
