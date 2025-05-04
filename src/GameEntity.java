import bagel.*;
import bagel.util.Point;

/**
 * The GameEntity class represents a basic entity in the game with a position and image.
 * It provides methods for rendering, movement, and collision-related calculations.
 * This class is designed to be extended by more specific game entities.
 */
public abstract class GameEntity {
    private Point position;
    private Image image;
    private double radius;
    private static final DrawOptions HALF_OPACITY = new DrawOptions().setBlendColour(1, 1, 1, 0.5);

    /**
     * Constructs a new GameEntity with a specified position and image.
     *
     * @param x The x-coordinate of the entity.
     * @param y The y-coordinate of the entity.
     * @param imagePath The file path to the image representing the entity.
     */
    public GameEntity(double x, double y, String imagePath) {
        this.position = new Point(x, y);
        this.image = new Image(imagePath);
    }

    /**
     * Renders the entity on the screen with the option to display it at half opacity
     * when the entity is in an invincible state.
     *
     * @param invincible true to render the entity at half opacity, false for normal rendering.
     */
    public void render(boolean invincible){
        if (invincible){
            image.draw(position.x, position.y, HALF_OPACITY);
        } else {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Renders the entity on the screen without any opacity modifications.
     */
    public void render(){
        image.draw(position.x, position.y);
    }

    /**
     * Calculates the distance between this entity and another point.
     *
     * @param other The other point to calculate the distance to.
     * @return The distance between this entity and the other point.
     */
    public double distanceTo(Point other) {
        return position.distanceTo(other);
    }

    // Getters

    /**
     * Returns the x-coordinate of the entity.
     *
     * @return The x-coordinate of the entity.
     */
    public double getX() {
        return position.x;
    }

    /**
     * Returns the y-coordinate of the entity.
     *
     * @return The y-coordinate of the entity.
     */
    public double getY() {
        return position.y;
    }

    /**
     * Returns the collision radius of the entity.
     *
     * @return The radius of the entity.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns the current position of the entity.
     *
     * @return The position of the entity as a Point.
     */
    public Point getPosition() {
        return position;
    }

    // Setters

    /**
     * Sets the x-coordinate of the entity's position.
     *
     * @param x The new x-coordinate of the entity.
     */
    public void setX(double x) {
        position = new Point(x, position.y);
    }

    /**
     * Sets the y-coordinate of the entity's position.
     *
     * @param y The new y-coordinate of the entity.
     */
    public void setY(double y) {
        position = new Point(position.x, y);
    }

    /**
     * Sets the collision radius of the entity.
     *
     * @param radius The new radius of the entity.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Moves the entity down by a specified speed value.
     * This is typically used when the camera moves down in the game.
     *
     * @param speed The speed at which the entity moves down.
     */
    public void moveDown(double speed) {
        position = new Point(position.x, position.y + speed);
    }
}
