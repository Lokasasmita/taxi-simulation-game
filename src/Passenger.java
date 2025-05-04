import bagel.Font;
import bagel.util.Point;
import java.util.Properties;

/**
 * The Passenger class represents a passenger in the game. Passengers can interact with taxis,
 * be picked up or dropped off, and have various attributes such as priority, health, and walking speed.
 */
public class Passenger extends GameEntity implements Collision {
    private int priority;
    private double health;
    private final int originalPriority;
    private final double WALK_SPEED_X;
    private final double WALK_SPEED_Y;
    private final double Y_DIST;
    private final double END_X;
    private final Font FONT;
    private final Trip TRIP;
    private final int hasUmbrella;
    private boolean inTaxi;
    private boolean atDestination;
    private boolean onGoingTrip;
    private boolean priorityAdjusted = false;
    private boolean isDead = false;
    private int collisionDuration = 0;
    private int collisionTimeout = 200;

    /**
     * Constructs a new Passenger instance with the given properties.
     *
     * @param x The initial x-coordinate of the passenger.
     * @param y The initial y-coordinate of the passenger.
     * @param priority The priority level of the passenger.
     * @param endX The x-coordinate of the passenger's destination.
     * @param yDistance The y-distance the passenger must travel.
     * @param hasUmbrella Indicates whether the passenger has an umbrella.
     * @param gameProps Properties containing game configurations such as walking speed and health.
     */
    public Passenger(double x, double y, int priority, double endX, double yDistance, int hasUmbrella,
                     Properties gameProps){
        super(x, y, gameProps.getProperty("gameObjects.passenger.image"));
        this.priority = priority;
        this.originalPriority = priority;
        this.END_X = endX;
        this.Y_DIST = yDistance;
        this.hasUmbrella = hasUmbrella;
        this.TRIP = new Trip(this, null, gameProps);
        this.FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize")));
        this.WALK_SPEED_X = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.walkSpeedX"));
        this.WALK_SPEED_Y = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.walkSpeedY"));
        this.health = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health"));
    }

    /**
     * Renders the passenger and their details such as priority and expected earnings if they are
     * idle or at their destination. If the passenger is not in the taxi or has reached the destination,
     * it displays their information on the screen.
     */
    @Override
    public void render(){
        if (collisionDuration > 0){
            collisionDuration--;
        }
        if (!inTaxi || atDestination) {
            super.render();

            // If idle
            if (!atDestination && !onGoingTrip) {
                // Render priority
                FONT.drawString(Integer.toString(priority), getX() - 30, getY());

                // Render expected trip earnings
                double expectedEarnings = TRIP.calculateEarnings();
                FONT.drawString(String.format("%.1f", expectedEarnings), getX() - 100, getY());
            }
        }
    }

    /* Passenger pickup logic */
    /**
     * Picks up the passenger, marking them as being in a taxi and starting their trip.
     */
    public void pickUp(){
        this.inTaxi = true;
        this.onGoingTrip = true;
    }

    /**
     * Drops off the passenger, marking them as having reached their destination.
     */
    public void dropOff(){
        this.inTaxi = false;
        this.atDestination = true;
        this.onGoingTrip = false;
    }

    /* Passenger movement */
    /**
     * Moves the passenger towards the target point at the specified speed along the X and Y axes.
     *
     * @param target The target point to move towards.
     * @param speedX The speed along the X-axis.
     * @param speedY The speed along the Y-axis.
     */
    public void moveTowards(Point target, double speedX, double speedY) {
        double distance = this.distanceTo(target);

        // Move towards the target if not already close enough
        if (distance > Math.max(speedX, speedY)) {
            // Calculate movement direction and update position
            double deltaX = (target.x - getX()) / distance * speedX;
            double deltaY = (target.y - getY()) / distance * speedY;
            this.setX(this.getX() + deltaX);
            this.setY(this.getY() + deltaY);
        } else {
            // Snap to the target if within one step's reach
            this.setX(target.x);
            this.setY(target.y);
        }
    }

    /**
     * Moves the passenger towards the taxi. If the passenger reaches the taxi,
     * they are picked up.
     *
     * @param taxiPosition The position of the taxi.
     */
    public void moveToTaxi(Point taxiPosition) {
        if (!inTaxi && !atDestination) {
            moveTowards(taxiPosition, WALK_SPEED_X, WALK_SPEED_Y);

            // Check if the passenger is close enough to the taxi to be picked up
            if (distanceTo(taxiPosition) <= 1) {
                this.setX(taxiPosition.x); // Follow the taxi's position
                pickUp();
            }
        }
    }

    /**
     * Moves the passenger towards the trip end flag if they are at the destination.
     *
     * @param flagPosition The position of the flag to move towards.
     */
    public void moveToEndFlag(Point flagPosition) {
        if (atDestination) {
            moveTowards(flagPosition, WALK_SPEED_X, WALK_SPEED_Y);
        }
    }

    /**
     * Checks whether the passenger has reached the trip end flag.
     *
     * @param flagPosition The position of the flag to check against.
     * @return true if the passenger has reached the flag, false otherwise.
     */
    public boolean reachedFlag(Point flagPosition) {
        // Check if the passenger's coordinates match the flag's coordinates
        return distanceTo(flagPosition) <= 1;
    }

    /**
     * Syncs the passenger's position with the driver's position when the taxi is destroyed.
     *
     * @param driver The driver to sync the position with.
     */
    public void syncDriver(Driver driver){
        //this function is only called when the taxi is destroyed
        //sync the passenger's position with the driver's position
        this.setX(driver.getX()-50);
        this.setY(driver.getY());
    }

    /* Priority Management */

    /**
     * Adjusts the passenger's priority based on the weather. If it's raining and
     * the passenger does not have an umbrella, their priority is set to 1.
     *
     * @param isRaining true if it's raining, false otherwise.
     */
    public void adjustPriorityForWeather(boolean isRaining) {
        if (isRaining && hasUmbrella == 0) {
            this.priority = 1;  // Set priority to 1 if raining and no umbrella
        } else {
            // Revert to original priority when it's sunny and adjust for coin power
            if (GamePlayScreen.isCoinPowered() && originalPriority > 1){
                this.priority = originalPriority-1;
            } else {
                this.priority = originalPriority;
            }
        }
    }

    /**
     * Increases the passenger's priority by decreasing the priority value, but only if it hasn't
     * been adjusted before.
     */
    public void increasePriority(){
        if(priority>1 && !priorityAdjusted){
            priority--;
            priorityAdjusted = true;
        }
    }

    /**
     * Decreases the passenger's priority by restoring the original value if it was previously adjusted.
     */
    public void decreasePriority(){
        if(priorityAdjusted){
            priority++;
            priorityAdjusted = false;
        }
    }

    /**
     * Handles the situation where the taxi is destroyed, throwing the passenger out of the taxi.
     *
     * @param taxiDamaged The damaged taxi.
     */
    public void taxiDestroyed(TaxiDamaged taxiDamaged){
        //if the taxi is destroyed, the passenger will be thrown out of the taxi
        this.inTaxi = false;
        this.setX(taxiDamaged.getX()-100);
        this.setY(taxiDamaged.getY());
    }

    /**
     * Checks whether the passenger has collided with another entity.
     *
     * @param other The other entity to check for a collision.
     * @return true if the passenger collided with a car, false otherwise.
     */
    @Override
    public boolean checkCollision(Collision other) {
        if (other instanceof Car car){
            double distance = this.distanceTo(car.getPosition());
            double collisionRange = this.getRadius() + car.getRadius();
            return distance <= collisionRange;
        }
        return false;
    }

    /**
     * Handles the collision between the passenger and another entity (Car),
     * updating the passenger's health if hit by a car.
     *
     * @param other The other entity involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        if (collisionDuration == 0){
            if (other instanceof OtherCar otherCar){
                this.health -= otherCar.getDamage();
            } else if (other instanceof EnemyCar enemyCar){
                this.health -= enemyCar.getDamage();
            }

            collisionDuration = collisionTimeout;
        }
        if (this.health <= 0){
            isDead = true;
        }
    }

    /* Getters */

    /**
     * Returns whether the passenger is currently in a taxi.
     *
     * @return true if the passenger is in a taxi, false otherwise.
     */
    public boolean isInTaxi() {
        return inTaxi;
    }

    /**
     * Returns whether the passenger is at their destination.
     *
     * @return true if the passenger is at their destination, false otherwise.
     */
    public boolean isAtDestination() {
        return atDestination;
    }

    /**
     * Returns the x-coordinate of the passenger's destination.
     *
     * @return The x-coordinate of the destination.
     */
    public double getEndX() {
        return END_X;
    }

    /**
     * Returns the y-distance the passenger must travel.
     *
     * @return The y-distance the passenger must travel.
     */
    public double getYDistance() {
        return Y_DIST;
    }

    /**
     * Returns the priority level of the passenger.
     *
     * @return The priority level.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the health of the passenger as a percentage.
     *
     * @return The passenger's health as a percentage.
     */
    public double getHealth() {
        return (health * 100);
    }

    /**
     * Returns whether the passenger is dead.
     *
     * @return true if the passenger is dead, false otherwise.
     */
    public boolean isDead(){
        return isDead;
    }
}
