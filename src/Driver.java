import java.util.Properties;

/**
 * The Driver class represents the player character in the game. It implements
 * the Singleton pattern to ensure that only one Driver instance is created.
 * The Driver can move in various directions, sync its position with a Taxi,
 * and handle collisions with other objects in the game.
 */
public class Driver extends GameEntity implements Collision{
    private static Driver instance;
    private int collisionDuration;
    private final int collisionTimeout;
    private final double WALK_SPEED_X;
    private final double WALK_SPEED_Y;
    private final double MAX_HEALTH;
    private double health;
    private boolean inTaxi = true; // driver starts inside the taxi in every world
    private boolean isDead = false;
    private boolean isInvincible = false;
    private int bounceTimeout;
    private final int bounceSpeed;
    private boolean lowerHigher = false;

    private Driver(double x, double y, Properties gameProps){
        super(x, y, gameProps.getProperty("gameObjects.driver.image"));
        this.collisionDuration = 0;
        this.collisionTimeout = 200;
        this.WALK_SPEED_X = Double.parseDouble(gameProps.getProperty("gameObjects.driver.walkSpeedX"));
        this.WALK_SPEED_Y = Double.parseDouble(gameProps.getProperty("gameObjects.driver.walkSpeedY"));
        this.MAX_HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.driver.health"));
        this.health = MAX_HEALTH;
        this.bounceTimeout = 0;
        this.bounceSpeed = 2;
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.driver.radius")));
    }

    /**
     * Returns the singleton instance of the Driver. If the instance does not
     * exist, it creates one.
     *
     * @param x The x-coordinate for the driver's initial position.
     * @param y The y-coordinate for the driver's initial position.
     * @param gameProps The properties for configuring the driver.
     * @return The singleton instance of the Driver.
     */
    public static Driver getInstance(double x, double y, Properties gameProps) {
        if (instance == null) {
            instance = new Driver(x, y, gameProps);
        }
        return instance;
    }

    /**
     * Moves the driver to the left by decreasing its x-coordinate.
     */
    public void moveLeft() {
        this.setX(this.getX() - WALK_SPEED_X);
    }

    /**
     * Moves the driver to the right by increasing its x-coordinate.
     */
    public void moveRight() {
        this.setX(this.getX() + WALK_SPEED_X);
    }

    /**
     * Moves the driver down by increasing its y-coordinate.
     */
    public void moveDown() {
        this.setY(this.getY() + WALK_SPEED_Y);
    }

    /**
     * Returns the driver's walking speed along the Y-axis.
     *
     * @return The speed of the driver along the Y-axis.
     */
    public double getSPEED_Y() {
        return this.WALK_SPEED_Y;
    }

    /**
     * Returns whether the driver is dead.
     *
     * @return true if the driver is dead, false otherwise.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Syncs the driver's position with the given Taxi while the driver is inside the taxi.
     *
     * @param taxi The Taxi object to sync the driver's position with.
     */
    public void syncWithTaxi(Taxi taxi) {
        if (inTaxi) {
            instance.setX(taxi.getX());
            instance.setY(taxi.getY());
        }
    }

    /**
     * Updates the driver's position when the taxi is destroyed, placing the driver outside the taxi.
     *
     * @param taxi The Taxi object that was destroyed.
     */
    public void taxiDestroyed(TaxiDamaged taxi) {
        inTaxi = false;
        instance.setX(taxi.getX() - 50);
        instance.setY(taxi.getY());
    }

    /**
     * Sets the driver's state to inside the taxi.
     *
     * @param taxi The Taxi object that the driver enters.
     */
    public void enterTaxi(Taxi taxi) {
        inTaxi = true;
    }

    /**
     * Returns whether the driver is currently inside the taxi.
     *
     * @return true if the driver is in the taxi, false otherwise.
     */
    public boolean isInTaxi() {
        return inTaxi;
    }

    /**
     * Sets the driver's invincibility status.
     *
     * @param invincible The new invincibility status.
     */
    public void setInvincible(boolean invincible) {
        this.isInvincible = invincible;
    }

    /**
     * Returns whether the driver is invincible.
     *
     * @return true if the driver is invincible, false otherwise.
     */
    public boolean getInvincible() {
        return this.isInvincible;
    }

    /**
     * Renders the driver if they are not inside the taxi and manages the invincibility state.
     */
    @Override
    public void render() {
        if (collisionDuration > 0) {
            collisionDuration--;
            if (collisionDuration == 0) {
                isInvincible = false;
            }
        }
        if (!inTaxi) {
            super.render(isInvincible);
        }
        updateBounceFrame();
    }

    /**
     * Updates the driver's bounce animation when in a bouncing state.
     */
    public void updateBounceFrame() {
        if (bounceTimeout > 0) {
            bounceTimeout--;
            if (lowerHigher) {
                this.setY(this.getY() + bounceSpeed);
            } else {
                this.setY(this.getY() - bounceSpeed);
            }
        }
    }

    /**
     * Revives the driver by resetting health to the maximum and changing the driver's state to alive.
     */
    public void revive() {
        this.health = MAX_HEALTH;
        isDead = false;
    }

    /**
     * Returns the driver's current health as a percentage.
     *
     * @return The driver's current health scaled to a percentage.
     */
    public double getHealth() {
        return (health * 100);
    }

    /**
     * Checks whether the driver has collided with a Taxi or Car.
     *
     * @param other The other game entity to check for a collision.
     * @return true if a collision occurred, false otherwise.
     */
    @Override
    public boolean checkCollision(Collision other) {
        if (other instanceof Taxi taxi){
            double distance = this.distanceTo(taxi.getPosition());
            return distance <= 10;
        } else if (other instanceof Car car){
            double distance = this.distanceTo(car.getPosition());
            double collisionRange = this.getRadius() + car.getRadius();
            return distance <= collisionRange;
        } else if (other instanceof Fireball fireball){
            double distance = this.distanceTo(fireball.getPosition());
            double collisionRange = this.getRadius() + fireball.getRadius();
            return distance <= collisionRange;
        }
        return false;
    }

    /**
     * Handles the driver's behavior when a collision occurs.
     * Updates the driver's health and invincibility state.
     *
     * @param other The other game entity involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        if (collisionDuration == 0) {
            if (other instanceof EnemyCar enemyCar){
                this.health -= enemyCar.getDamage();
            } else if (other instanceof OtherCar otherCar){
                this.health -= otherCar.getDamage();
            } else if (other instanceof Fireball fireball) {
                this.health -= fireball.getDamage();
            }
            if (this.health <= 0){
                this.isDead = true;
            }
            collisionDuration = collisionTimeout;
            isInvincible = true;
        }
    }

    /**
     * Initiates a bounce animation when the driver collides with another Car.
     *
     * @param other The other Car involved in the collision.
     */
    public void bounce(Collision other) {
        if (other instanceof Car otherCar) {
            bounceTimeout = 10;
            lowerHigher = this.getY() > otherCar.getY();
        }
    }
}
