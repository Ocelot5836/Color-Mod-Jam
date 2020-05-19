package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.common.item.SpawnEggItemBase;
import io.github.ocelot.entity.BobRossEntity;
import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
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
    public static final RegistryObject<EntityType<BobRossEntity>> BOB_ROSS = register("bob_ross", 0xFF00FF, 0X7F007F, EntityType.Builder.create(BobRossEntity::new, EntityClassification.MISC).size(0.6F, 1.8F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, int primaryColor, int secondaryColor, EntityType.Builder<T> builder)
    {
        RegistryObject<EntityType<T>> object = register(id, builder);
        PainterItems.ITEMS.register(id + "_spawn_egg", () -> new SpawnEggItemBase<>(object, primaryColor, secondaryColor, new Item.Properties().group(WorldPainter.TAB)));
        return object;
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.Builder<T> builder)
    {
        return ENTITIES.register(id, () -> builder.build(id));
    }
}
