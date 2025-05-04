import java.util.Properties;

/**
 * The Fireball class represents a projectile in the game that can inflict damage upon collision.
 * It moves upwards on the screen and disappears when it collides with another object.
 */
public class Fireball extends GameEntity implements Collision{
    private final double damage;
    private final double speedY;
    private boolean alive;

    /**
     * Constructs a new Fireball instance at the given position and initializes its properties such as
     * radius, damage, speed, and image based on the provided game properties.
     *
     * @param x The initial x-coordinate of the fireball.
     * @param y The initial y-coordinate of the fireball.
     * @param gameProps Properties containing fireball-specific configurations such as image, radius, and damage.
     */
    public Fireball(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.fireball.image"));
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius")));
        this.damage = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage"));
        this.speedY = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));
        this.alive = true;
    }

    /**
     * Renders the fireball on the screen if it is still alive.
     * The fireball moves upwards based on its speed.
     */
    @Override
    public void render() {
        if (alive) {
            super.render();
        }
        this.setY(this.getY() - speedY);
    }

    /**
     * Checks if the fireball has collided with another object.
     *
     * @param other The other object to check for collision.
     * @return Currently always returns false since it's not being used.
     * Gives no effects, because we check fireball's collision through the other classes
     * Just for the override requirement. Give no effect to the game
     */
    @Override
    public boolean checkCollision(Collision other) {
        return false;
    }

    /**
     * Handles the event when the fireball collides with another object.
     * The fireball is marked as no longer alive and moved off-screen.
     *
     * @param other The other object involved in the collision.
     */
    @Override
    public void onCollision(Collision other) {
        alive = false;
        //set off screen
        this.setY(-20);
        this.setX(-20);
    }

    /**
     * Returns the damage inflicted by the fireball upon collision.
     *
     * @return The damage value of the fireball.
     */
    public double getDamage() {
        return this.damage;
    }

    /**
     * Returns whether the fireball is still active (alive) in the game.
     *
     * @return true if the fireball is alive, false otherwise.
     */
    public boolean getAlive() {
        return alive;
    }
}
