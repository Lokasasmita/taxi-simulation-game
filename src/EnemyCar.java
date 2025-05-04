import java.util.Random;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * The EnemyCar class represents an enemy vehicle in the game that extends
 * the Car class. It has configurable properties such as speed, health,
 * and damage. The class also includes random speed generation for movement.
 */
public class EnemyCar extends Car{
    private static final Random random = new Random();

    /**
     * Constructs a new EnemyCar instance with the given position
     * Initializes the car's radius, health, damage, and speed.
     *
     * @param x The initial x-coordinate of the enemy car.
     * @param y The initial y-coordinate of the enemy car.
     * @param gameProps Properties containing enemy car-specific configurations.
     */
    public EnemyCar(double x, double y, Properties gameProps){
        super(x, y, gameProps.getProperty("gameObjects.enemyCar.image"));
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.radius")));
        this.setHealth(Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.health")));
        this.setDamage(Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.damage")));
        this.setSpeedX(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.speedX")));
        this.setSpeedY(getRandomSpeedY(gameProps));
    }

    /**
     * Generates a random speed for the enemy car along the Y-axis between the minimum
     * and maximum Y-axis speed values defined in the game properties.
     *
     * @param gameProps Properties containing the minimum and maximum Y-axis speed values.
     * @return A randomly generated Y-axis speed within the specified range.
     */
    public static double getRandomSpeedY(Properties gameProps) {
        double minSpeedY = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.minSpeedY"));
        double maxSpeedY = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.maxSpeedY"));
        return minSpeedY + (maxSpeedY - minSpeedY) * random.nextDouble();
    }

    /**
     * Updates the position of the enemy car by moving it along the Y-axis.
     * The car moves upwards based on its speed along the Y-axis.
     */
    @Override
    public void update(){
        setY(getY() - getSpeedY());
    }

    // Add this method to render fireballs
    @Override
    public void render() {
        super.render();
    }

    public boolean getAlive(){
        return getHealth() > 0;
    }
}
