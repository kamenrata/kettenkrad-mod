package com.example.kettenkrad.item;

import com.example.kettenkrad.KettenkradMod;
import com.example.kettenkrad.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item KETTENKRAD_ITEM = Registry.register(
        Registries.ITEM, Identifier.of(KettenkradMod.MOD_ID, "kettenkrad"),
        new KettenkradItem(new Item.Settings().maxCount(1)));

    public static final Item KETTENKRAD_SPAWN_EGG = Registry.register(
        Registries.ITEM, Identifier.of(KettenkradMod.MOD_ID, "kettenkrad_spawn_egg"),
        new SpawnEggItem(ModEntities.KETTENKRAD, 0x4a3728, 0x8b7355, new Item.Settings()));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TRANSPORT).register(entries -> {
            entries.add(KETTENKRAD_ITEM);
            entries.add(KETTENKRAD_SPAWN_EGG);
        });
    }
}
