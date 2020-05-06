package com.josephbleau.game.entity.stage;

import com.badlogic.gdx.math.Rectangle;

public class TestStage extends Stage {
    public TestStage() {
        this.getRects().add(new Rectangle(0, 0, 500, 10));
        this.blastZone = new Rectangle(60, 60, 800 - 120, 640 - 120);
        this.spawn(150, 200);
    }
}
