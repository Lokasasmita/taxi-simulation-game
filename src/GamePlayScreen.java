import bagel.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The GamePlayScreen class represents the main gameplay screen and manages
 * various game entities, player interactions, and the game state.
 */
public class GamePlayScreen {
    private final Properties gameProps;
    private final Properties messageProps;
    private Random random = new Random();

    // Background
    private Image BACKGROUND;
    private final Image sunnyBackground;
    private final Image rainyBackground;
    private double bg_Y = 0;
    private double SCROLL_SPEED;
 
    private boolean isRaining;

    // Game Entities
    private Taxi taxi;
    
    private Driver driver;
    private Passenger currPassenger;
    private Passenger droppedPassenger;
    private boolean taxiIsMoving = false;
    private final double TAXI_DETECT_RADIUS;

    private final List<InvinciblePower> INVINCIBLE_POWERS = new ArrayList<>();
    private final List<Coin> COINS = new ArrayList<>();
    private final List<Passenger> PASSENGERS = new ArrayList<>();
    private final List<TripEndFlag> tripEndFlags = new ArrayList<>();
    private final List<Car> CARS = new ArrayList<>();
    private final List<TaxiDamaged> destroyedTaxis = new ArrayList<>();
    private final List<Smoke> SMOKES = new ArrayList<>();
    private final List<Fire> FIRES = new ArrayList<>();
    private final List<Fireball> FIREBALLS = new ArrayList<>();
    private final String[][] WEATHER_CONDITIONS;
    private int currentWeatherIndex = 0;

    // Road Lanes
    private final double roadLaneCenter1, roadLaneCenter2, roadLaneCenter3;

    // Trip management
    private Trip currTrip;
    private Trip lastTrip = null;
    private final int TRIP_DETAILS_X;
    private final int TRIP_DETAILS_Y;

    // Text on screen
    private final Font INFO_FONT;

    // Player Stats
    private boolean playerWon = false;
    private final String SCORES_FILE;
    private final String PLAYER_NAME;

    private double playerScore = 0.0;
    private final int SCORE_X;
    private final int SCORE_Y;

    private final double TARGET_SCORE;
    private final int TARGET_X;
    private final int TARGET_Y;

    private final int TAXI_HEALTH_X;
    private final int TAXI_HEALTH_Y;
    private final int DRIVER_HEALTH_X;
    private final int DRIVER_HEALTH_Y;
    private final int PASSENGER_HEALTH_X;
    private final int PASSENGER_HEALTH_Y;

    // Frames
    private int frameCounter = 0;
    private final int MAX_FRAMES;
    private final int FRAMES_X;
    private final int FRAMES_Y;

    // Coin Power
    private static boolean coinPowered = false;
    private int coinPowerFrames = 0;
    private final int COIN_POWER_DURATION;
    private final int COIN_POWER_FRAMES_X;
    private final int COIN_POWER_FRAMES_Y;

    // Invincible Power
    private static boolean invinciblePowered = false;
    private int invinciblePowerFrames = 0; 
    private final int INVINCIBLE_POWER_DURATION;

    /**
     * Constructs a new GamePlayScreen instance with the given game and message properties.
     * It initializes game entities and configurations based on the provided properties.
     *
     * @param gameProps Properties containing game-specific configurations such as images, fonts, and scores.
     * @param messageProps Properties containing in-game messages such as text for earnings and health.
     * @param playerName The name of the player.
     */
    public GamePlayScreen(Properties gameProps, Properties messageProps, String playerName) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.sunny"));
        sunnyBackground = new Image(gameProps.getProperty("backgroundImage.sunny"));
        rainyBackground = new Image(gameProps.getProperty("backgroundImage.raining"));

        this.SCORES_FILE = gameProps.getProperty("gameEnd.scoresFile");
        this.PLAYER_NAME = playerName;

        this.gameProps = gameProps;
        this.messageProps = messageProps;

        roadLaneCenter1 = Double.parseDouble(gameProps.getProperty("roadLaneCenter1"));
        roadLaneCenter2 = Double.parseDouble(gameProps.getProperty("roadLaneCenter2"));
        roadLaneCenter3 = Double.parseDouble(gameProps.getProperty("roadLaneCenter3"));

        /* Load configurations from properties file */
        TARGET_SCORE = Double.parseDouble(gameProps.getProperty("gamePlay.target"));
        TAXI_DETECT_RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.taxiDetectRadius"));
        MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        INFO_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        COIN_POWER_DURATION = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        INVINCIBLE_POWER_DURATION = Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames"));

        // Text positions
        SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.x"));
        SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.y"));
        TARGET_X = Integer.parseInt(gameProps.getProperty("gamePlay.target.x"));
        TARGET_Y = Integer.parseInt(gameProps.getProperty("gamePlay.target.y"));
        FRAMES_X = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.x"));
        FRAMES_Y = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.y"));
        TRIP_DETAILS_X = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.x"));
        TRIP_DETAILS_Y = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.y"));
        COIN_POWER_FRAMES_X = Integer.parseInt(gameProps.getProperty("gamePlay.coin.x"));
        COIN_POWER_FRAMES_Y = Integer.parseInt(gameProps.getProperty("gamePlay.coin.y"));
        TAXI_HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x"));
        TAXI_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y"));
        DRIVER_HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x"));
        DRIVER_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y")); 
        PASSENGER_HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.x"));
        PASSENGER_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.y"));

        // Read world file and initialize game entities
        String[][] worldData = IOUtils.readCommaSeparatedFile(gameProps.getProperty("gamePlay.objectsFile"));
        // Read weather file and get weather conditions
        WEATHER_CONDITIONS = IOUtils.readCommaSeparatedFile(gameProps.getProperty("gamePlay.weatherFile"));
        initializeEntities(worldData);
    }

    /**
     * Updates the gameplay logic on each frame, handling user input, rendering entities,
     * and managing the game state.
     *
     * @param input The current mouse/keyboard input.
     * @return true if the game is over, false otherwise.
     */
    public boolean update(Input input) {
        frameCounter++;

        if (driver.isInTaxi()){
            this.SCROLL_SPEED = taxi.getSPEED_Y();
        }
        else{
            this.SCROLL_SPEED = driver.getSPEED_Y();
        }

        // Handle input from player
        if (input.isDown(Keys.UP)) {
            if(driver.isInTaxi())this.taxiIsMoving = true;
            moveBackgroundsDown();
            moveEntitiesDown();
        }

        if(input.isDown(Keys.LEFT)){
            if(driver.isInTaxi()){
                this.taxiIsMoving = true;
                taxi.moveLeft();
            } else{
                driver.moveLeft();
            }
        }
        if(input.isDown(Keys.RIGHT)){
            if(driver.isInTaxi()){
                this.taxiIsMoving = true;
                taxi.moveRight();
            }else{
                driver.moveRight();
            }
        }
        if (input.isDown(Keys.DOWN)){
            if(!driver.isInTaxi()){
                driver.moveDown();
            }
        }

        renderBackgrounds();
        renderEntities();
        spawnCars();
        renderInfo();

        for (Car car: CARS){
            car.update();
        }
        for (Passenger passenger : PASSENGERS) {
            // Adjust passenger priority unless we've dropped them off
            if(!passenger.isAtDestination()){
                passenger.adjustPriorityForWeather(isRaining);
            }
        }

        manageCoinPower();
        manageInvinciblePower();
        checkForPassengerPickup();
        manageTrip();
        manageCarsCollisions();
        manageDriver();

        this.taxiIsMoving = false;

        return isGameOver();
    }

    /* Backgrounds */
    private void renderBackgrounds() {
        double bgPos = (Window.getHeight() / 2.0) + (bg_Y % Window.getHeight());

        // Check weather conditions
        if (currentWeatherIndex < WEATHER_CONDITIONS.length) {
            String[] currentWeather = WEATHER_CONDITIONS[currentWeatherIndex];
            int startFrame = Integer.parseInt(currentWeather[1]);
            int endFrame = Integer.parseInt(currentWeather[2]);

            if (frameCounter >= startFrame && frameCounter <= endFrame) {
                if (currentWeather[0].equals("RAINING")) {
                    BACKGROUND = rainyBackground;
                    isRaining = true;
                } else {
                    BACKGROUND = sunnyBackground;
                    isRaining = false;
                }
            } else if (frameCounter > endFrame) {
                currentWeatherIndex++;
            }
        }

        BACKGROUND.draw(Window.getWidth() / 2.0, bgPos);
        BACKGROUND.draw(Window.getWidth() / 2.0, bgPos - Window.getHeight());
    }

    private void moveBackgroundsDown() {
        bg_Y += SCROLL_SPEED;
    }

    /* Game Entities */
    private void initializeEntities(String[][] worldData) {
        for(String[] entity: worldData) {
            switch (entity[0]){
                case "TAXI":
                    taxi = new Taxi(Double.parseDouble(entity[1]), Double.parseDouble(entity[2]), gameProps);
                    driver = Driver.getInstance(0, 0, gameProps);
                    driver.enterTaxi(taxi);
                    driver.revive();
                    break;
                case "COIN":
                    COINS.add(new Coin(Double.parseDouble(entity[1]), Double.parseDouble(entity[2]), gameProps));
                    break;
                case "PASSENGER":
                    PASSENGERS.add(new Passenger(
                            Double.parseDouble(entity[1]),
                            Double.parseDouble(entity[2]),
                            Integer.parseInt(entity[3]),
                            Double.parseDouble(entity[4]),
                            Double.parseDouble(entity[5]),
                            Integer.parseInt(entity[6]),
                            gameProps
                    ));
                    break;
                case "DRIVER":
                    Driver.getInstance(Double.parseDouble(entity[1]), Double.parseDouble(entity[2]), gameProps);
                    break;
                case "INVINCIBLE_POWER":
                    INVINCIBLE_POWERS.add(new InvinciblePower(Double.parseDouble(entity[1]), Double.parseDouble(entity[2]), gameProps));
                    break;
            }
        }
    }

    private void renderEntities() {
        taxi.render();

        for (TaxiDamaged destroyedTaxi : destroyedTaxis){
            destroyedTaxi.render();
        }

        for (Coin coin : COINS) {
            coin.render();
        }
        for (InvinciblePower power : INVINCIBLE_POWERS) {
            power.render();
        }
        for (Passenger passenger : PASSENGERS) {
            passenger.render();
        }
        for (TripEndFlag flag : tripEndFlags) {
            flag.render();
        }

        for (Car car : CARS) {
            if (car instanceof EnemyCar enemyCar) {
                if (enemyCar.getAlive()) {
                    if (!enemyCar.getInvincible()){
                        spawnFireball(enemyCar);
                    }
                    if (enemyCar.getRevive()) {
                        //enemy cars has getRandomSpeedY method
                        car.setSpeedY(enemyCar.getRandomSpeedY(gameProps));
                    }
                }
            }
            if (car instanceof OtherCar otherCar){
                if (otherCar.getRevive()){
                    //other cars has getRandomSpeedY method
                    car.setSpeedY(otherCar.getRandomSpeedY(gameProps));
                }
            }
            car.render();
        }
        for (Smoke smoke : SMOKES) {
            smoke.render();
        }

        for (Fire fire : FIRES) {
            fire.render();
        }

        for (Fireball fireball : FIREBALLS) {
            if (fireball.getAlive()){
                fireball.render();
            }
        }

        Driver driver = Driver.getInstance(0, 0, gameProps);
        driver.syncWithTaxi(taxi);
        driver.render();
    }

    private void moveEntitiesDown() {
        if (!driver.isInTaxi()){
            taxi.moveDown(SCROLL_SPEED);
        }

        for (TaxiDamaged destroyedTaxi: destroyedTaxis){
            if (destroyedTaxi != null) {
                destroyedTaxi.moveDown(SCROLL_SPEED);
            }
        }

        for (InvinciblePower power : INVINCIBLE_POWERS) {
            power.moveDown(SCROLL_SPEED);
        }
        for (Coin coin : COINS) {
            coin.moveDown(SCROLL_SPEED);
        }
        for (Passenger passenger : PASSENGERS) {
            passenger.moveDown(SCROLL_SPEED);
        }
        for (TripEndFlag flag : tripEndFlags) {
            flag.moveDown(SCROLL_SPEED);
        }
        for (Car car: CARS){
            car.moveDown(SCROLL_SPEED);
        }

        for (Smoke smoke : SMOKES) {
            smoke.moveDown(SCROLL_SPEED);
        }
        for (Fire fire : FIRES) {
            fire.moveDown(SCROLL_SPEED);
        }
    }

    /* Spawning new taxi */
    private void spawnNewTaxi(){
        double lane = selectTaxiRandomLane();
        double y = selectTaxiRandomYCoordinate();
        taxi = new Taxi(lane, y, gameProps);
    }

    private double selectTaxiRandomLane() {
        int laneNumber = random.nextInt(2); // Random lane: 0 or 1
        return switch (laneNumber) {
            case 0 -> roadLaneCenter1;
            case 1 -> roadLaneCenter3;
            default -> roadLaneCenter1; // Fallback in case something goes wrong
        };
    }

    private double selectTaxiRandomYCoordinate() {
        int minY = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        int maxY = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));
        return minY + random.nextInt(maxY - minY + 1);
    }

    /* Spawning other cars & enemy cars */
    private void spawnCars(){
        int randomInt = random.nextInt(1000) + 1;

        if (randomInt % 200  == 0){
            double lane = selectOtherCarRandomLane();
            double y = selectOtherCarRandomYCoordinate();

            // determine whether we should spawn a normal or enemy car
            if (randomInt%400 == 0){
                CARS.add(new EnemyCar(lane, y, gameProps));
            } else {
                CARS.add(new OtherCar(lane, y, gameProps));
            }
        }
    }

    private double selectOtherCarRandomLane() {
        int laneNumber = random.nextInt(3); // Random lane: 0, 1, or 2
        return switch (laneNumber) {
            case 0 -> roadLaneCenter1;
            case 1 -> roadLaneCenter2;
            case 2 -> roadLaneCenter3;
            default -> roadLaneCenter1; // Fallback in case something goes wrong
        };
    }

    private double selectOtherCarRandomYCoordinate() {
        return random.nextBoolean() ? -50 : 768;
    }

    private void spawnFireball(EnemyCar enemyCar){
        int randomInt = random.nextInt(1000) + 1;
        if (randomInt % 300  == 0){
            double fireballX = enemyCar.getX();
            double fireballY = (enemyCar.getY() - enemyCar.getRadius()) - 10 ; // Spawn just after the enemy car
            FIREBALLS.add(new Fireball(fireballX, fireballY, gameProps));
        }
    }

    /* Text on screen */
    private void renderInfo() {
        // Render the total score
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.earnings") +
                String.format("%.2f", playerScore), SCORE_X, SCORE_Y);

        // Render the target score
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.target")
                + String.format("%.2f", TARGET_SCORE), TARGET_X, TARGET_Y);

        // Render the remaining number of frames
        int framesRemaining = MAX_FRAMES - frameCounter;
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.remFrames") +
                framesRemaining, FRAMES_X, FRAMES_Y);

        INFO_FONT.drawString(messageProps.getProperty("gamePlay.taxiHealth") +
                taxi.getHealth(), TAXI_HEALTH_X, TAXI_HEALTH_Y);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.driverHealth") +
                Driver.getInstance(0, 0, gameProps).getHealth(), DRIVER_HEALTH_X, DRIVER_HEALTH_Y); 
        
        double passengerHealth = 100;
        if (currPassenger != null){
            passengerHealth = currPassenger.getHealth();
        }
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.passengerHealth") +
                passengerHealth, PASSENGER_HEALTH_X, PASSENGER_HEALTH_Y);
    }

    /**
     * Renders the details of the current trip on the screen, including expected earnings and penalties.
     */
    public void renderCurrentTripDetails() {
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.onGoingTrip.title"), 35, TRIP_DETAILS_Y);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.trip.expectedEarning")
                        + String.format("%.1f", currTrip.getEarnings()), TRIP_DETAILS_X, TRIP_DETAILS_Y + 30);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.trip.penalty") +
                        currPassenger.getPriority(), TRIP_DETAILS_X, TRIP_DETAILS_Y + 60);
    }

    /**
     * Renders the details of the last completed trip on the screen, including earnings, priority, and penalty.
     */
    public void renderLastTripDetails() {
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.completedTrip.title"), 35, TRIP_DETAILS_Y);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.trip.expectedEarning")
                        + String.format("%.1f",lastTrip.getEarnings()), TRIP_DETAILS_X, TRIP_DETAILS_Y + 30);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.trip.priority")
                        + lastTrip.getPassenger().getPriority(), TRIP_DETAILS_X, TRIP_DETAILS_Y + 60);
        INFO_FONT.drawString(messageProps.getProperty("gamePlay.trip.penalty")
                        + String.format("%.2f", lastTrip.getPenalty()), TRIP_DETAILS_X, TRIP_DETAILS_Y + 90);
    }

    /* Passenger Logic */
    private void checkForPassengerPickup() {
        for (Passenger passenger : PASSENGERS) {
            if (!passenger.isInTaxi() && !passenger.isAtDestination()) {
                if (canPickUp(passenger)) {
                    passenger.moveToTaxi(taxi.getPosition());
                    if (passenger.isInTaxi()) {
                        currPassenger = passenger;
                        TripEndFlag newFlag = new TripEndFlag(passenger.getEndX(), passenger.getY()-passenger.getYDistance(),
                                currPassenger, gameProps);
                        tripEndFlags.add(newFlag);
                        currTrip = new Trip(passenger, newFlag, gameProps);
                    }
                }
            }
        }
    }

    private boolean canPickUp(Passenger passenger) {
        boolean taxiStopped = (!taxiIsMoving);
        boolean taxiEmpty = true;
        if(currPassenger!=null){
            if(currPassenger.isInTaxi()){
                taxiEmpty= false;
            }
        }

        // Taxi must be adjacent to passenger
        double distance = taxi.getPosition().distanceTo(passenger.getPosition());
        boolean adjacent = (distance <= TAXI_DETECT_RADIUS);

        // Driver must be in taxi
        boolean driverInTaxi = driver.isInTaxi();

        return taxiEmpty && taxiStopped && adjacent && driverInTaxi;
    }

    /* Driver management */
    /**
     * Manages the driver's interaction with the taxi. If the driver collides with the taxi,
     * the driver enters the taxi, and if the driver is invincible, the taxi becomes invincible as well.
     * Additionally, if there is a current passenger, the passenger is picked up.
     */
    public void manageDriver(){
        if (driver.checkCollision(taxi)){
            driver.enterTaxi(taxi);
            if (driver.getInvincible()){
                taxi.setInvincible(true);
            }
            // Harsh way to get the passenger in the taxi
            if (currPassenger != null){
                currPassenger.pickUp();
            }
        }
    }

    /* Trip Management */
    private void manageTrip() {

        if (currTrip != null && !currTrip.isCompleted()) {
            renderCurrentTripDetails();
        }

        // If there is a dropped passenger, manage its movement
        if (droppedPassenger != null) {
            // Find the flag corresponding to the dropped passenger
            TripEndFlag passengerFlag = findFlagForPassenger(droppedPassenger);

            if (passengerFlag != null) {
                droppedPassenger.moveToEndFlag(passengerFlag.getPosition());

                // Check if the dropped passenger has reached their flag
                if (droppedPassenger.reachedFlag(passengerFlag.getPosition())) {
                    tripEndFlags.remove(passengerFlag); // Remove the flag once reached
                    droppedPassenger = null;
                }
            }
        }

        // Manage the current passenger's trip logic
        if (currPassenger != null) {
            // Sync passenger movements with taxi
            if (currPassenger.isInTaxi()) {
                currPassenger.setX(taxi.getX());
                currPassenger.setY(taxi.getY());
            } else {
                currPassenger.syncDriver(driver);
            }


            // Check if the taxi is adjacent to or past the flag
            TripEndFlag currentFlag = findFlagForPassenger(currPassenger);
            if (currentFlag != null && !taxiIsMoving && currentFlag.atFlag(taxi.getPosition()) && !currTrip.isCompleted() && currPassenger.isInTaxi()) {
                currTrip.completeTrip(taxi.getPosition());
                playerScore += currTrip.getProfit();
                currPassenger.dropOff();
                droppedPassenger = currPassenger; // Set the dropped passenger
                currPassenger = null; // Reset current passenger
                lastTrip = currTrip;
            }
        }

        if (lastTrip != null && (currTrip == null || currTrip.isCompleted())) {
            renderLastTripDetails();
        }
    }

    private TripEndFlag findFlagForPassenger(Passenger passenger) {
        for (TripEndFlag flag : tripEndFlags) {
            if (flag.isForPassenger(passenger)) {
                return flag;
            }
        }
        return null;
    }

    /* Invincible Power */
    private void manageInvinciblePower(){
        for (InvinciblePower power : INVINCIBLE_POWERS) {
            if (power.checkCollision(taxi)) {
                power.onCollision(taxi);
                invinciblePowerFrames = 0;
                invinciblePowered = true;
                taxi.setInvincible(true);
            } else if (power.checkCollision(driver)) {
                power.onCollision(driver);
                invinciblePowerFrames = 0;
                invinciblePowered = true;
                driver.setInvincible(true);
            }

        }
        if (invinciblePowerFrames < INVINCIBLE_POWER_DURATION && invinciblePowered){
            invinciblePowerFrames++;
            if (invinciblePowerFrames == INVINCIBLE_POWER_DURATION){
                driver.setInvincible(false);
                taxi.setInvincible(false);
            }
        }
        else {
            invinciblePowered = false;
        }
    }

    /* Coin Power */
    /**
     * Manages the player's score and coin power, updating passenger priority if needed.
     */
    public void manageCoinPower(){
        for (Coin coin : COINS) {
            if(coin.checkCollision(taxi)){
                coin.onCollision(taxi);
                coinPowerFrames = 0;
                coinPowered = true;
            } else if (coin.checkCollision(driver)) {
                coin.onCollision(driver);
                coinPowerFrames = 0;
                coinPowered = true;
            }
        }
        if(coinPowerFrames < COIN_POWER_DURATION && coinPowered){
            coinPowerFrames++;
            INFO_FONT.drawString(Integer.toString(coinPowerFrames), COIN_POWER_FRAMES_X, COIN_POWER_FRAMES_Y);

            if(currPassenger != null) {
                currPassenger.increasePriority();
                currTrip.updateEarnings();
            }
        }
        else {
            if(currPassenger != null){
                currPassenger.decreasePriority();
                currTrip.updateEarnings();
            }
            coinPowered = false;
        }
    }

    /**
     * Returns whether the coin power is currently active.
     *
     * @return true if coin power is active, false otherwise.
     */
    public static boolean isCoinPowered(){
        return coinPowered;
    }

    /* Car collisions */
    private void manageCarsCollisions() {
        // Check collisions between taxi and enemy and other cars
        if (!taxi.getInvincible() && !driver.getInvincible()){
            for (Car car: CARS){
                if (taxi.checkCollision(car)){
                    // Spawn smoke once if it's time
                    if (taxi.isTimeToSpawnSmoke()) {
                        SMOKES.add(new Smoke(taxi.getX(), taxi.getY(), gameProps));
                    }
                    if (car.isTimeToSpawnSmoke()) {
                        SMOKES.add(new Smoke(car.getX(), car.getY(), gameProps));
                    }
                    taxi.onCollision(car);
                    car.onCollision(taxi);

                    //to have the bounce effect
                    taxi.bounce(car);
                    car.bounce(taxi);

                    //Spawn fire
                    if (taxi.getDestroyed()) {
                        destroyedTaxis.add(new TaxiDamaged(taxi.getX(), taxi.getY(), gameProps));
                        spawnNewTaxi();
                        if (driver.isInTaxi()){
                            driver.taxiDestroyed(destroyedTaxis.get(destroyedTaxis.size()-1));
                            if (currPassenger != null){
                                currPassenger.taxiDestroyed(destroyedTaxis.get(destroyedTaxis.size()-1));
                            }
                        }
                        if (taxi.getInvincible()){
                            driver.setInvincible(true);
                        }
                        FIRES.add(new Fire(destroyedTaxis.get(destroyedTaxis.size()-1).getX(), destroyedTaxis.get(destroyedTaxis.size()-1).getY(), gameProps));
                    }
                    if (car.getDestroyed()) {
                        FIRES.add(new Fire(car.getX(), car.getY(), gameProps));
                    }
                } else if (driver.checkCollision(car)){
                    driver.onCollision(car);
                    car.onCollision(driver);

                    driver.bounce(car);
                    car.bounce(driver);
                }
                else if (currPassenger != null && currPassenger.checkCollision(car)){
                    currPassenger.onCollision(car);
                    car.onCollision(currPassenger);
                }

                for (Fireball fireball : FIREBALLS) {
                    if (fireball.getAlive()){
                        if (driver.checkCollision(fireball)) {
                            driver.onCollision(fireball);
                            fireball.onCollision(driver);
                        }
                        if (currPassenger != null) {
                            if (currPassenger.checkCollision(fireball)) {
                                currPassenger.onCollision(fireball);
                                fireball.onCollision(currPassenger);
                            }
                        }

                        if (taxi.checkCollision(fireball)){
                            taxi.onCollision(fireball);
                            fireball.onCollision(taxi);
                        }
                        if (car instanceof EnemyCar enemyCar) {
                            if (enemyCar.checkCollision(fireball)) {
                                enemyCar.onCollision(fireball);
                                fireball.onCollision(enemyCar);
                            }
                        } else if (car instanceof OtherCar otherCar) {
                            if (otherCar.checkCollision(fireball)) {
                                otherCar.onCollision(fireball);
                                fireball.onCollision(otherCar);
                            }
                        }
                    }
                }
            }
        }
        // Check collisions between all enemy and other cars
        for (int i = 0; i < CARS.size(); i++) {
            Car car1 = CARS.get(i);
            if (car1.getInvincible()){
                continue;
            }
            for (int j = i + 1; j < CARS.size(); j++) {
                Car car2 = CARS.get(j);
                if (car2.getInvincible()){
                    continue;
                }
                if (car1.checkCollision(car2)) {
                    double car1lastx = car1.getX();
                    double car1lasty = car1.getY();
                    double car2lastx = car2.getX();
                    double car2lasty = car2.getY();
                    // Spawn smoke once if it's time
                    if (car1.isTimeToSpawnSmoke()) {
                        SMOKES.add(new Smoke(car1.getX(), car1.getY(), gameProps));
                    }
                    if (car2.isTimeToSpawnSmoke()) {
                        SMOKES.add(new Smoke(car2.getX(), car2.getY(), gameProps));
                    }
                    car1.onCollision(car2);
                    car2.onCollision(car1);

                    //to have the bounce effect
                    car1.bounce(car2);
                    car2.bounce(car1);

                    // Spawn fire
                    if (car1.getDestroyed()) {
                        FIRES.add(new Fire(car1lastx, car1lasty, gameProps));
                    }
                    if (car2.getDestroyed()) {
                        FIRES.add(new Fire(car2lastx, car2lasty, gameProps));
                    }
                }
            }
        }
    }

    // Check if the taxi is out of bounds
    private boolean isTaxiOutOfBounds() {
        return taxi.getY() > Window.getHeight()+taxi.getRadius();
    }

    /* Manage Stats */
    /**
     * Returns whether the player has won the game.
     *
     * @return true if the player won, false otherwise.
     */
    public boolean didPlayerWin() {
        return playerWon;
    }

    private boolean isGameOver() {
        if (frameCounter >= MAX_FRAMES || isTaxiOutOfBounds()) {
            playerWon = false;
            savePlayerScore();
            return true;
        }
        if(playerScore >= TARGET_SCORE){
            playerWon = true;
            savePlayerScore();
            return true;
        }
        if (driver.isDead()){
            playerWon = false;
            savePlayerScore();
            return true;
        }
        if (currPassenger != null && currPassenger.isDead()){
            playerWon = false;
            savePlayerScore();
            return true;
        }
        return false;
    }

    private void savePlayerScore() {
        IOUtils.writeLineToFile(SCORES_FILE, PLAYER_NAME + "," + String.format("%.2f", playerScore));
    }

}