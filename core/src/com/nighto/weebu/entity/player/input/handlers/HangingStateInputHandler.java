package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
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
    protected boolean doHandleInput(GameController gameController) {
        if (gameController.isPressed(GameInput.Jump)) {
            getPlayer().setActive(true);
            getPlayer().setyVel(getPlayer().getCharacterData().getAttributes().getFullHopSpeed());

            enterState(State.AIRBORNE);
            enterSubstate(State.SUBSTATE_DEFAULT);
        }

        return true;
    }
}
