package com.josephbleau.game.event.events;

import com.josephbleau.game.entity.Entity;

public class CollissionEvent extends Event {
    /** The first entity involved in the collision **/
    public Entity entity1;

    /** The second entity involved in the collision **/
    public Entity entity2;

    public CollissionEvent(Entity entity1, Entity entity2) {
        this.entity1 = entity1;
        this.entity2 = entity2;
    }
}
