package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class ShieldStateInputHandler extends StateInputHandler {

    public ShieldStateInputHandler(Player player) {
        super(player, new State[]{State.SHIELDING});
    }

    @Override
    public boolean doHandleInput(GameController gameController) {
        if (gameController.isPressed(GameInput.Sidestep)) {
            enterState(State.SIDESTEPPING);
        } else if (gameController.isPressed(GameInput.Jump)) {
            enterState(State.JUMPSQUAT);
        } else if (!gameController.isPressed(GameInput.Shield)) {
            enterState(State.STANDING);
        }

        return false;
    }
}
