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

    /** Amount of velocity added to your current x-velocity to counter-act it while in the air and not
     * actively controlling your character. */
    protected float airFriction;

    /** Amount of velocity addedf to your current x-velocity to counter-act it while on the ground
     * and not actievly controlling your character*/
    protected float groundFriction;

    /** Factor in how far you are sent back **/
    protected float knockbackModifier;

    /** Velocity assigned when air-dodging **/
    protected float airDodgeVelocity;

    /** Number of air-dodges allowed before landing **/
    protected float numberOfAirDodges;

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

    public float getKnockbackModifier() {
        return knockbackModifier;
    }

    public float getAirDodgeVelocity() {
        return airDodgeVelocity;
    }

    public float getNumberOfAirDodges() {
        return numberOfAirDodges;
    }
}
