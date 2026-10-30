package com.weathercontrol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Guarda que modo de clima esta activo (se conserva al cerrar el mundo). */
public class WeatherState extends SavedData {
    private WeatherMode mode = WeatherMode.NONE;

    public static WeatherState get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(WeatherState::load, WeatherState::new, "weathercontrol_state");
    }

    public static WeatherState load(CompoundTag tag) {
        WeatherState s = new WeatherState();
        try {
            s.mode = WeatherMode.valueOf(tag.getString("mode"));
        } catch (IllegalArgumentException ignored) {
            s.mode = WeatherMode.NONE;
        }
        return s;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("mode", mode.name());
        return tag;
    }

    public WeatherMode getMode() { return mode; }

    public void setMode(WeatherMode mode) {
        this.mode = mode;
        setDirty();
    }
}
