package com.example.kettenkrad.client;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.KettenkradEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class KettenkradRenderer extends EntityRenderer<KettenkradEntity> {

    private static final Identifier TEXTURE =
        Identifier.of(KettenkradMod.MOD_ID, "textures/entity/kettenkrad.png");

    private final KettenkradModel model;

    public KettenkradRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new KettenkradModel(
            ctx.getModelLoader().getModelPart(KettenkradModel.LAYER_LOCATION));
    }

    @Override
    public void render(KettenkradEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 1.5, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - yaw));
        matrices.scale(-1f, -1f, 1f);
        var vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        model.setAngles(entity, 0, 0, entity.age, yaw, 0);
        model.render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(KettenkradEntity entity) { return TEXTURE; }
}
