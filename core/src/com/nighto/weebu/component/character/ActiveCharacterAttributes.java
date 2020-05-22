package com.nighto.weebu.component.character;

public class ActiveCharacterAttributes extends InitialCharacterAttributes {
    private boolean fastFalling;

    public ActiveCharacterAttributes(InitialCharacterAttributes initialCharacterAttributes) {
        this.numberOfJumps = initialCharacterAttributes.getNumberOfJumps();
        this.fallSpeed = initialCharacterAttributes.getFallSpeed();
        this.groundFriction = initialCharacterAttributes.getGroundSpeed();
        this.airSpeed = initialCharacterAttributes.getAirSpeed();
        this.shortHopSpeed = initialCharacterAttributes.getShortHopSpeed();
        this.fullHopSpeed = initialCharacterAttributes.getFullHopSpeed();
        this.jumpSquatDuration = initialCharacterAttributes.getJumpSquatDuration();
        this.sidestepDuration = initialCharacterAttributes.getSidestepDuration();
        this.airFriction = initialCharacterAttributes.getAirFriction();
        this.groundFriction = initialCharacterAttributes.getGroundFriction();
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
}
