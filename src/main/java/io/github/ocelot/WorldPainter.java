package io.github.ocelot;

import io.github.ocelot.init.ClientRegistry;
import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterDimensions;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.item.PaintbrushItem;
import net.minecraft.item.DyeColor;
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
            PaintbrushItem.setColor(stack, DyeColor.WHITE.getColorValue());
            return stack;
        }
    };

    public WorldPainter()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        PainterBlocks.REGISTRY.register(bus);
        PainterItems.REGISTRY.register(bus);
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
        if (event.getMissingNames().contains(PainterDimensions.PAINTED_DIMENSION.getId()))
            DimensionManager.registerDimension(PainterDimensions.PAINTED_DIMENSION.getId(), PainterDimensions.PAINTED_DIMENSION.get(), null, true);
    }
}
