package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class CrouchingStateInputHandler extends StateInputHandler {

    public CrouchingStateInputHandler(Player player) {
        super(
                player,
                new State[]{State.CROUCHING},
                new State[]{State.SUBSTATE_KNOCKBACK}
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        if(!gameController.isPressed(GameInput.Crouch)) {
            enterState(State.STANDING);

            return false;
        }

        return true;
    }
}
