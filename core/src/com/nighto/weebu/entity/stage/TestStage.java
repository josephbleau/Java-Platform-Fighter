package com.nighto.weebu.entity.stage;

import com.nighto.weebu.component.stage.StageDataComponent;

import java.io.IOException;

public class TestStage extends Stage {
    public TestStage() {
        try {
            registerComponent(StageDataComponent.class, StageDataComponent.loadFromJson("TestStage.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTag("Test Stage");
    }

    @Override
    public void update(float delta) {}
}
