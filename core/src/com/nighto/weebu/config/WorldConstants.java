package com.nighto.weebu.config;

import com.badlogic.gdx.Application;

public class WorldConstants {
    public static final float VIEWPORT_WIDTH = 1920f;
    public static final float VIEWPORT_HEIGHT = 1080f;

    // Conversion from world units to pixels (for rendering)
    // Viewport of 20m x 11.25m or 1920px x 1080px
    // 1m = 96px
    public static float UNIT_TO_PX = 96f;
    public static float PX_TO_UNIT = 1f/96.0f;

    public static int LOG_LEVEL = Application.LOG_DEBUG;
    public static boolean DEBUG = true;
}
