package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class HangingStateInputHandler extends StateInputHandler {

    public HangingStateInputHandler(Player player) {
        super(
                player,
                new State[]{State.HANGING}
        );
    }

    @Override
    protected boolean doHandleInput(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                gamecubeController.buttonPressed(GamecubeController.Button.X)) {

            getPlayer().setActive(true);
            getPlayer().setyVel(getPlayer().getCharacterData().getAttributes().getFullHopSpeed());

            enterState(State.AIRBORNE);
            enterSubstate(State.SUBSTATE_DEFAULT);
        }

        return true;
    }
}
