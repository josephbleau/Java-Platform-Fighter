package com.nighto.weebu.entity.character.loader;

import com.badlogic.gdx.math.Rectangle;
import com.google.gson.Gson;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.InitialCharacterAttributes;
import com.nighto.weebu.entity.character.CharacterState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class CharacterAttributesLoader {
    private CharacterAttributesLoader() {}

    public static CharacterDataComponent loadCharacterData(String characterName) {
        Gson gson = new Gson();

        try {
            String attributesJsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/" + characterName + "/attributes.json")));
            String hurtboxesJsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/" + characterName + "/hurtboxes.json")));

            InitialCharacterAttributes initialCharacterAttributes = gson.fromJson(attributesJsonFile, InitialCharacterAttributes.class);
            Map<CharacterState, Rectangle> hurtboxes = gson.fromJson(hurtboxesJsonFile, Map.class);

            return new CharacterDataComponent(initialCharacterAttributes, hurtboxes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
