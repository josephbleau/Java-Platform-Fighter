package com.nighto.weebu.event.game;

import com.nighto.weebu.entity.Entity;

public class DeathEvent extends Event {
    /** The entity that died. **/
    public Entity entity;

    public DeathEvent(Entity entity) {
        this.entity = entity;
    }
}
