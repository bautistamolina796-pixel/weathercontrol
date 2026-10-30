package com.weathercontrol;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WeatherItem extends Item {
    private final WeatherMode mode;

    public WeatherItem(WeatherMode mode, Properties props) {
        super(props);
        this.mode = mode;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        ServerLevel overworld = serverLevel.getServer().overworld();
        WeatherState state = WeatherState.get(overworld);
        Component name = Component.translatable("weathercontrol.mode." + mode.name().toLowerCase());

        if (state.getMode() == mode) {
            // Segundo click derecho: desactivar
            state.setMode(WeatherMode.NONE);
            WeatherEvents.applyClear(overworld);
            stack.getOrCreateTag().putBoolean("Active", false);
            player.displayClientMessage(Component.translatable("weathercontrol.message.off", name), true);
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 1f, 0.7f);
        } else {
            // Activar (si habia otro modo activo, se reemplaza)
            state.setMode(mode);
            WeatherEvents.enforce(overworld, mode);
            stack.getOrCreateTag().putBoolean("Active", true);
            player.displayClientMessage(Component.translatable("weathercontrol.message.on", name), true);
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 1f, 1.2f);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /** Mantiene el brillo del item sincronizado con el estado real del clima. */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(level instanceof ServerLevel sl)) return;
        if (level.getGameTime() % 20 != 0) return;
        boolean active = WeatherState.get(sl.getServer().overworld()).getMode() == mode;
        if (stack.getOrCreateTag().getBoolean("Active") != active) {
            stack.getOrCreateTag().putBoolean("Active", active);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean("Active");
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
