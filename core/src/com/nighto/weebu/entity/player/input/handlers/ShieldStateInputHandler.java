package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.InputPriority;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class ShieldStateInputHandler extends StateInputHandler {

    public ShieldStateInputHandler(Player player) {
        super(player, InputPriority.HIGH, new State[]{State.SHIELDING});
    }

    @Override
    public boolean doHandleInput(State state, GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
                gamecubeController.getControlStick().y >= GamecubeController.HARD_DIRECTION_THRESHOLD) {
            enterState(State.SIDESTEPPING);
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                gamecubeController.buttonPressed(GamecubeController.Button.X)) {
            enterState(State.JUMPSQUAT);
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
            enterState(State.STANDING);
        }

        return false;
    }
}
