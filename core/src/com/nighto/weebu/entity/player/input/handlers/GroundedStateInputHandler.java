package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.MeleeAttack;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class GroundedStateInputHandler extends StateInputHandler {

    public GroundedStateInputHandler(Player player) {
        super(
                player,
                new State[] {
                        State.DEFAULT,
                        State.STANDING,
                        State.RUNNING
                },
                new State[] {
                        State.SUBSTATE_ATTACKING
                }
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        return handleShield(gameController) &&
                handleRun(gameController) &&
                handleCrouch(gameController) &&
                handleJump(gameController) &&
                handleNeutralSpecial(gameController) &&
                handleNeutralAttack(gameController);
    }

    private boolean handleShield(GameController gameController) {
        if (gameController.isPressed(GameInput.Shield)) {
            getPlayer().spawnShield();
            getPlayer().setxVel(0);

            enterState(State.SHIELDING);

            return false;
        }

        return true;
    }

    private boolean handleRun(GameController gameController) {
        if (gameController.isPressed(GameInput.ControlLeftLight)) {
            getPlayer().setxVel(-getPlayer().getCharacterData().getAttributes().getGroundSpeed());
            enterState(State.RUNNING);
        } else if (gameController.isPressed(GameInput.ControlRightLight)) {
            getPlayer().setxVel(getPlayer().getCharacterData().getAttributes().getGroundSpeed());
            enterState(State.RUNNING);
        } else {
            getPlayer().setxVel(0);
            enterState(State.STANDING);
        }

        return true;
    }

    private boolean handleCrouch(GameController gameController) {
        if (gameController.isPressed(GameInput.Crouch)) {
            getPlayer().setxVel(0);
            enterState(State.CROUCHING);
        }

        return true;
    }

    private boolean handleJump(GameController gameController) {
        if (gameController.hasChangedSinceLastFrame(GameInput.Jump) && gameController.isPressed(GameInput.Jump)) {
            getPlayer().setJumpCount(1);
            getPlayer().resetTimers();
            enterState(State.JUMPSQUAT);

            return false;
        }

        return true;
    }

    private boolean handleNeutralAttack(GameController gameController) {
        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if (gameController.isPressed(GameInput.NeutralAttack)) {
                float xOffsetDirectionMultiplier = (getPlayer().getFacingRight()) ? 1 : -1;
                float xOffset = 30 * xOffsetDirectionMultiplier;

                getPlayer().startAttack(new MeleeAttack(getPlayer(), xOffset, 30));
                getPlayer().setxVel(0);
            }
        }

        return true;
    }

    private boolean handleNeutralSpecial(GameController gameController) {
        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if (gameController.isPressed(GameInput.NeutralSpecial)) {
                getPlayer().startAttack(new ProjectileAttack(getPlayer(), 0, 30));
                getPlayer().setxVel(0);
            }
        }

        return true;
    }
}
