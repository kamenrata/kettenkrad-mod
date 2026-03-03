package com.example.kettenkrad;

import com.example.kettenkrad.entity.ModEntities;
import com.example.kettenkrad.inventory.KettenkradScreenHandler;
import com.example.kettenkrad.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class KettenkradMod implements ModInitializer {
    public static final String MOD_ID = "kettenkrad";

    public static final ScreenHandlerType<KettenkradScreenHandler> KETTENKRAD_SCREEN_HANDLER =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "kettenkrad_inventory"),
            new ScreenHandlerType<>(KettenkradScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModItems.register();
    }
}
