import bagel.util.Point;
import java.lang.Math;
import java.util.Properties;


/**
 * The Trip class represents a passenger's trip in the game, managing earnings, penalties,
 * and completion status. It calculates the profit based on the distance and passenger priority.
 */
public class Trip {
    private final Properties GAME_PROPS;
    private final double DISTANCE_RATE;
    private final double PENALTY_RATE;
    private final Passenger passenger;
    private final TripEndFlag tripEndFlag;
    private double earnings;
    private double penalty;
    private double profit;
    private boolean isCompleted = false;

    /**
     * Constructs a new Trip instance for the given passenger and trip end flag, and initializes the
     * trip's earnings and rates based on the game properties.
     *
     * @param passenger The passenger associated with the trip.
     * @param tripEndFlag The flag representing the passenger's destination.
     * @param gameProps Properties containing the trip's rate configurations such as distance rate and penalty rate.
     */
    public Trip(Passenger passenger, TripEndFlag tripEndFlag, Properties gameProps) {
        this.GAME_PROPS = gameProps;
        this.passenger = passenger;
        this.tripEndFlag = tripEndFlag;
        this.earnings = calculateEarnings();

        DISTANCE_RATE = Double.parseDouble(gameProps.getProperty("trip.rate.perY"));
        PENALTY_RATE = Double.parseDouble(gameProps.getProperty("trip.penalty.perY"));
    }

    /**
     * Calculates the initial expected earnings at the start of the trip based on the
     * passenger's distance to the destination and priority.
     *
     * @return The calculated earnings for the trip.
     */
    public double calculateEarnings() {
        double distanceFee = passenger.getYDistance() * DISTANCE_RATE;
        double priorityFee = priorityRate(passenger.getPriority()) * passenger.getPriority();
        return distanceFee + priorityFee;
    }

    /**
     * Updates the earnings of the trip whenever the passenger's priority changes.
     */
    public void updateEarnings() {
        this.earnings = calculateEarnings();
    }

    private double calculatePenalty(Point taxiPos) {
        if (taxiPos.y < tripEndFlag.getPosition().y && tripEndFlag.atFlag(taxiPos)) {
            // Calculate the overshoot distance using distanceTo method
            double overshootDist = tripEndFlag.getPosition().distanceTo(taxiPos);
            return overshootDist * PENALTY_RATE;
        }
        return 0.0;
    }

    /**
     * Completes the trip and calculates the penalty based on the taxi's final position.
     * The profit is calculated as earnings minus the penalty, but it cannot be negative.
     *
     * @param taxiPos The final position of the taxi when the trip is completed.
     */
    public void completeTrip(Point taxiPos){
        penalty = calculatePenalty(taxiPos);
        profit = Math.max(earnings - penalty, 0);
        isCompleted = true;
    }

    private double priorityRate(int priority){
        return  switch (priority) {
            case 1 -> Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority1"));
            case 2 -> Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority2"));
            case 3 -> Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority3"));
            default -> 0.0;
        };
    }

    /* Getters */

    /**
     * Returns the profit for the trip, calculated as earnings minus penalty.
     *
     * @return The profit from the trip.
     */
    public double getProfit() {
        return profit;
    }

    /**
     * Returns whether the trip has been completed.
     *
     * @return true if the trip is completed, false otherwise.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Returns the penalty incurred during the trip.
     *
     * @return The penalty amount.
     */
    public double getPenalty() {
        return penalty;
    }

    /**
     * Returns the current earnings for the trip.
     *
     * @return The earnings amount.
     */
    public double getEarnings() {
        return earnings;
    }

    /**
     * Returns the passenger associated with the trip.
     *
     * @return The passenger of the trip.
     */
    public Passenger getPassenger() {
        return passenger;
    }
}
