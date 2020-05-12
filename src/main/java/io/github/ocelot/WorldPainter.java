package io.github.ocelot;

import io.github.ocelot.init.*;
import io.github.ocelot.item.PaintDyeable;
import io.github.ocelot.network.SyncPaintingsMessage;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
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
            PainterItems.PAINT_BRUSH.get().setColor(stack, 0xFF00FF); // Yes
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
        PainterEntities.ENTITIES.register(bus);
        PainterItems.ITEMS.register(bus);
        PainterDimensions.BIOMES.register(bus);
        PainterDimensions.DIMENSIONS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(FMLCommonSetupEvent event)
    {
        PainterMessages.init();
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

    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity playerEntity = event.getPlayer();
        World world = playerEntity.world;
        if (!world.isRemote() && playerEntity instanceof ServerPlayerEntity)
        {
            PainterMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new SyncPaintingsMessage(PaintingManager.get(world).getAllPaintings()));
        }
    }
}
