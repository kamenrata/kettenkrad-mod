package com.example.kettenkrad.inventory;

import com.example.kettenkrad.KettenkradMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class KettenkradScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public KettenkradScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(27));
    }

    public KettenkradScreenHandler(int syncId, PlayerInventory playerInv, Inventory inventory) {
        super(KettenkradMod.KETTENKRAD_SCREEN_HANDLER, syncId);
        checkSize(inventory, 27);
        this.inventory = inventory;
        inventory.onOpen(playerInv.player);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inventory, col + row*9, 8 + col*18, 18 + row*18));
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col + row*9 + 9, 8 + col*18, 84 + row*18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInv, col, 8 + col*18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 27) {
                if (!insertItem(stack, 27, slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!insertItem(stack, 0, 27, false)) return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}
