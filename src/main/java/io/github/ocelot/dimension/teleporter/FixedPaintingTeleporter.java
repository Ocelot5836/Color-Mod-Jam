package io.github.ocelot.dimension.teleporter;

import io.github.ocelot.data.CapabilityPaintingSource;
import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * @author Ocelot
 */
public class FixedPaintingTeleporter implements ITeleporter
{
    private final WorldPaintingEntity entity;

    public FixedPaintingTeleporter(WorldPaintingEntity entity)
    {
        this.entity = entity;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        entity.getCapability(CapabilityPaintingSource.SOURCE_PAINTING_CAPABILITY).ifPresent(data -> data.setSourcePainting(this.entity.getUniqueID()));
        BlockPos.Mutable spawnPos = new BlockPos.Mutable();
        for (int y = 0; y < 256; y++)
        {
            if (!destWorld.isAirBlock(spawnPos.setPos(0, 255 - y, 0)))
            {
                spawnPos.setPos(0, 255 - y + 1, 0);
                destWorld.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(spawnPos), 3, spawnPos);
                entity.setLocationAndAngles(0.5, 255 - y + 1, 0.5, yaw, entity.rotationPitch);
                break;
            }
        }
        return repositionEntity.apply(false);
    }
}
