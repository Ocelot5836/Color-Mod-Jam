package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.dimension.PaintedBiome;
import io.github.ocelot.item.PaintbrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WorldPainter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry
{
    public static final ColorResolver PAINTED_LEAVES_RESOLVER = (biome, posX, posZ) -> biome instanceof PaintedBiome ? ((PaintedBiome) biome).getFoliageColor(posX, posZ) : 0xffffff;

    @OnlyIn(Dist.CLIENT)
    public static void init(IEventBus bus)
    {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
        blockColors.register((state, world, pos, layer) -> world != null && pos != null ? world.getBlockColor(pos, PAINTED_LEAVES_RESOLVER) : -1, PainterBlocks.PAINTED_LEAVES.get());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Item event)
    {
        BlockColors blockColors = event.getBlockColors();
        ItemColors itemColors = event.getItemColors();
        itemColors.register((stack, layer) -> layer == 0 ? -1 : 0xFF000000 | PaintbrushItem.getColor(stack), PainterItems.PAINT_BRUSH.get());
        itemColors.register((stack, layer) ->
        {
            if (!(stack.getItem() instanceof BlockItem))
                return -1;
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return blockColors.getColor(blockstate, null, null, layer);
        }, PainterBlocks.PAINTED_LEAVES.get());
    }
}
