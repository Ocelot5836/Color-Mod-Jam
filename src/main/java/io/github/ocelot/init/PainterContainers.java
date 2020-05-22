package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.container.BobRossContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Ocelot
 */
public class PainterContainers
{
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, WorldPainter.MOD_ID);

    public static final RegistryObject<ContainerType<BobRossContainer>> BOB_ROSS = register("bob_ross", BobRossContainer::new);

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String id, ContainerType.IFactory<T> factory)
    {
        return CONTAINERS.register(id, () -> new ContainerType<>(factory));
    }
}
