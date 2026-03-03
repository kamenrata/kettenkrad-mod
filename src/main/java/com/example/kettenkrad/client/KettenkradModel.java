package com.example.kettenkrad.client;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.KettenkradEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KettenkradModel extends EntityModel<KettenkradEntity> {

    public static final EntityModelLayer LAYER_LOCATION =
        new EntityModelLayer(Identifier.of(KettenkradMod.MOD_ID, "kettenkrad"), "main");

    private final ModelPart body, frontWheel, leftTrack, rightTrack, handlebar, seat;

    public KettenkradModel(ModelPart root) {
        body       = root.getChild("body");
        frontWheel = root.getChild("front_wheel");
        leftTrack  = root.getChild("left_track");
        rightTrack = root.getChild("right_track");
        handlebar  = root.getChild("handlebar");
        seat       = root.getChild("seat");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData md = new ModelData();
        ModelPartData root = md.getRoot();
        root.addChild("body",
            ModelPartBuilder.create().uv(0,0).cuboid(-8,-4,-12,16,8,24),
            ModelTransform.pivot(0,16,0));
        root.addChild("front_wheel",
            ModelPartBuilder.create().uv(0,32).cuboid(-2,-6,-2,4,12,4),
            ModelTransform.pivot(0,16,-16));
        root.addChild("left_track",
            ModelPartBuilder.create().uv(40,0).cuboid(-2,-3,-12,4,6,24),
            ModelTransform.pivot(-10,20,0));
        root.addChild("right_track",
            ModelPartBuilder.create().uv(40,0).cuboid(-2,-3,-12,4,6,24),
            ModelTransform.pivot(10,20,0));
        root.addChild("handlebar",
            ModelPartBuilder.create().uv(0,48).cuboid(-8,-2,-2,16,2,4),
            ModelTransform.pivot(0,10,-14));
        root.addChild("seat",
            ModelPartBuilder.create().uv(40,32).cuboid(-4,-2,-4,8,2,10),
            ModelTransform.pivot(0,12,-2));
        return TexturedModelData.of(md, 128, 64);
    }

    @Override
    public void setAngles(KettenkradEntity entity, float limbAngle, float limbDistance,
                          float animTime, float headYaw, float headPitch) {
        float spd = Math.abs(entity.getDataTracker().get(KettenkradEntity.SPEED_ACCESSOR));
        leftTrack.pitch  += spd * 0.5f;
        rightTrack.pitch += spd * 0.5f;
        frontWheel.pitch += spd * 0.5f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices,
                       int light, int overlay, int color) {
        body.render(matrices, vertices, light, overlay, color);
        frontWheel.render(matrices, vertices, light, overlay, color);
        leftTrack.render(matrices, vertices, light, overlay, color);
        rightTrack.render(matrices, vertices, light, overlay, color);
        handlebar.render(matrices, vertices, light, overlay, color);
        seat.render(matrices, vertices, light, overlay, color);
    }
}
