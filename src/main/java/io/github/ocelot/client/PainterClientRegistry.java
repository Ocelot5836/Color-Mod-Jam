package io.github.ocelot.client;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.client.render.entity.BobRossEntityRenderer;
import io.github.ocelot.client.render.entity.WorldPaintingEntityRenderer;
import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterScreens;
import io.github.ocelot.item.PaintDyeable;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import io.github.ocelot.client.render.tileentity.EaselTileEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WorldPainter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PainterClientRegistry
{
//    public static final ColorResolver PAINTED_LEAVES_RESOLVER = (biome, posX, posZ) -> biome instanceof PaintedLeavesColor ? ((PaintedLeavesColor) biome).getFoliageColor(posX, posZ) : -1;

    private static final Logger LOGGER = LogManager.getLogger();

    @OnlyIn(Dist.CLIENT)
    public static void init(IEventBus bus)
    {
        PainterScreens.init();

        ClientRegistry.bindTileEntityRenderer(PainterBlocks.EASEL_TE.get(), EaselTileEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PainterEntities.WORLD_PAINTING.get(), WorldPaintingEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PainterEntities.BOB_ROSS.get(), BobRossEntityRenderer::new);

//        RenderTypeLookup.setRenderLayer(PainterBlocks.PAINTED_LEAVES.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(PainterBlocks.PAINT_BUCKET.get(), RenderType.getCutout());
        MinecraftForge.EVENT_BUS.register(PainterClientRegistry.class);
    }

//    @SuppressWarnings("unchecked")
//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public static void onWorldLoad(WorldEvent.Load event)
//    {
//        if (event.getWorld() instanceof ClientWorld)
//        {
//            try
//            {
//                Object2ObjectArrayMap<ColorResolver, ColorCache> map = (Object2ObjectArrayMap<ColorResolver, ColorCache>) ObfuscationReflectionHelper.findField(ClientWorld.class, "field_228315_B_").get(event.getWorld());
//                if (!map.containsKey(PAINTED_LEAVES_RESOLVER))
//                    map.put(PAINTED_LEAVES_RESOLVER, new ColorCache());
//            } catch (IllegalAccessException e)
//            {
//                LOGGER.error("Could not access block colors", e);
//            }
//        }
//    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
//        blockColors.register((state, world, pos, layer) -> world != null && pos != null ? world.getBlockColor(pos, PAINTED_LEAVES_RESOLVER) : -1, PainterBlocks.PAINTED_LEAVES.get());
        blockColors.register((state, world, pos, layer) -> world != null && pos != null && world.getTileEntity(pos) instanceof PaintBucketTileEntity ? ((PaintBucketTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).getColor() : -1, PainterBlocks.PAINT_BUCKET.get());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(ColorHandlerEvent.Item event)
    {
        BlockColors blockColors = event.getBlockColors();
        ItemColors itemColors = event.getItemColors();

        Item[] paintDyeables = ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof PaintDyeable).toArray(Item[]::new);
        itemColors.register((stack, layer) -> layer == 0 ? -1 : ((PaintDyeable) stack.getItem()).getColor(stack), paintDyeables);
    }
}
