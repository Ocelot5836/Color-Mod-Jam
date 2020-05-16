package io.github.ocelot.datagen;

import io.github.ocelot.WorldPainter;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = WorldPainter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PainterDataGenerators
{
    @SubscribeEvent
    public static void onEvent(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new PainterLootTableProvider(generator));
        generator.addProvider(new PainterRecipeProvider(generator));
    }
}
