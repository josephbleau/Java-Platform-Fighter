package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.InputPriority;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class NoInputStateInputHandler extends StateInputHandler {

    public NoInputStateInputHandler(Player player) {
        super(
                player,
                InputPriority.HIGHEST,
                new State[]{
                        State.SIDESTEPPING
                }
        );
    }

    @Override
    protected boolean doHandleInput(State state, GamecubeController gamecubeController) {
        return false;
    }
}
