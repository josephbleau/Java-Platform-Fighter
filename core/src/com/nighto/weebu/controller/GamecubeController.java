package com.nighto.weebu.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;

public class GamecubeController {
    public static final String MAYFLASH_ADAPTER_ID = "MAYFLASH GameCube Controller Adapter";

    public static final float HARD_DIRECTION_THRESHOLD = 0.5f;
    public static final float LIGHT_DIRECTION_THRESHOLD = 0.02f;

    private Controller controller;

    public GamecubeController(Controller controller) {
        this.controller = controller;
    }

    public enum Button {
        X(0, "X"),
        A(1, "A"),
        B(2, "B"),
        Y(3, "Y"),

        LEFT_BUMPER_CLICK(4, "LEFT BUMPER CLICK"),
        RIGHT_BUMPER_CLICK(5, "RIGHT BUMPER CLICK"),
        Z(7, "Z"),

        START(9, "START"),

        DPAD_UP(12, "DPAD UP"),
        DPAD_RIGHT(13, "DPAD RIGHT"),
        DPAD_DOWN(14, "DPAD DOWN"),
        DPAD_LEFT(15, "DPAD UP");

        final public int value;
        final public String name;

        Button(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    public enum Axis {
        SMASY_X_AXIS(0),
        SMASH_Y_AXIS(1),
        CONTROL_Y_AXIS(2),
        CONTROL_X_AXIS(3);

        final public int value;

        Axis(int value) {
            this.value = value;
        }
    }

    public boolean buttonPressed(Button button) {
        return controller.getButton(button.value);
    }

    public Vector2 getControlStick(){
        return new Vector2(
                controller.getAxis(Axis.CONTROL_X_AXIS.value),
                controller.getAxis(Axis.CONTROL_Y_AXIS.value)
        );
    }
    public Vector2 getSmashStick() {
        return new Vector2(
                controller.getAxis(Axis.SMASY_X_AXIS.value),
                controller.getAxis(Axis.SMASH_Y_AXIS.value)
        );
    }
}
