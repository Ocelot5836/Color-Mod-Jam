package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.dimension.PaintedBiome;
import io.github.ocelot.item.PaintbrushItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WorldPainter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry
{
    public static final ColorResolver PAINTED_LEAVES_RESOLVER = (biome, posX, posZ) -> biome instanceof PaintedBiome ? ((PaintedBiome) biome).getFoliageColor(posX, posZ) : -1;

    @OnlyIn(Dist.CLIENT)
    public static void init(IEventBus bus)
    {
        MinecraftForge.EVENT_BUS.register(ClientRegistry.class);
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof ClientWorld)
        {
            try
            {
                Object2ObjectArrayMap<ColorResolver, ColorCache> map = (Object2ObjectArrayMap<ColorResolver, ColorCache>) ObfuscationReflectionHelper.findField(ClientWorld.class, "field_228315_B_").get(event.getWorld());
                if (!map.containsKey(PAINTED_LEAVES_RESOLVER))
                    map.put(PAINTED_LEAVES_RESOLVER, new ColorCache());
            } catch (IllegalAccessException e)
            {
                WorldPainter.LOGGER.error("Could not access block colors", e);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
        blockColors.register((state, world, pos, layer) ->
        {
            if (world != null && pos != null)
            {
                GameSettings settings = Minecraft.getInstance().gameSettings;
                int biomeBlend = settings.biomeBlendRadius;
                settings.biomeBlendRadius = 0;
                int color = world.getBlockColor(pos, PAINTED_LEAVES_RESOLVER);
                settings.biomeBlendRadius = biomeBlend;
                return color;
            }
            return -1;
        }, PainterBlocks.PAINTED_LEAVES.get());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Item event)
    {
        BlockColors blockColors = event.getBlockColors();
        ItemColors itemColors = event.getItemColors();
        itemColors.register((stack, layer) -> layer == 0 ? -1 : PaintbrushItem.getColor(stack), PainterItems.PAINT_BRUSH.get());
        itemColors.register((stack, layer) ->
        {
            if (!(stack.getItem() instanceof BlockItem))
                return -1;
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return blockColors.getColor(blockstate, null, null, layer);
        }, PainterBlocks.PAINTED_LEAVES.get());
    }
}
