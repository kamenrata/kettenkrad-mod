package com.example.kettenkrad.item;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item KETTENKRAD_ITEM = Registry.register(
        Registries.ITEM,
        Identifier.of(KettenkradMod.MOD_ID, "kettenkrad"),
        new KettenkradItem(new Item.Settings().maxCount(1)));

    // 専用タブ
    public static final ItemGroup KETTENKRAD_GROUP = Registry.register(
        Registries.ITEM_GROUP,
        Identifier.of(KettenkradMod.MOD_ID, "kettenkrad_tab"),
        FabricItemGroup.builder()
            .displayName(Text.literal("Kettenkrad"))
            .icon(() -> new ItemStack(KETTENKRAD_ITEM))
            .entries((ctx, entries) -> {
                entries.add(KETTENKRAD_ITEM);
            })
            .build()
    );

    public static void register() {}
}
