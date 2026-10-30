package com.weathercontrol;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(WeatherControlMod.MODID)
public class WeatherControlMod {
    public static final String MODID = "weathercontrol";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Item> RAIN_ITEM = ITEMS.register("rain_item",
            () -> new WeatherItem(WeatherMode.RAIN, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SNOW_ITEM = ITEMS.register("snow_item",
            () -> new WeatherItem(WeatherMode.SNOW, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THUNDER_ITEM = ITEMS.register("thunder_item",
            () -> new WeatherItem(WeatherMode.THUNDER, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CLEAR_ITEM = ITEMS.register("clear_item",
            () -> new WeatherItem(WeatherMode.CLEAR, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("weather_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.weathercontrol"))
                    .icon(() -> new ItemStack(RAIN_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(RAIN_ITEM.get());
                        output.accept(SNOW_ITEM.get());
                        output.accept(THUNDER_ITEM.get());
                        output.accept(CLEAR_ITEM.get());
                    })
                    .build());

    public WeatherControlMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
