package com.josephbleau.game.entity.stage;

import com.josephbleau.game.entity.Entity;

public abstract class Stage extends Entity {
    /** Gravity determines how fast an entity falls (in px/seconds) **/
    private final float gravity = 5.0f;

    /** You cannot apply force to a stage. **/
    @Override
    public void applyForce(float x, float y) { }

    public float getGravity() {
        return this.gravity;
    }
}
