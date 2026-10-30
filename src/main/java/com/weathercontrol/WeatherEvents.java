package com.weathercontrol;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WeatherControlMod.MODID)
public class WeatherEvents {
    private static final int LONG = 1_000_000;

    public static void applyClear(ServerLevel level) {
        level.setWeatherParameters(6000, 0, false, false);
    }

    /** Fuerza el clima segun el modo activo. */
    public static void enforce(ServerLevel level, WeatherMode mode) {
        switch (mode) {
            case CLEAR -> level.setWeatherParameters(LONG, 0, false, false);
            case RAIN, SNOW -> level.setWeatherParameters(0, LONG, true, false);
            case THUNDER -> level.setWeatherParameters(0, LONG, true, true);
            default -> { }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        if (!(event.level instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;

        WeatherMode mode = WeatherState.get(level).getMode();
        if (mode == WeatherMode.NONE) return;

        long time = level.getGameTime();
        // Re-aplicar cada 5 s (por si dormiste, o otro mod/comando cambio el clima)
        if (time % 100 == 0) enforce(level, mode);

        if (mode == WeatherMode.SNOW && time % 10 == 0) {
            for (ServerPlayer player : level.players()) {
                snowAround(level, player);
            }
        }
    }

    /** Cubre de nieve (y congela agua) alrededor de cada jugador, incluso en biomas calidos. */
    private static void snowAround(ServerLevel level, ServerPlayer player) {
        for (int i = 0; i < 14; i++) {
            int x = player.getBlockX() + level.random.nextInt(65) - 32;
            int z = player.getBlockZ() + level.random.nextInt(65) - 32;
            if (!level.hasChunk(x >> 4, z >> 4)) continue;

            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (belowState.is(Blocks.WATER) && level.getFluidState(below).isSource()) {
                level.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
            } else if (belowState.is(Blocks.SNOW)) {
                int layers = belowState.getValue(SnowLayerBlock.LAYERS);
                if (layers < 3 && level.random.nextInt(4) == 0) {
                    level.setBlockAndUpdate(below, belowState.setValue(SnowLayerBlock.LAYERS, layers + 1));
                }
            } else if (level.getBlockState(pos).isAir()
                    && level.canSeeSky(pos)
                    && Blocks.SNOW.defaultBlockState().canSurvive(level, pos)) {
                level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
            }
        }
    }
}
