package com.josephbleau.game.control;

import com.badlogic.gdx.math.Vector2;

public class NoopGamecubeController extends GamecubeController {

    public NoopGamecubeController() {
        super(null, -1);
    }

    @Override
    public boolean buttonPressed(Button button) {
        return false;
    }

    @Override
    public Vector2 getControlStick() {
        return new Vector2(0,0);
    }

    @Override
    public Vector2 getSmashStick() {
        return new Vector2(0,0);
    }
}
