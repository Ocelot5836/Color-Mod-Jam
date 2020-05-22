package io.github.ocelot.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Ocelot
 */
public class DeathPaintingTeleporter implements ITeleporter
{
    private final UUID paintingEntityId;

    public DeathPaintingTeleporter(UUID paintingEntityId)
    {
        this.paintingEntityId = paintingEntityId;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        Entity paintingEntity = destWorld.getEntityByUuid(this.paintingEntityId);
        if (paintingEntity instanceof WorldPaintingEntity)
        {
            Direction facing = paintingEntity.getHorizontalFacing().getOpposite();
            BlockPos paintingPos = ((WorldPaintingEntity) paintingEntity).getHangingPosition().offset(facing.getOpposite());
            entity.setPositionAndRotation(paintingPos.getX() + 0.5 + facing.getXOffset() * 0.5 - facing.rotateYCCW().getXOffset() * 0.5, paintingPos.getY(), paintingPos.getZ() + 0.5 + facing.getZOffset() * 0.5 - facing.rotateYCCW().getZOffset() * 0.5, facing.getHorizontalAngle(), entity.rotationPitch);
            entity.setMotion(facing.getOpposite().getXOffset() * 0.5, 0.5, facing.getOpposite().getZOffset() * 0.5);
        }
        else
        {
            entity.setLocationAndAngles(0, 256, 0, yaw, entity.rotationPitch);
        }
        return repositionEntity.apply(false);
    }
}

