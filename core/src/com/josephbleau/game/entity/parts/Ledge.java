package com.josephbleau.game.entity.parts;

import com.badlogic.gdx.math.Rectangle;

public class Ledge extends Rectangle {
    public boolean hangLeft;

    public Ledge(float x, float y, float width, float height, boolean hangLeft) {
        super(x,y,width,height);
        this.hangLeft = hangLeft;
    }
}
