package com.nighto.weebu.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;

public class GameController {
    private GamecubeController controller;
    private boolean off;
    private final Map<GameInput, Boolean> lastFrame;
    private final Map<GameInput, Boolean> currentFrame;

    public GameController(GamecubeController controller, boolean off) {
        this.controller = controller;
        this.off = off;

        if (this.controller == null) {
            this.controller = new NoopGamecubeController();
        }

        lastFrame = new HashMap<>();
        currentFrame = new HashMap<>();

        for(GameInput input : GameInput.values()) {
            lastFrame.put(input, Boolean.FALSE);
            currentFrame.put(input, Boolean.FALSE);
        }
    }

    public void poll() {
        if (off) {
            return;
        }

        lastFrame.putAll(currentFrame);

        if (controller instanceof NoopGamecubeController) {
            return;
        }

        currentFrame.put(GameInput.ControlLeftLight, controller.getControlStick().x <= -GamecubeController.LIGHT_DIRECTION_THRESHOLD || Gdx.input.isKeyPressed(Input.Keys.LEFT));
        currentFrame.put(GameInput.ControlLeftHard, controller.getControlStick().x <= -GamecubeController.HARD_DIRECTION_THRESHOLD || Gdx.input.isKeyPressed(Input.Keys.LEFT));
        currentFrame.put(GameInput.ControlRightLight, controller.getControlStick().x >= GamecubeController.LIGHT_DIRECTION_THRESHOLD || Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        currentFrame.put(GameInput.ControlRightHard, controller.getControlStick().x >= GamecubeController.HARD_DIRECTION_THRESHOLD || Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        currentFrame.put(GameInput.Jump, controller.buttonPressed(GamecubeController.Button.X) || controller.buttonPressed(GamecubeController.Button.Y) || Gdx.input.isKeyPressed(Input.Keys.SPACE));
        currentFrame.put(GameInput.Crouch, (controller.getControlStick().y > GamecubeController.HARD_DIRECTION_THRESHOLD) || Gdx.input.isKeyPressed(Input.Keys.DOWN));
        currentFrame.put(GameInput.NeutralAttack, controller.buttonPressed(GamecubeController.Button.A) || Gdx.input.isKeyPressed(Input.Keys.A));
        currentFrame.put(GameInput.NeutralSpecial, controller.buttonPressed(GamecubeController.Button.B) || Gdx.input.isKeyPressed(Input.Keys.S));
        currentFrame.put(GameInput.Shield, controller.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) || controller.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));
        currentFrame.put(GameInput.Sidestep, currentFrame.get(GameInput.Crouch) && currentFrame.get(GameInput.Shield));
    }

    public Boolean hasChangedSinceLastFrame(GameInput input) {
        return currentFrame.get(input) != lastFrame.get(input);
    }

    public Boolean isPressed(GameInput input) {
        return currentFrame.get(input);
    }
}
