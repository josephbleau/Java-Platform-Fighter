package com.nighto.weebu.entity.stage.parts;

import com.badlogic.gdx.math.Rectangle;

public class Ledge {
    private static final String leftSide = "left";

    public String side;
    public Rectangle boundingBox;

    public boolean isHangLeft() {
        return leftSide.equalsIgnoreCase(side);
    }
}
