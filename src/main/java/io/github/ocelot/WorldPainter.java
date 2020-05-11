package io.github.ocelot;

import io.github.ocelot.init.ClientRegistry;
import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterDimensions;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.item.PaintDyeable;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod(WorldPainter.MOD_ID)
public class WorldPainter
{
    public static final String MOD_ID = "worldpainter";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup TAB = new ItemGroup(MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            ItemStack stack = new ItemStack(PainterItems.PAINT_BRUSH.get());
            PainterItems.PAINT_BRUSH.get().setColor(stack, 0xFF00FF);
            PainterItems.PAINT_BRUSH.get().setPaint(stack, PaintDyeable.MAX_PAINT);
            return stack;
        }
    };

    public WorldPainter()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        PainterBlocks.BLOCKS.register(bus);
        PainterBlocks.TILE_ENTTIES.register(bus);
        PainterItems.ITEMS.register(bus);
        PainterDimensions.DIMENSIONS.register(bus);
        PainterDimensions.BIOMES.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(FMLCommonSetupEvent event)
    {
    }

    private void initClient(FMLClientSetupEvent event)
    {
        ClientRegistry.init(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onEvent(RegisterDimensionsEvent event)
    {
        DimensionManager.registerOrGetDimension(PainterDimensions.PAINTED_DIMENSION.getId(), PainterDimensions.PAINTED_DIMENSION.get(), null, true);
        DimensionManager.registerOrGetDimension(PainterDimensions.PLAID_DIMENSION.getId(), PainterDimensions.PLAID_DIMENSION.get(), null, true);
    }
}
