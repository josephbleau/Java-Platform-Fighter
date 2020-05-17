package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class WallslideStateInputHandler extends StateInputHandler {
    public WallslideStateInputHandler(Player player) {
        super(player, new State[]{State.WALLSLIDING});
    }

    @Override
    public boolean doHandleInput(GameController gameController) {
        getPlayer().setJumpCount(0);

        if (getPlayer().getGameController().isPressed(GameInput.Jump)) {
            enterState(State.JUMPSQUAT);
            return true;
        }

        if (inSubState(State.SUBSTATE_WALLSLIDING_RIGHT)) {
            if (getPlayer().getGameController().isPressed(GameInput.ControlRightLight) ||
                    getPlayer().getGameController().isPressed(GameInput.ControlRightHard)) {
                return false;
            }
        }

        if (inSubState(State.SUBSTATE_WALLSLIDING_LEFT)) {
            if (getPlayer().getGameController().isPressed(GameInput.ControlLeftLight) ||
                    getPlayer().getGameController().isPressed(GameInput.ControlLeftHard)) {
                return false;
            }
        }

        enterState(State.AIRBORNE);
        return true;
    }
}
