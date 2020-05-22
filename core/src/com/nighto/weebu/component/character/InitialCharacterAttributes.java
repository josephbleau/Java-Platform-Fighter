package com.nighto.weebu.component.character;

public class InitialCharacterAttributes {
    /** Total of number of jumps **/
    protected int numberOfJumps;

    /** The maximum y-velocity that can be reached via gravity. Being knocked toward the ground will ignore this value. **/
    protected float fallSpeed;

    /** The maximum x-velocity that can be reached via controller inputs while grounded. **/
    protected float groundSpeed;

    /** The maximum x-velocity that can be reached via controller inputs while in the air. **/
    protected float airSpeed;

    /** The initial velocity boost of a short hop **/
    protected float shortHopSpeed;

    /** The initial velocity boost of a full hop **/
    protected float fullHopSpeed;

    /** The amount of time until you jump after pressing jump (from the ground) in seconds. **/
    protected float jumpSquatDuration;

    /** The total amount of time spent in sidestep **/
    protected float sidestepDuration;

    protected float airFriction;
    protected float groundFriction;

    public int getNumberOfJumps() {
        return numberOfJumps;
    }

    public float getFallSpeed() {
        return fallSpeed;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public float getAirSpeed() {
        return airSpeed;
    }

    public float getShortHopSpeed() {
        return shortHopSpeed;
    }

    public float getFullHopSpeed() {
        return fullHopSpeed;
    }

    public float getJumpSquatDuration() {
        return jumpSquatDuration;
    }

    public float getSidestepDuration() {
        return sidestepDuration;
    }

    public float getAirFriction() {
        return airFriction;
    }

    public float getGroundFriction() {
        return groundFriction;
    }
}
