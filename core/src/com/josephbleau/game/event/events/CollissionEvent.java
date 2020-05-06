package com.josephbleau.game.event.events;

import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.entity.Entity;

public class CollissionEvent extends Event {
    /** The first entity involved in the collision **/
    public Entity entity1;
    public Rectangle rectangle1;

    /** The second entity involved in the collision **/
    public Entity entity2;
    public Rectangle rectangle2;

    public CollissionEvent(Entity entity1, Rectangle rectangle1, Entity entity2, Rectangle rectangle2) {
        this.entity1 = entity1;
        this.rectangle1 = rectangle1;
        this.entity2 = entity2;
        this.rectangle2 = rectangle2;
    }
}
