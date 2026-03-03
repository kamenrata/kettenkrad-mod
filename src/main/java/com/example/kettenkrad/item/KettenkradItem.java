package com.example.kettenkrad.item;

import com.example.kettenkrad.entity.KettenkradEntity;
import com.example.kettenkrad.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KettenkradItem extends Item {
    public KettenkradItem(Settings settings) { super(settings); }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;
        BlockPos pos = ctx.getBlockPos().up();
        KettenkradEntity entity = new KettenkradEntity(ModEntities.KETTENKRAD, world);
        entity.setPos(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
        entity.setYaw(ctx.getPlayerFacing().asRotation());
        world.spawnEntity(entity);
        PlayerEntity player = ctx.getPlayer();
        if (player != null && !player.isCreative()) ctx.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
