package com.nighto.weebu.entity.stage;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.stage.parts.Ledge;

public class TestStage extends Stage {
    public TestStage() {
        this.getRects().add(new Rectangle(0, 0, 1000, 50));
        this.blastZone = new Rectangle(60, 60, 1920 - 120, 1080 - 120);
        this.spawn((1920 - 1000) / 2, 200);

        this.ledges.add(new Ledge(((1920 - 1000) / 2)-5, 240, 5, 10, true));
        this.ledges.add(new Ledge(((1920 - 1000) / 2) + 1000, 240, 5, 10, false));
    }
}
