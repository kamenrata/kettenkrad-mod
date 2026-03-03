package com.example.kettenkrad.client;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class KettenkradClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.KETTENKRAD, KettenkradRenderer::new);
        HandledScreens.register(KettenkradMod.KETTENKRAD_SCREEN_HANDLER, GenericContainerScreen::new);
    }
}
