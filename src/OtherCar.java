import java.util.Random;
import java.util.Properties;

/**
 * The OtherCar class represents a non-enemy car in the game. It extends the Car class
 * and has random speed and type selection for variation. These cars can interact with
 * the player and other entities in the game.
 */
public class OtherCar extends Car{
    private static final Random random = new Random();
    private static final int NUM_TYPES = 2; // Number of other car types

    /**
     * Constructs a new OtherCar instance with a randomly selected car type and speed,
     * initialized based on the given game properties.
     *
     * @param x The initial x-coordinate of the car.
     * @param y The initial y-coordinate of the car.
     * @param gameProps Properties containing car-specific configurations such as image, radius, speed, and health.
     */
    public OtherCar(double x, double y, Properties gameProps){
        super(x, y, String.format(gameProps.getProperty("gameObjects.otherCar.image"), getRandomCarType()));
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.radius")));
        this.setHealth(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.health")));
        this.setDamage(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.damage")));
        this.setSpeedX(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.speedX")));
        this.setSpeedY(getRandomSpeedY(gameProps));
    }

    private static int getRandomCarType() {
        return random.nextInt(NUM_TYPES) + 1; // Returns either 1 or 2
    }

    /**
     * Generates a random speed for the car along the Y-axis between the minimum
     * and maximum Y-axis speed values defined in the game properties.
     *
     * @param gameProps Properties containing the minimum and maximum Y-axis speed values.
     * @return A randomly generated Y-axis speed within the specified range.
     */
    public static double getRandomSpeedY(Properties gameProps) {
        double minSpeedY = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.minSpeedY"));
        double maxSpeedY = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.maxSpeedY"));
        return minSpeedY + (maxSpeedY - minSpeedY) * random.nextDouble();
    }

    /**
     * Updates the position of the car by moving it along the Y-axis.
     * The car moves upwards based on its speed along the Y-axis.
     */
    @Override
    public void update() {
        setY(getY() - getSpeedY());
    }
}
