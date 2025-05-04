/**
 * The Collision interface defines the behavior for game entities that can
 * participate in collisions with other objects.
 */
public interface Collision {
    /**
     * Checks whether this entity has collided with another entity.
     *
     * @param other The other entity to check for a collision with.
     * @return true if a collision occurred, false otherwise.
     */
    boolean checkCollision(Collision other);

    /**
     * Defines the behavior when a collision with another entity occurs.
     *
     * @param other The other entity involved in the collision.
     */
    void onCollision(Collision other);
}
