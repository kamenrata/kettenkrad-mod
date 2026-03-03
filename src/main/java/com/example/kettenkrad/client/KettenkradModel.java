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

    // ── パーツ ──
    private final ModelPart body;       // 車体
    private final ModelPart hood;       // ボンネット
    private final ModelPart frontFork;  // フロントフォーク
    private final ModelPart frontWheel; // 前輪
    private final ModelPart leftTrack;  // 左キャタピラ
    private final ModelPart rightTrack; // 右キャタピラ
    private final ModelPart seat;       // シート
    private final ModelPart handlebar;  // ハンドル
    private final ModelPart exhaust;    // 排気管

    public KettenkradModel(ModelPart root) {
        body       = root.getChild("body");
        hood       = root.getChild("hood");
        frontFork  = root.getChild("front_fork");
        frontWheel = root.getChild("front_wheel");
        leftTrack  = root.getChild("left_track");
        rightTrack = root.getChild("right_track");
        seat       = root.getChild("seat");
        handlebar  = root.getChild("handlebar");
        exhaust    = root.getChild("exhaust");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData md = new ModelData();
        ModelPartData root = md.getRoot();

        // 車体（メインボディ）横長の箱
        root.addChild("body",
            ModelPartBuilder.create().uv(0, 0)
                .cuboid(-5, -3, -6, 10, 5, 14),
            ModelTransform.pivot(0, 14, 0));

        // ボンネット（エンジン部分、前方）
        root.addChild("hood",
            ModelPartBuilder.create().uv(0, 19)
                .cuboid(-4, -4, -6, 8, 4, 8),
            ModelTransform.pivot(0, 14, -6));

        // フロントフォーク
        root.addChild("front_fork",
            ModelPartBuilder.create().uv(34, 0)
                .cuboid(-1, -8, -1, 2, 10, 2),
            ModelTransform.pivot(0, 14, -16));

        // 前輪（バイク部分）
        root.addChild("front_wheel",
            ModelPartBuilder.create().uv(40, 12)
                .cuboid(-2, -5, -2, 4, 10, 4),
            ModelTransform.pivot(0, 18, -16));

        // 左キャタピラ（横から見ると楕円形に見える長方形）
        root.addChild("left_track",
            ModelPartBuilder.create().uv(0, 40)
                .cuboid(-2, -3, -8, 3, 6, 18),
            ModelTransform.pivot(-7, 18, 0));

        // 右キャタピラ
        root.addChild("right_track",
            ModelPartBuilder.create().uv(0, 40)
                .cuboid(-1, -3, -8, 3, 6, 18),
            ModelTransform.pivot(5, 18, 0));

        // シート（運転席）
        root.addChild("seat",
            ModelPartBuilder.create().uv(44, 40)
                .cuboid(-3, -2, -2, 6, 2, 8),
            ModelTransform.pivot(0, 11, -1));

        // ハンドルバー
        root.addChild("handlebar",
            ModelPartBuilder.create().uv(0, 52)
                .cuboid(-6, -1, -1, 12, 2, 2),
            ModelTransform.pivot(0, 10, -13));

        // 排気管（右側面）
        root.addChild("exhaust",
            ModelPartBuilder.create().uv(44, 44)
                .cuboid(0, -1, -4, 2, 2, 10),
            ModelTransform.pivot(5, 14, -2));

        return TexturedModelData.of(md, 128, 64);
    }

    @Override
    public void setAngles(KettenkradEntity entity, float limbAngle, float limbDistance,
                          float animTime, float headYaw, float headPitch) {
        float spd = Math.abs(entity.getDataTracker().get(KettenkradEntity.SPEED_ACCESSOR));
        // キャタピラと前輪を回転
        leftTrack.pitch  += spd * 0.8f;
        rightTrack.pitch += spd * 0.8f;
        frontWheel.pitch += spd * 0.8f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices,
                       int light, int overlay, int color) {
        body.render(matrices, vertices, light, overlay, color);
        hood.render(matrices, vertices, light, overlay, color);
        frontFork.render(matrices, vertices, light, overlay, color);
        frontWheel.render(matrices, vertices, light, overlay, color);
        leftTrack.render(matrices, vertices, light, overlay, color);
        rightTrack.render(matrices, vertices, light, overlay, color);
        seat.render(matrices, vertices, light, overlay, color);
        handlebar.render(matrices, vertices, light, overlay, color);
        exhaust.render(matrices, vertices, light, overlay, color);
    }
}
