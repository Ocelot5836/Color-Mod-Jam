package io.github.ocelot.entity;

import io.github.ocelot.init.PainterDimensions;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.SpawnWorldPaintingMessage;
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
import java.util.UUID;

/**
 * @author Ocelot
 */
public class WorldPaintingEntity extends HangingEntity implements PaintingHolder
{
    private UUID paintingId;

    public WorldPaintingEntity(EntityType<WorldPaintingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public WorldPaintingEntity(World world, BlockPos pos, Direction facing)
    {
        super(PainterEntities.WORLD_PAINTING.get(), world, pos);
        this.updateFacingWithBoundingBox(facing);
    }

    @OnlyIn(Dist.CLIENT)
    public WorldPaintingEntity(World world, BlockPos pos, Direction facing, @Nullable UUID paintingId)
    {
        this(world, pos, facing);
        this.paintingId = paintingId;
        this.updateFacingWithBoundingBox(facing);
    }

    private ItemStack getStack()
    {
        ItemStack stack = new ItemStack(PainterItems.WORLD_PAINTING.get());
        if (this.paintingId != null)
            PainterItems.WORLD_PAINTING.get().setPainting(stack, this.paintingId);
        return stack;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        this.serializePainting(nbt);
    }

    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        this.paintingId = this.deserializePainting(nbt);
    }

    @Override
    public void setPainting(@Nullable Painting painting)
    {
        if (this.world != null)
        {
            PaintingManager paintingManager = PaintingManager.get(this.world);
            if (!paintingManager.hasPainting(painting))
                paintingManager.addPainting(painting);
            this.paintingId = painting == null ? null : painting.getId();
        }
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

    @Override
    public void onCollideWithPlayer(PlayerEntity player)
    {
        if (!this.world.isRemote() && this.paintingId != null && PaintingManager.get(this.world).hasPainting(this.paintingId))
        {
            if (this.world.getDimension().getType() == DimensionType.OVERWORLD)
            {
                if (Painting.PLAD_PAINTING.getId().equals(this.paintingId))
                {
                    player.changeDimension(PainterDimensions.getDimensionType(PainterDimensions.PLAID_DIMENSION.get()), new PladTeleporter());
                    return;
                }
            }
            else if (this.world.getDimension().getType() == PainterDimensions.getDimensionType(PainterDimensions.PLAID_DIMENSION.get()))
            {
                if (Painting.PLAD_PAINTING.getId().equals(this.paintingId))
                {
                    player.changeDimension(DimensionType.OVERWORLD, new PladTeleporter());
                }
            }
        }
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
//        BlockPos blockpos = this.hangingPosition.add(x - this.getPosX(), y - this.getPosY(), z - this.getPosZ());
//        this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return PainterMessages.INSTANCE.toVanillaPacket(new SpawnWorldPaintingMessage(this), NetworkDirection.PLAY_TO_CLIENT);
    }
}
