package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class CrouchingStateInputHandler extends StateInputHandler {

    public CrouchingStateInputHandler(Player player) {
        super(
                player,
                new State[]{State.CROUCHING}
        );
    }

    @Override
    protected boolean doHandleInput(GamecubeController gamecubeController) {
        if(!Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            enterState(State.STANDING);
            return false;
        }

        return true;
    }
}
