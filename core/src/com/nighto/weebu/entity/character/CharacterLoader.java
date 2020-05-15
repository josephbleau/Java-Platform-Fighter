package com.nighto.weebu.entity.character;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CharacterLoader {
    private CharacterLoader() {}

    public static CharacterData loadCharacterData() {
        Gson gson = new Gson();

        try {
            String jsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/character1.json")));
            return gson.fromJson(jsonFile, CharacterData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
