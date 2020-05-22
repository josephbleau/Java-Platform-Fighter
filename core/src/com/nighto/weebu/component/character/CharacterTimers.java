package com.nighto.weebu.component.character;

import com.nighto.weebu.component.CharacterDataComponent;

public class CharacterTimers {

    public CharacterTimers(CharacterDataComponent characterDataComponent) {
        jumpSquatTimeRemaining = characterDataComponent.getActiveAttributes().getJumpSquatDuration();
        jumpSquatTime = characterDataComponent.getActiveAttributes().getJumpSquatDuration();

        sidestepTimeRemaining = characterDataComponent.getActiveAttributes().getSidestepDuration();
        sidestepTime = characterDataComponent.getActiveAttributes().getSidestepDuration();

        knockbackTimeRemaining = 0;
    }

    /** The amount of time left in jump squat. **/
    protected float jumpSquatTimeRemaining;
    private final float jumpSquatTime;

    /** The amount of time left in the current sidestep. **/
    protected float sidestepTimeRemaining;
    private final float sidestepTime;

    /** The amount of time left in knockback. **/
    protected float knockbackTimeRemaining;

    public void tickTimers(float delta) {
        if (jumpSquatTimeRemaining >= 0) {
            jumpSquatTimeRemaining -= delta;
        }

        if (sidestepTimeRemaining >= 0) {
            sidestepTimeRemaining -= delta;
        }

        if (knockbackTimeRemaining >= 0) {
            knockbackTimeRemaining -= delta;
        }
    }

    public void resetTimers() {
        jumpSquatTimeRemaining = jumpSquatTime;
        sidestepTimeRemaining = sidestepTime;
        knockbackTimeRemaining = 0;
    }

    public float getJumpSquatTimeRemaining() {
        return jumpSquatTimeRemaining;
    }

    public float getSidestepTimeRemaining() {
        return sidestepTimeRemaining;
    }

    public float getKnockbackTimeRemaining() {
        return knockbackTimeRemaining;
    }

    public void setKnockbackTimeRemaining(float knockbackTimeRemaining) {
        this.knockbackTimeRemaining = knockbackTimeRemaining;
    }
}
