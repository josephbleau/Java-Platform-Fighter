package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class NoInputStateInputHandler extends StateInputHandler {

    public NoInputStateInputHandler(Player player) {
        super(
                player,
                new State[]{
                        State.SIDESTEPPING,
                        State.JUMPSQUAT
                }
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        return false;
    }
}
