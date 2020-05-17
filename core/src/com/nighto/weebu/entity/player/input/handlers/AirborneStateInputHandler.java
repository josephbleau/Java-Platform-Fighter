package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class AirborneStateInputHandler extends StateInputHandler {

    private boolean jumpChanged = false;

    public AirborneStateInputHandler(Player player) {
        super(
                player,
                new State[]{State.AIRBORNE}
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        jumpChanged = gameController.hasChangedSinceLastFrame(GameInput.Jump);

        return handleJump(gameController) &&
                handleSidestep(gameController) &&
                handleDrift(gameController) &&
                handleFastFall(gameController) &&
                handleNeutralSpecial(gameController);
    }

    private boolean handleJump(GameController gameController) {
        int jumpCount = getPlayer().getJumpCount();
        int totalJumps = getPlayer().getCharacterData().getAttributes().getNumberOfJumps();

        if (jumpChanged && jumpCount < totalJumps) {
            if (gameController.isPressed(GameInput.Jump)) {
                getPlayer().setJumpCount(++jumpCount);
                enterState(State.JUMPSQUAT);
            }
        }

        return true;
    }

    private boolean handleSidestep(GameController gameController) {
        if (gameController.isPressed(GameInput.Shield)) {
            getPlayer().setxVel(0);
            enterState(State.SIDESTEPPING);

            return false;
        }

        return true;
    }

    private boolean handleDrift(GameController gameController) {
        if (gameController.isPressed(GameInput.ControlLeftLight)) {
            getPlayer().setxVel(-getPlayer().getCharacterData().getAttributes().getAirSpeed());
        } else if (gameController.isPressed(GameInput.ControlRightLight)) {
            getPlayer().setxVel(getPlayer().getCharacterData().getAttributes().getAirSpeed());
        } else {
            getPlayer().setxVel(0);
        }

        return true;
    }

    private boolean handleFastFall(GameController gameController) {
        getPlayer().setFastFalling(gameController.isPressed(GameInput.Crouch));
        return true;
    }

    private boolean handleNeutralSpecial(GameController gameController) {
        if(!inSubState(State.SUBSTATE_ATTACKING)) {
            if (gameController.isPressed(GameInput.NeutralSpecial)) {
                getPlayer().startAttack(new ProjectileAttack(getPlayer(),0 , 30));
                return false;
            }
        }

        return true;
    }
}
