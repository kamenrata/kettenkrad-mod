package com.example.kettenkrad.inventory;

import com.example.kettenkrad.KettenkradMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class KettenkradScreenHandler extends GenericContainerScreenHandler {

    public KettenkradScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(27));
    }

    public KettenkradScreenHandler(int syncId, PlayerInventory playerInv, Inventory inventory) {
        super(KettenkradMod.KETTENKRAD_SCREEN_HANDLER, syncId, playerInv, inventory, 3);
    }
}
