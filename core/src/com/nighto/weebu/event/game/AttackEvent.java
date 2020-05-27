package com.nighto.weebu.event.game;

import com.badlogic.gdx.math.Polygon;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.AttackData;

public class AttackEvent extends Event {
    public Entity owner;
    public Polygon hitbox;
    public AttackData attackData;

    public AttackEvent(Entity owner, Polygon hitbox) {
        this.owner = owner;
        this.hitbox = hitbox;
//        this.attackData = attackData;
    }
}
