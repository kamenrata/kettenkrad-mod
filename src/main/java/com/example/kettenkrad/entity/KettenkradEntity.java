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
import net.minecraft.entity.player.PlayerInventory;
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

    private static final float MAX_SPEED    = 0.35f;
    private static final float ACCELERATION = 0.018f;
    private static final float FRICTION     = 0.92f;
    private static final int   INV_SIZE     = 27;

    private static final TrackedData<Float> SPEED =
        DataTracker.registerData(KettenkradEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> SPEED_ACCESSOR = SPEED;

    private final SimpleInventory inventory = new SimpleInventory(INV_SIZE);
    private int     engineSoundTimer = 0;
    private boolean wasRiding        = false;

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
        if (isOnGround()) {
            Vec3d v = getVelocity();
            if (v.y < 0) setVelocity(v.x, 0, v.z);
        }
        if (getWorld().isClient) return;

        Entity driver = getFirstPassenger();
        if (driver instanceof PlayerEntity player) {
            if (!wasRiding) {
                getWorld().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 0.4f, 1.8f);
                wasRiding = true;
                engineSoundTimer = 0;
            }
            handleDriving(player);
            engineSoundTimer++;
            if (engineSoundTimer >= 15) {
                float spd   = Math.abs(dataTracker.get(SPEED));
                float ratio = spd / MAX_SPEED;
                float pitch  = 0.5f + ratio * 1.0f;
                float volume = 0.3f + ratio * 0.5f;
                var sound = ratio > 0.15f
                    ? SoundEvents.ENTITY_MINECART_RIDING
                    : SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE;
                getWorld().playSound(null, getX(), getY(), getZ(),
                    sound, SoundCategory.NEUTRAL, volume, pitch);
                engineSoundTimer = 0;
            }
        } else {
            if (wasRiding) {
                getWorld().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 0.3f, 0.6f);
                wasRiding = false;
            }
            engineSoundTimer = 0;
            float spd = dataTracker.get(SPEED) * FRICTION;
            dataTracker.set(SPEED, spd);
            applyMovement(getYaw(), spd);
        }
    }

    private void handleDriving(PlayerEntity player) {
        float cur     = dataTracker.get(SPEED);
        float forward = player.forwardSpeed;
        if      (forward > 0.01f)  cur = Math.min(cur + ACCELERATION, MAX_SPEED);
        else if (forward < -0.01f) cur = Math.max(cur - ACCELERATION, -MAX_SPEED * 0.5f);
        else                       cur *= FRICTION;
        float yaw = player.getYaw();
        setYaw(yaw);
        setHeadYaw(yaw);
        dataTracker.set(SPEED, cur);
        applyMovement(yaw, cur);
    }

    private void applyMovement(float yaw, float speed) {
        double rad = Math.toRadians(yaw);
        double dx  = -Math.sin(rad) * speed;
        double dz  =  Math.cos(rad) * speed;
        double dy  = isOnGround() ? Math.min(getVelocity().y, 0) : getVelocity().y - 0.08;
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
        return getPos().add(isDriver ? 0.0 : 0.6, 0.9, -0.3);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return getPos().add(1.8, 0.1, 0);
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
    @Override public boolean collides()   { return !isRemoved(); }
}
