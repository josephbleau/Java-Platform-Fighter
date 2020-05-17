package com.nighto.weebu.entity.character;

public class CharacterTimers {

    public CharacterTimers(CharacterData characterData) {
        jumpSquatTimeRemaining = characterData.getAttributes().jumpSquatDuration;;
        jumpSquatTime = characterData.getAttributes().jumpSquatDuration;

        sidestepTimeRemaining = characterData.getAttributes().sidestepDuration;
        sidestepTime = characterData.getAttributes().sidestepDuration;

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
