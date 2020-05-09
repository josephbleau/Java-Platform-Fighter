package com.nighto.weebu.event.events;

import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.entity.Entity;

public class CollissionEvent extends Event {
    /** The first entity involved in the collision **/
    public Entity entity1;
    public Shape2D shape1;

    /** The second entity involved in the collision **/
    public Entity entity2;
    public Shape2D shape2;

    public CollissionEvent(Entity entity1, Shape2D shape1, Entity entity2, Shape2D shape2) {
        this.entity1 = entity1;
        this.shape1 = shape1;
        this.entity2 = entity2;
        this.shape2 = shape2;
    }
}
