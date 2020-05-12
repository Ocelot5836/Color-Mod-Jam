package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
public class PainterEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, WorldPainter.MOD_ID);

    public static final RegistryObject<EntityType<WorldPaintingEntity>> WORLD_PAINTING = register("world_painting", EntityType.Builder.<WorldPaintingEntity>create(WorldPaintingEntity::new, EntityClassification.MISC).setTrackingRange(10).setUpdateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(false).size(0.5F, 0.5F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.Builder<T> builder)
    {
        return ENTITIES.register(id, () -> builder.build(id));
    }
}
