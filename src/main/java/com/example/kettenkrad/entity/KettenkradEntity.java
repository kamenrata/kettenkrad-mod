package com.example.kettenkrad.entity;

import com.example.kettenkrad.inventory.KettenkradScreenHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class KettenkradEntity extends Entity {

    private static final float MAX_SPEED    = 0.28f;
    private static final float ACCELERATION = 0.015f;
    private static final float DECELERATION = 0.90f;
    private static final int   INV_SIZE     = 27;

    private static final TrackedData<Float> SPEED =
        DataTracker.registerData(KettenkradEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> SPEED_ACCESSOR = SPEED;

    private final SimpleInventory inventory = new SimpleInventory(INV_SIZE);
    private int     engineSoundTimer = 0;
    private boolean wasRiding        = false;
    private float   currentYaw       = 0f;

    public KettenkradEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPEED, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;

        boolean onGround = isOnGround();
        Entity driver = getFirstPassenger();

        if (driver instanceof PlayerEntity player) {
            if (!wasRiding) {
                getWorld().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 0.4f, 1.8f);
                wasRiding  = true;
                currentYaw = getYaw();
                engineSoundTimer = 0;
            }

            handleDriving(player);

            engineSoundTimer++;
            if (engineSoundTimer >= 15) {
                float spd   = Math.abs(dataTracker.get(SPEED));
                float ratio = spd / MAX_SPEED;
                var sound = ratio > 0.15f
                    ? SoundEvents.ENTITY_MINECART_RIDING
                    : SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE;
                getWorld().playSound(null, getX(), getY(), getZ(),
                    sound, SoundCategory.NEUTRAL, 0.3f + ratio * 0.5f, 0.5f + ratio);
                engineSoundTimer = 0;
            }

        } else {
            if (wasRiding) {
                getWorld().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 0.3f, 0.6f);
                wasRiding = false;
            }
            engineSoundTimer = 0;
            float spd = dataTracker.get(SPEED) * DECELERATION;
            if (Math.abs(spd) < 0.001f) spd = 0f;
            dataTracker.set(SPEED, spd);
        }

        applyMovement(onGround);
    }

    private void handleDriving(PlayerEntity player) {
        float cur     = dataTracker.get(SPEED);
        float forward = player.forwardSpeed;

        if      (forward > 0.01f)  cur = Math.min(cur + ACCELERATION, MAX_SPEED);
        else if (forward < -0.01f) cur = Math.max(cur - ACCELERATION, -MAX_SPEED * 0.4f);
        else                       { cur *= DECELERATION; if (Math.abs(cur) < 0.001f) cur = 0f; }

        // マウスの向きに滑らかに旋回
        float targetYaw = player.getYaw();
        float yawDiff   = targetYaw - currentYaw;
        while (yawDiff >  180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        if (cur < 0) yawDiff = -yawDiff;
        currentYaw += yawDiff * 0.15f;

        setYaw(currentYaw);
        setHeadYaw(currentYaw);
        dataTracker.set(SPEED, cur);
    }

    private void applyMovement(boolean onGround) {
        float  speed = dataTracker.get(SPEED);
        double rad   = Math.toRadians(currentYaw);
        double dx    = -Math.sin(rad) * speed;
        double dz    =  Math.cos(rad) * speed;

        // 重力：地面にいるときは沈まないよう固定
        double dy;
        if (onGround) {
            dy = speed != 0 ? 0.1 : 0.0; // 段差乗り越え用に少し上向き
        } else {
            dy = getVelocity().y - 0.05; // 空中は緩い重力
        }

        setVelocity(dx, dy, dz);
        move(MovementType.SELF, getVelocity());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (getWorld().isClient) return ActionResult.SUCCESS;
        if (player.isSneaking()) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, p) -> new KettenkradScreenHandler(syncId, inv, inventory),
                Text.translatable("entity.kettenkrad.kettenkrad")
            ));
            return ActionResult.SUCCESS;
        }
        if (getFirstPassenger() == null) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return getPassengerList().size() < 2;
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        boolean isDriver = getPassengerList().indexOf(passenger) == 0;
        return getPos().add(isDriver ? 0.0 : 0.5, 1.1, isDriver ? -0.2 : -0.5);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return getPos().add(2.0, 0.5, 0);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (getWorld().isClient || isRemoved()) return false;
        if (!source.isOf(net.minecraft.entity.damage.DamageTypes.EXPLOSION)) return false;
        this.kill();
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < INV_SIZE; i++) items.set(i, inventory.getStack(i));
        nbt.put("Inventory", Inventories.writeNbt(new NbtCompound(), items, getWorld().getRegistryManager()));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
        Inventories.readNbt(nbt.getCompound("Inventory"), items, getWorld().getRegistryManager());
        for (int i = 0; i < INV_SIZE; i++) inventory.setStack(i, items.get(i));
    }

    public SimpleInventory getKettenkradInventory() { return inventory; }
    @Override public boolean shouldSave() { return true; }
}
