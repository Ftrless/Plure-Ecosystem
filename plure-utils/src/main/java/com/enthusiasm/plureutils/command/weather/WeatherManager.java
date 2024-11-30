package com.enthusiasm.plureutils.command.weather;

import net.minecraft.server.world.ServerWorld;

public class WeatherManager {
    public static void setDay(ServerWorld world) {
        world.setTimeOfDay(3000L);
    }
    public static void setNight(ServerWorld world) {
        world.setTimeOfDay(13000L);
    }

    public static void setSunny(ServerWorld world) {
        world.setWeather(1000000, 0, false, false);
    }
}
