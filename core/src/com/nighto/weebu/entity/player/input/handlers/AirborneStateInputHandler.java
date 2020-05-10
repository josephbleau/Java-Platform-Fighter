package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.attack.Projectile;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.InputPriority;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class AirborneStateInputHandler extends StateInputHandler {

    public AirborneStateInputHandler(Player player) {
        super(
                player,
                InputPriority.MEDIUM,
                new State[]{State.AIRBORNE}
        );
    }

    @Override
    protected boolean doHandleInput(State state, GamecubeController gamecubeController) {
        return handleSidestep(gamecubeController) &&
                handleDrift(gamecubeController) &&
                handleNeutralSpecial(gamecubeController);
    }

    private boolean handleSidestep(GamecubeController gamecubeController) {
        if ((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK))) {
            getPlayer().setxVel(0);
            enterState(State.SIDESTEPPING);

            return false;
        }

        return true;
    }

    private boolean handleDrift(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
            getPlayer().setxVel(-getPlayer().getMaximumNaturalAirSpeed());
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
            getPlayer().setxVel(getPlayer().getMaximumNaturalAirSpeed());
        } else {
            getPlayer().setxVel(0);
        }

        return true;
    }

    private boolean handleNeutralSpecial(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.S) || gamecubeController.buttonPressed(GamecubeController.Button.B)) {
            getPlayer().startAttack(new Projectile(getPlayer().getFacingRight()));
        }

        return false;
    }
}
