package com.nighto.weebu.component.stage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.entity.stage.parts.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StageDataComponent extends Component {
    private static final String stageDirectoryPath = "core/assets/levels/";

    public String tag;
    public float gravity;

    public Ledge[] ledges;
    public Platform[] platforms;
    public Vector2[] spawns;

    public Rectangle blastZone;

    private StageDataComponent() {}

    public List<Rectangle> getAllBoundingBoxes() {
        List<Rectangle> boundingBoxes = new ArrayList<>();

        boundingBoxes.addAll(getPlatformBoundingBoxes());
        boundingBoxes.addAll(getLedgeBoundingBoxes());

        return boundingBoxes;
    }

    public List<Rectangle> getPlatformBoundingBoxes() {
        return Arrays.stream(platforms).map(p->p.boundingBox).collect(Collectors.toList());
    }

    public List<Rectangle> getLedgeBoundingBoxes() {
        return Arrays.stream(ledges).map(l->l.boundingBox).collect(Collectors.toList());
    }

    public static StageDataComponent loadFromJson(String fileName) throws IOException {
        String jsonFile = new String(Files.readAllBytes(Paths.get(stageDirectoryPath + fileName)));
        return new Gson().fromJson(jsonFile, StageDataComponent.class);
    }
}
