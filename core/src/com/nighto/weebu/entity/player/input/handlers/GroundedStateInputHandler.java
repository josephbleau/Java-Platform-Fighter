package com.nighto.weebu.entity.player.input.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.controller.GamecubeController;
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
                        State.CLEAR,
                        State.STANDING,
                        State.RUNNING
                },
                new State[] {
                        State.SUBSTATE_ATTACKING
                }
        );
    }

    @Override
    protected boolean doHandleInput(GamecubeController gamecubeController) {
        return handleShield(gamecubeController) &&
                handleRun(gamecubeController) &&
                handleCrouch(gamecubeController) &&
                handleJump(gamecubeController) &&
                handleNeutralSpecial(gamecubeController) &&
                handleNeutralAttack(gamecubeController);
    }

    private boolean handleShield(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
            getPlayer().spawnShield();
            getPlayer().setxVel(0);

            enterState(State.SHIELDING);
            return false;
        }

        return true;
    }

    private boolean handleRun(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                gamecubeController.getControlStick().x < -GamecubeController.LIGHT_DIRECTION_THRESHOLD) {
            getPlayer().setxVel(-getPlayer().getMaximumNaturalGroundSpeed());
            enterState(State.RUNNING);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                gamecubeController.getControlStick().x > GamecubeController.LIGHT_DIRECTION_THRESHOLD) {
            getPlayer().setxVel(getPlayer().getMaximumNaturalGroundSpeed());
            enterState(State.RUNNING);
        } else {
            getPlayer().setxVel(0);
            enterState(State.STANDING);
        }

        return true;
    }

    private boolean handleCrouch(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || gamecubeController.getControlStick().y >= GamecubeController.HARD_DIRECTION_THRESHOLD) {
            getPlayer().setxVel(0);
            enterState(State.CROUCHING);
        }

        return true;
    }

    private boolean handleJump(GamecubeController gamecubeController) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                gamecubeController.buttonPressed(GamecubeController.Button.X)) {
            enterState(State.JUMPSQUAT);
            return false;
        }

        return true;
    }

    private boolean handleNeutralAttack(GamecubeController gamecubeController) {
        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if ((Gdx.input.isKeyPressed(Input.Keys.A) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.A))) {

                float xOffsetDirectionMultiplier = (getPlayer().getFacingRight()) ? 1 : -1;
                float xOffset = 30 * xOffsetDirectionMultiplier;

                getPlayer().startAttack(new MeleeAttack(xOffset, 30));
                getPlayer().setxVel(0);
            }
        }

        return true;
    }

    private boolean handleNeutralSpecial(GamecubeController gamecubeController) {
        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if ((Gdx.input.isKeyPressed(Input.Keys.S) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.B))) {
                getPlayer().startAttack(new ProjectileAttack(getPlayer().getFacingRight(), 0, 30));
                getPlayer().setxVel(0);
            }
        }

        return true;
    }
}
