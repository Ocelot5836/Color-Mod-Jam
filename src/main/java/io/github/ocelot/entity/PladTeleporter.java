package io.github.ocelot.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class PladTeleporter implements ITeleporter
{
    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        entity.setLocationAndAngles(0, 256, 0, yaw, entity.rotationPitch);
        return repositionEntity.apply(false);
    }
}
