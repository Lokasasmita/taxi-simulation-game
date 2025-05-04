import bagel.util.Point;

import java.util.Properties;

/**
 * The TripEndFlag class represents the flag marking the end of a passenger's trip.
 * It checks whether the taxi has reached the flag and associates the flag with a specific passenger.
 */
public class TripEndFlag extends GameEntity{
    private final Passenger passenger;

    /**
     * Constructs a new TripEndFlag instance at the given position for the specified passenger,
     * initializing the flag's image and radius based on the game properties.
     *
     * @param x The x-coordinate of the flag.
     * @param y The y-coordinate of the flag.
     * @param passenger The passenger associated with this flag.
     * @param gameProps Properties containing configurations such as the flag's image and radius.
     */
    public TripEndFlag(double x, double y, Passenger passenger, Properties gameProps) {
        super(x, y, gameProps.getProperty("gameObjects.tripEndFlag.image"));
        this.passenger = passenger;
        this.setRadius(Double.parseDouble(gameProps.getProperty("gameObjects.tripEndFlag.radius")));
    }

    /**
     * Checks if the taxi is at the flag to drop off the passenger. This method compares the
     * taxi's position with the flag's position and determines if the taxi is within the flag's radius.
     *
     * @param taxiPosition The position of the taxi.
     * @return true if the taxi is at the flag, false otherwise.
     */
    public boolean atFlag(Point taxiPosition) {
        return (distanceTo(taxiPosition) <= getRadius()) || (taxiPosition.y < getY());
    }

    /**
     * Checks if this flag is associated with the specified passenger.
     *
     * @param passenger The passenger to check.
     * @return true if this flag is for the specified passenger, false otherwise.
     */
    public boolean isForPassenger(Passenger passenger) {
        return this.passenger == passenger;
    }

}
