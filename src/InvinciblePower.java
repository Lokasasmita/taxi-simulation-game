import java.util.Properties;

/**
 * The InvinciblePower class represents a collectible power-up in the game that
 * grants invincibility to the Taxi or Driver. It implements the Collision interface
 * to detect when it is collected by the player.
 */
public class InvinciblePower extends GameEntity implements Collision {
    private final double RADIUS;
    private boolean isCollected = false;

    /**
     * Constructs a new InvinciblePower instance with the given position and game properties.
     * Initializes the power-up's image and radius.
     *
     * @param x The initial x-coordinate of the power-up.
     * @param y The initial y-coordinate of the power-up.
     * @param gameProps Properties containing the power-up's image path and radius.
     */
    public InvinciblePower(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.invinciblePower.image"));
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.invinciblePower.radius"));
    }

    /**
     * Renders the invincibility power-up on the screen if it has not been collected.
     */
    @Override
    public void render() {
        if (!isCollected) {
            super.render();
        }
    }

    /**
     * Checks whether the invincibility power-up has collided with a Taxi or Driver.
     *
     * @param other The other game entity to check for a collision.
     * @return true if the power-up collides with a Taxi or Driver, false otherwise.
     */
    @Override
    public boolean checkCollision(Collision other) {
        if (other instanceof Taxi taxi && !isCollected) {
            double distance = this.distanceTo(taxi.getPosition());
            double collisionRange = RADIUS + taxi.getRadius();
            return distance <= collisionRange;
        } else if (other instanceof Driver driver && !isCollected) {
            double distance = this.distanceTo(driver.getPosition());
            double collisionRange = RADIUS + driver.getRadius();
            return distance <= collisionRange;
        }
        return false;
    }

    /**
     * Handles the event when the invincibility power-up is collected by setting
     * its collected state to true.
     *
     * @param other The other game entity involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        isCollected = true;
    }
}
