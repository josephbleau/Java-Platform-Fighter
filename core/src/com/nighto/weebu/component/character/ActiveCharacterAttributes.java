package com.nighto.weebu.component.character;

public class ActiveCharacterAttributes extends InitialCharacterAttributes {
    private boolean fastFalling;
    private final InitialCharacterAttributes initialCharacterAttributes;

    public ActiveCharacterAttributes(InitialCharacterAttributes initialCharacterAttributes) {
        this.initialCharacterAttributes = initialCharacterAttributes;
        resetValues();
    }

    public void resetValues() {
        numberOfJumps = initialCharacterAttributes.getNumberOfJumps();
        fallSpeed = initialCharacterAttributes.getFallSpeed();
        groundSpeed = initialCharacterAttributes.getGroundSpeed();
        airSpeed = initialCharacterAttributes.getAirSpeed();
        shortHopSpeed = initialCharacterAttributes.getShortHopSpeed();
        fullHopSpeed = initialCharacterAttributes.getFullHopSpeed();
        jumpSquatDuration = initialCharacterAttributes.getJumpSquatDuration();
        sidestepDuration = initialCharacterAttributes.getSidestepDuration();
        airFriction = initialCharacterAttributes.getAirFriction();
        groundFriction = initialCharacterAttributes.getGroundFriction();
        knockbackModifier = initialCharacterAttributes.getKnockbackModifier();

        fastFalling = false;
    }

    public boolean isFastFalling() {
        return fastFalling;
    }

    public void setFastFalling(boolean fastFalling) {
        this.fastFalling = fastFalling;
    }

    public void setNumberOfJumps(int numberOfJumps) {
        this.numberOfJumps = numberOfJumps;
    }

    public void resetJumps() {
        this.numberOfJumps = initialCharacterAttributes.getNumberOfJumps();
    }

    public void decrementJumps() {
        this.numberOfJumps--;
    }

    public void incrementJumps() {
        this.numberOfJumps++;
    }

    public void setFallSpeed(float fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public void setGroundSpeed(float groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public void setAirSpeed(float airSpeed) {
        this.airSpeed = airSpeed;
    }

    public void setShortHopSpeed(float shortHopSpeed) {
        this.shortHopSpeed = shortHopSpeed;
    }

    public void setFullHopSpeed(float fullHopSpeed) {
        this.fullHopSpeed = fullHopSpeed;
    }

    public void setJumpSquatDuration(float jumpSquatDuration) {
        this.jumpSquatDuration = jumpSquatDuration;
    }

    public void setSidestepDuration(float sidestepDuration) {
        this.sidestepDuration = sidestepDuration;
    }

    public void setAirFriction(float airFriction) {
        this.airFriction = airFriction;
    }

    public void setGroundFriction(float groundFriction) {
        this.groundFriction = groundFriction;
    }

    public void setKnockbackModifier(float knockbackModifier) { this.knockbackModifier = knockbackModifier; }
}
