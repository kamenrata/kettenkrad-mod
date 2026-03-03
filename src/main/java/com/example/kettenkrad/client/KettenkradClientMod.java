package com.example.kettenkrad.client;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.ModEntities;
import com.example.kettenkrad.inventory.KettenkradScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class KettenkradClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // モデルレイヤーの登録（これが抜けていた！）
        EntityModelLayerRegistry.registerModelLayer(
            KettenkradModel.LAYER_LOCATION,
            KettenkradModel::getTexturedModelData
        );

        EntityRendererRegistry.register(ModEntities.KETTENKRAD, KettenkradRenderer::new);

        HandledScreens.register(KettenkradMod.KETTENKRAD_SCREEN_HANDLER, GenericContainerScreen::new);
    }
}
