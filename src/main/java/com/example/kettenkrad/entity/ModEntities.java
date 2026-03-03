package com.example.kettenkrad.entity;

import com.example.kettenkrad.KettenkradMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<KettenkradEntity> KETTENKRAD =
        Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(KettenkradMod.MOD_ID, "kettenkrad"),
            FabricEntityTypeBuilder.<KettenkradEntity>create(SpawnGroup.MISC, KettenkradEntity::new)
                .dimensions(EntityDimensions.fixed(2.5f, 1.5f))
                .trackRangeBlocks(80)
                .trackedUpdateRate(3)
                .build()
        );

    public static void register() {}
}
