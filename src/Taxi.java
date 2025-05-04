import java.util.Properties;

/**
 * The Taxi class represents the player's taxi in the game. It extends the Car class
 * and includes properties such as speed, health, and damage, all of which are initialized
 * based on the provided game properties.
 */
public class Taxi extends Car {

     /**
     * Constructs a new Taxi instance
     * Initializes the taxi's image, radius, speed, health, and damage based on the game properties.
     *
     * @param x The initial x-coordinate of the taxi.
     * @param y The initial y-coordinate of the taxi.
     * @param gameProps Properties containing taxi-specific configurations such as image, speed, and health.
     */
    public Taxi(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.taxi.image"));
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.radius")));
        this.setSpeedX(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.speedX")));
        this.setSpeedY(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.speedY")));
        this.setHealth(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")));
        this.setDamage(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")));
    }

    /**
     * Moves the taxi to the left by decreasing its x-coordinate by its speed along the X-axis.
     */
    public void moveLeft() {
        setX(this.getX() - getSpeedX());
    }

    /**
     * Moves the taxi to the right by increasing its x-coordinate by its speed along the X-axis.
     */
    public void moveRight() {
        setX(this.getX() + getSpeedX());
    }

    /**
     * Returns the speed of the taxi along the Y-axis.
     *
     * @return The taxi's speed along the Y-axis.
     */
    public double getSPEED_Y() {
        return this.getSpeedY();
    }

    /**
     * Updates the taxi's state. Currently, this method is empty but can be extended
     * to add update logic for the taxi.
     */
    @Override
    public void update() {
        // Logic for updating the taxi can be added here
    }
}
