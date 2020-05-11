package com.nighto.weebu.entity.stage;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.parts.Ledge;

public class TestStage extends Stage {
    public TestStage() {
        this.getRects().add(new Rectangle(0, 0, 500, 50));
        this.getRects().add(new Rectangle(200, 50, 50, 200));
        this.blastZone = new Rectangle(60, 60, 800 - 120, 640 - 120);
        this.spawn(150, 200);

        this.ledges.add(new Ledge(150-5, 240, 5, 10, true));
        this.ledges.add(new Ledge(150 + 500, 240, 5, 10, false));
    }
}
