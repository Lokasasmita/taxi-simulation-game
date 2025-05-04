import java.util.Properties;

/**
 * The TaxiDamaged class represents a damaged taxi in the game. It extends the Car class
 * and displays the damaged version of the taxi's image.
 */
public class TaxiDamaged extends Car {

    /**
     * Constructs a new TaxiDamaged instance
     * Initializes the damaged taxi's image based on the game properties.
     *
     * @param x The initial x-coordinate of the damaged taxi.
     * @param y The initial y-coordinate of the damaged taxi.
     * @param gameProps Properties containing the damaged taxi's image path.
     */
    public TaxiDamaged(double x, double y, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.taxi.damagedImage"));
    }

    /**
     * Renders the damaged taxi on the screen.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Updates the damaged taxi's state. Currently, this method is empty but can be extended
     * to add update logic for the damaged taxi.
     */
    @Override
    public void update() {
        // Logic for updating the damaged taxi can be added here
    }
}
