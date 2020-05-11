package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.InputPriority;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class JumpSquatExitStateInputHandler extends StateInputHandler {

    public JumpSquatExitStateInputHandler(Player player) {
        super(
                player,
                InputPriority.HIGH,
                new State[]{State.EXIT_JUMPSQUAT}
        );
    }

    @Override
    protected boolean doHandleInput(GamecubeController gamecubeController) {
        enterState(State.AIRBORNE);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                gamecubeController.buttonPressed(GamecubeController.Button.X) ||
                gamecubeController.buttonPressed((GamecubeController.Button.Y))) {
            getPlayer().setyVel(getPlayer().getFullHopVel());
        } else {
            getPlayer().setyVel(getPlayer().getShortHopVel());
        }

        return true;
    }
}
