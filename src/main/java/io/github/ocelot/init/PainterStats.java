package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Ocelot
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PainterStats
{
    public static final ResourceLocation PAINT = registerCustom("paint", IStatFormatter.DEFAULT);
    public static final ResourceLocation CLEAN_PAINT_BRUSH = registerCustom("clean_paint_brush", IStatFormatter.DEFAULT);
    public static final ResourceLocation CLEAN_PAINT_BUCKET = registerCustom("clean_paint_bucket", IStatFormatter.DEFAULT);

    @SubscribeEvent
    public static void register(RegistryEvent.Register<StatType<?>> event)
    {
    }

    private static ResourceLocation registerCustom(String id, IStatFormatter formatter)
    {
        ResourceLocation registryName = new ResourceLocation(WorldPainter.MOD_ID, id);
        Registry.register(Registry.CUSTOM_STAT, id, registryName);
        Stats.CUSTOM.get(registryName, formatter);
        return registryName;
    }
}
