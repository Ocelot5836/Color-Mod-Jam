package io.github.ocelot.entity;

import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Ocelot
 */
public class PaintingTeleporter implements ITeleporter
{
    private final UUID paintingId;

    public PaintingTeleporter(UUID paintingId)
    {
        this.paintingId = paintingId;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        BlockPos pos = PaintingManager.get(currentWorld).getPaintingStartPosition(this.paintingId);
        if (pos != null)
        {
            BlockPos.Mutable spawnPos = new BlockPos.Mutable();
            for (int y = 0; y < 256; y++)
            {
                if (destWorld.isAirBlock(spawnPos.setPos(pos.getX(), y, pos.getZ())))
                {
                    entity.setLocationAndAngles(pos.getX() + 0.5, y, pos.getZ() + 0.5, yaw, entity.rotationPitch);
                    break;
                }
            }
        }
        return repositionEntity.apply(false);
    }
}
