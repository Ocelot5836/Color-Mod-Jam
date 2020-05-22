package io.github.ocelot.dimension.teleporter;

import io.github.ocelot.data.CapabilityPaintingSource;
import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * @author Ocelot
 */
public class StandardPaintingTeleporter implements ITeleporter
{
    private final WorldPaintingEntity entity;

    public StandardPaintingTeleporter(WorldPaintingEntity entity)
    {
        this.entity = entity;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        entity.getCapability(CapabilityPaintingSource.SOURCE_PAINTING_CAPABILITY).ifPresent(data -> data.setSourcePainting(this.entity.getUniqueID()));
        BlockPos pos = PaintingManager.get(currentWorld).getPaintingStartPosition(this.entity.getPaintingId());
        if (pos != null)
        {
            BlockPos.Mutable spawnPos = new BlockPos.Mutable();
            for (int y = 0; y < 256; y++)
            {
                if (!destWorld.isAirBlock(spawnPos.setPos(pos.getX(), 255 - y, pos.getZ())))
                {
                    entity.setLocationAndAngles(pos.getX() + 0.5, 255 - y + 1, pos.getZ() + 0.5, -45, entity.rotationPitch);
                    break;
                }
            }
        }
        return repositionEntity.apply(false);
    }
}
