package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.client.screen.BobRossTradeScreenFactory;
import io.github.ocelot.container.BobRossTradeContainer;
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

    public static final RegistryObject<ContainerType<BobRossTradeContainer>> BOB_ROSS = CONTAINERS.register("bob_ross", () -> new ContainerType<>(new BobRossTradeScreenFactory()));
}
