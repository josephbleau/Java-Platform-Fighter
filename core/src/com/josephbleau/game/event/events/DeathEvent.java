package com.josephbleau.game.event.events;

import com.josephbleau.game.entity.Entity;

public class DeathEvent extends Event {
    /** The entity that died. **/
    public Entity entity;

    public DeathEvent(Entity entity) {
        this.entity = entity;
    }
}
