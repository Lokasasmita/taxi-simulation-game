/**
 * The abstract Car class represents a vehicle in the game that extends
 * GameEntity and implements the Collision interface. It includes properties
 * for speed, health, damage, and collision behavior.
 */
public abstract class Car extends GameEntity implements Collision {
    private double speedX;
    private double speedY;
    private double health;
    private double damage;
    private int collisionDuration;
    private int collisionTimeout;
    private double collisionDamage;
    private int bounceTimeout;
    private int bounceSpeed;
    private boolean lowerHigher;
    private boolean isInvincible = false;
    private boolean isDestroyed = false;
    private boolean revive = false;

    /**
     * Constructs a new Car instance with the given initial position and image path.
     *
     * @param x The initial x-coordinate of the car.
     * @param y The initial y-coordinate of the car.
     * @param imagePath The path to the image representing the car.
     */
    public Car(double x, double y, String imagePath) {
        super(x, y, imagePath);
        this.collisionTimeout = 200;
        this.collisionDuration = 0;
        this.bounceTimeout = 0;    
        this.bounceSpeed = 1;
        this.lowerHigher = false;
    }

    /**
     * Renders the car, handles collision behavior, and manages the bounce animation.
     */
    @Override
    public void render() {
        if (collisionDuration > 0) {
            collisionDuration--;
            if (collisionDuration == 0){
                isInvincible = false;
                revive = true;
            }
        } else{
            revive = false;
        }
        if (!isDestroyed) {
            super.render(isInvincible);
        }

        updateBounceFrame();
    }

    /**
     * Checks whether this car has collided with another game entity.
     *
     * @param other The other game entity to check for a collision.
     * @return true if a collision occurred, false otherwise.
     */
    @Override
    public boolean checkCollision(Collision other) {
        if (other instanceof Car otherCar) {
            double distance = this.distanceTo(otherCar.getPosition());
            double collisionRange = this.getRadius() + otherCar.getRadius();
            return distance <= collisionRange;
        } else if (other instanceof Driver driver) {
            double distance = this.distanceTo(driver.getPosition());
            double collisionRange = this.getRadius() + driver.getRadius();
            return distance <= collisionRange;
        } else if (other instanceof Fireball fireball) {
            double distance = this.distanceTo(fireball.getPosition());
            double collisionRange = this.getRadius() + fireball.getRadius();
            return distance <= collisionRange;
        }
        return false;
    }

    /**
     * Executes collision behavior when this car collides with another collidable entity.
     * Updates health, invincibility, and destruction status.
     *
     * @param other The other game entity involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        if(!isInvincible){
            if (collisionDuration == 0) {
                if (other instanceof Taxi taxi) {
                    this.health -= taxi.getDamage();
                    this.bounceSpeed = 1;
                }
                else if (other instanceof EnemyCar enemyCar) {
                    this.health -= enemyCar.getDamage();
                    this.bounceSpeed = 1;
                }
                else if (other instanceof OtherCar otherCar) {
                    this.health -= otherCar.getDamage();
                    this.bounceSpeed = 1;
                } else if (other instanceof Driver) {
                    this.bounceSpeed = 2;
                } else if (other instanceof Fireball fireball) {
                    this.health -= fireball.getDamage();
                    this.bounceSpeed = 1;
                }

                collisionDuration = collisionTimeout;
                isInvincible = true;
            }
            if (this.getClass() != Taxi.class) {
                this.speedY = 0;
                //set to off screen, so it doesn't collide with other cars on the street
                if (this.health <= 0) {
                    this.setY(-50);
                    this.setX(-50);
                    this.isInvincible = true; // so it doesn't need to spawn smoke
                }
            }
            if (this.health <= 0) {
                isDestroyed = true;
            }
        }
    }

    /**
     * Handles the bounce animation when this car collides with another collidable entity.
     *
     * @param other The other collidable game entity involved in the collision.
     */
    public void bounce(Collision other){
        if (other instanceof Car otherCar) {
            bounceTimeout = 10;
            if (this.getY() > otherCar.getY()){
                this.lowerHigher = true;
            } else {
                this.lowerHigher = false;
            }
        } else if (other instanceof Driver driver) {
            bounceTimeout = 10;
            if (this.getY() > driver.getY()){
                this.lowerHigher = true;
            } else {
                this.lowerHigher = false;
            }
        }
    }

    /**
     * Updates the frame for the bounce animation when the car is in a bouncing state.
     */
    public void updateBounceFrame(){
        if (bounceTimeout > 0){
            bounceTimeout--;
            if (lowerHigher){
                this.setY(this.getY() + bounceSpeed);
            } else {
                this.setY(this.getY() - bounceSpeed);
            }
        }
    }

    /**
     * Returns whether it is time to spawn smoke for the car, based on the collision state.
     *
     * @return true if it is time to spawn smoke, false otherwise.
     */
    public boolean isTimeToSpawnSmoke() {
        return collisionDuration == 0;
    }

    /**
     * Abstract method to be implemented by subclasses to update the car's state.
     */
    public abstract void update();

    /* Getters and setters */
    /**
     * Returns the car's health
     *
     * @return The car's health scaled by 100
     */
    public double getHealth() {
        return (health * 100);
    }

    /**
     * Returns whether the car is destroyed.
     *
     * @return true if the car is destroyed, false otherwise.
     */
    public boolean getDestroyed() {
        return isDestroyed;
    }

    /**
     * Returns the amount of damage the car can inflict.
     *
     * @return The damage value of the car.
     */
    public double getDamage() {
        return this.damage;
    }

    /**
     * Returns the car's speed along the X-axis.
     *
     * @return The speed of the car along the X-axis.
     */
    public double getSpeedX() {
        return this.speedX;
    }

    /**
     * Returns the car's speed along the Y-axis.
     *
     * @return The speed of the car along the Y-axis.
     */
    public double getSpeedY() {
        return this.speedY;
    }

    /**
     * Returns whether the car is currently invincible.
     *
     * @return true if the car is invincible, false otherwise.
     */
    public boolean getInvincible() {
        return this.isInvincible;
    }

    /**
     * Returns whether the car is in a revived state after a collision.
     *
     * @return true if the car is revived, false otherwise.
     */
    public boolean getRevive() {
        return this.revive;
    }

    /**
     * Sets the car's health.
     *
     * @param health The new health value for the car.
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Sets the damage value the car can inflict.
     *
     * @param damage The new damage value for the car.
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Sets the speed of the car along the X-axis.
     *
     * @param speedX The new speed along the X-axis.
     */
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    /**
     * Sets the speed of the car along the Y-axis.
     *
     * @param speedY The new speed along the Y-axis.
     */
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    /**
     * Sets the invincibility status of the car.
     *
     * @param invincible The new invincibility status of the car.
     */
    public void setInvincible(boolean invincible) {
        this.isInvincible = invincible;
    }
}
