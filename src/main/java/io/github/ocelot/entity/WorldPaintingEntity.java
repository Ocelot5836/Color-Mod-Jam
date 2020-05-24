package io.github.ocelot.entity;

import io.github.ocelot.dimension.teleporter.FixedPaintingTeleporter;
import io.github.ocelot.dimension.teleporter.StandardPaintingTeleporter;
import io.github.ocelot.init.PainterDimensions;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.SpawnWorldPaintingMessage;
import io.github.ocelot.painting.FixedPaintingType;
import io.github.ocelot.painting.Painting;
import io.github.ocelot.painting.PaintingHolder;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class WorldPaintingEntity extends HangingEntity implements PaintingHolder
{
    private UUID paintingId;
    private boolean teleportation;

    public WorldPaintingEntity(EntityType<WorldPaintingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public WorldPaintingEntity(World world, BlockPos pos, Direction facing, boolean teleportation)
    {
        super(PainterEntities.WORLD_PAINTING.get(), world, pos);
        this.teleportation = teleportation;
        this.updateFacingWithBoundingBox(facing);
    }

    @OnlyIn(Dist.CLIENT)
    public WorldPaintingEntity(World world, BlockPos pos, Direction facing, boolean teleportation, @Nullable UUID paintingId)
    {
        this(world, pos, facing, teleportation);
        this.paintingId = paintingId;
        this.updateFacingWithBoundingBox(facing);
    }

    private ItemStack getStack()
    {
        if (FixedPaintingType.isFixed(this.paintingId))
            return FixedPaintingType.getType(this.paintingId).getStack();
        ItemStack stack;
        if (this.teleportation)
        {
            stack = new ItemStack(PainterItems.TELEPORTATION_PAINTING.get());
            if (this.paintingId != null)
                PainterItems.TELEPORTATION_PAINTING.get().setPainting(stack, this.paintingId);
        }
        else
        {
            stack = new ItemStack(PainterItems.WORLD_PAINTING.get());
            if (this.paintingId != null)
                PainterItems.WORLD_PAINTING.get().setPainting(stack, this.paintingId);
        }
        return stack;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        this.serializePainting(nbt);
        nbt.putBoolean("teleportation", this.teleportation);
    }

    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        this.paintingId = this.deserializePainting(nbt);
        this.teleportation = nbt.getBoolean("teleportation");
        this.updateBoundingBox();
    }

    @Override
    public void setPainting(@Nullable UUID paintingId)
    {
        this.paintingId = paintingId;
    }

    public boolean isTeleportation()
    {
        return teleportation;
    }

    @Nullable
    @Override
    public UUID getPaintingId()
    {
        return paintingId;
    }

    @Override
    public int getWidthPixels()
    {
        return Painting.SIZE;
    }

    @Override
    public int getHeightPixels()
    {
        return Painting.SIZE;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return this.getStack();
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity)
    {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof PlayerEntity)
            {
                PlayerEntity playerentity = (PlayerEntity) brokenEntity;
                if (playerentity.abilities.isCreativeMode)
                {
                    return;
                }
            }

            this.entityDropItem(this.getStack());
        }
    }

    private void warp(Entity entity)
    {
        if (!this.world.isRemote())
        {
            if (!this.getBoundingBox().intersects(entity.getBoundingBox()))
                return;

            if (this.teleportation && this.paintingId != null && PaintingManager.get(this.world).hasPainting(this.paintingId))
            {
                if (this.world.getDimension().getType() == DimensionType.OVERWORLD)
                {
                    if (this.paintingId.equals(FixedPaintingType.PLAID.getPainting().getId()))
                    {
                        entity.changeDimension(PainterDimensions.getDimensionType(PainterDimensions.PLAID_DIMENSION.get()), new FixedPaintingTeleporter(this));
                    }
                    else if (PaintingManager.get(this.world).initializeRealm(this.paintingId))
                    {
                        entity.changeDimension(PainterDimensions.getDimensionType(PainterDimensions.PAINTED_DIMENSION.get()), new StandardPaintingTeleporter(this));
                    }
                }
            }
        }
        return;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!this.isAlive())
            return;

        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());

        for (Entity entity : list)
        {
            if (!(entity instanceof PlayerEntity) && entity.isAlive() && !entity.isSpectator())
            {
                this.warp(entity);
            }
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entity)
    {
        this.warp(entity);
    }

    @Override
    public void playPlaceSound()
    {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        this.setPosition(x, y, z);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        BlockPos blockpos = this.hangingPosition.add(x - this.getPosX(), y - this.getPosY(), z - this.getPosZ());
        this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return PainterMessages.INSTANCE.toVanillaPacket(new SpawnWorldPaintingMessage(this), NetworkDirection.PLAY_TO_CLIENT);
    }
}
