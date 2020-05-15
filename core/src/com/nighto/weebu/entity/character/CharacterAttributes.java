package com.nighto.weebu.entity.character;

public class CharacterAttributes {
    float numberOfJumps;

    /** The maximum y-velocity that can be reached via gravity. Being knocked toward the ground will ignore this value. **/
    float fallSpeed;

    /** The maximum x-velocity that can be reached via controller inputs while grounded. **/
    float groundSpeed;

    /** The maximum x-velocity that can be reached via controller inputs while in the air. **/
    float airSpeed;

    /** The initial velocity boost of a short hop **/
    float shortHopSpeed;

    /** The initial velocity boost of a full hop **/
    float fullHopSpeed;

    /** The amount of time until you jump after pressing jump (from the ground) in seconds. **/
    float jumpSquatDuration;

    /** The total amount of time spent in sidestep **/
    float sidestepDuration;

    public float getNumberOfJumps() {
        return numberOfJumps;
    }

    public void setNumberOfJumps(float numberOfJumps) {
        this.numberOfJumps = numberOfJumps;
    }

    public float getFallSpeed() {
        return fallSpeed;
    }

    public void setFallSpeed(float fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(float groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public float getAirSpeed() {
        return airSpeed;
    }

    public void setAirSpeed(float airSpeed) {
        this.airSpeed = airSpeed;
    }

    public float getShortHopSpeed() {
        return shortHopSpeed;
    }

    public void setShortHopSpeed(float shortHopSpeed) {
        this.shortHopSpeed = shortHopSpeed;
    }

    public float getFullHopSpeed() {
        return fullHopSpeed;
    }

    public void setFullHopSpeed(float fullHopSpeed) {
        this.fullHopSpeed = fullHopSpeed;
    }

    public float getJumpSquatDuration() {
        return jumpSquatDuration;
    }

    public void setJumpSquatDuration(float jumpSquatDuration) {
        this.jumpSquatDuration = jumpSquatDuration;
    }

    public float getSidestepDuration() {
        return sidestepDuration;
    }

    public void setSidestepDuration(float sidestepDuration) {
        this.sidestepDuration = sidestepDuration;
    }
}
