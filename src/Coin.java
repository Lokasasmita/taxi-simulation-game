import java.util.Properties;

/**
 * The Coin class represents a collectible object in the game that extends
 * GameEntity and implements the Collision interface. It can be collected by
 * the player (Taxi or Driver) to provide a Coin Power.
 */
public class Coin extends GameEntity implements Collision{
    private final double RADIUS;
    private boolean isCollected = false;

    /**
     * Constructs a new Coin instance with the given initial position
     *
     * @param x The initial x-coordinate of the coin.
     * @param y The initial y-coordinate of the coin.
     * @param gameProps Properties containing the image path and radius of the coin.
     */
    public Coin(double x, double y, Properties gameProps){
        super(x, y, gameProps.getProperty("gameObjects.coin.image"));
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius"));
    }

    /**
     * Renders the coin on the screen if it has not been collected.
     */
    @Override
    public void render() {
        if (!isCollected) {
            super.render();
        }
    }

    /**
     * Checks whether this coin has collided with a Taxi or Driver.
     * If a collision occurs and the coin has not been collected, it returns true.
     *
     * @param other The other game entity to check for a collision.
     * @return true if the coin collides with a Taxi or Driver, false otherwise.
     */
    @Override
    public boolean checkCollision(Collision other) {
        if (other instanceof Taxi taxi && !isCollected) {
            // Cast the Collision to Taxi
            double distance = this.distanceTo(taxi.getPosition());
            double collisionRange = RADIUS + taxi.getRadius();

            return distance <= collisionRange;
        } else if (other instanceof Driver driver && !isCollected) {
            // Cast the Collision to Driver
            double distance = this.distanceTo(driver.getPosition());
            double collisionRange = RADIUS + driver.getRadius();
            return distance <= collisionRange;
        }
        return false;
    }

    /**
     * Handles the event when the coin is collected by setting its collected state to true.
     *
     * @param other The other game entity involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        isCollected = true;
    }
}
