package io.github.ocelot;

import io.github.ocelot.client.PainterClientRegistry;
import io.github.ocelot.data.CapabilityPaintingSource;
import io.github.ocelot.dimension.teleporter.DeathPaintingTeleporter;
import io.github.ocelot.init.*;
import io.github.ocelot.network.SyncPaintingRealmsMessage;
import io.github.ocelot.network.SyncPaintingsMessage;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod(WorldPainter.MOD_ID)
public class WorldPainter
{
    public static final String MOD_ID = "worldpainter";

    public static final ItemGroup TAB = new ItemGroup(MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            ItemStack stack = new ItemStack(PainterItems.SMALL_PAINT_BRUSH.get());
            PainterItems.SMALL_PAINT_BRUSH.get().setColor(stack, 0xFF00FF); // Yes
            PainterItems.SMALL_PAINT_BRUSH.get().setPaint(stack, PainterItems.SMALL_PAINT_BRUSH.get().getBrush(stack).getMaxPaint());
            return stack;
        }
    };

    public WorldPainter()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            PainterClientRegistry.init(bus);
            bus.addListener(PainterClientRegistry::setup);
        });
        bus.addListener(this::enqueueIMC);
        PainterBlocks.BLOCKS.register(bus);
        PainterBlocks.TILE_ENTTIES.register(bus);
        PainterItems.ITEMS.register(bus);
        PainterContainers.CONTAINERS.register(bus);
        PainterEntities.ENTITIES.register(bus);
        PainterDimensions.DIMENSIONS.register(bus);
        PainterDimensions.BIOMES.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(FMLCommonSetupEvent event)
    {
        PainterMessages.init();
        CapabilityPaintingSource.register();
    }

    private void enqueueIMC(InterModEnqueueEvent event)
    {
        if (ModList.get().isLoaded("curios"))
        {
            InterModComms.sendTo(CuriosAPI.MODID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("head"));
        }
    }

    @SubscribeEvent
    public void onEvent(RegisterDimensionsEvent event)
    {
        PainterDimensions.getDimensionType(PainterDimensions.PAINTED_DIMENSION.get());
        PainterDimensions.getDimensionType(PainterDimensions.PLAID_DIMENSION.get());
    }

    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity playerEntity = event.getPlayer();
        World world = playerEntity.world;
        if (!world.isRemote() && playerEntity instanceof ServerPlayerEntity)
        {
            SyncPaintingsMessage.sendTo(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), PaintingManager.get(world).getAllPaintings());
            SyncPaintingRealmsMessage.sendTo(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), PaintingManager.get(world).getAllPaintingRealms());
        }
    }

    @SubscribeEvent
    public void onEvent(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getHealth() - event.getAmount() <= 0 && (entity.dimension == PainterDimensions.getDimensionType(PainterDimensions.PAINTED_DIMENSION.get()) || entity.dimension == PainterDimensions.getDimensionType(PainterDimensions.PLAID_DIMENSION.get())))
        {
            entity.getCapability(CapabilityPaintingSource.SOURCE_PAINTING_CAPABILITY).ifPresent(data ->
            {
                if (data.getSourcePainting() != null)
                {
                    event.setCanceled(true);
                    entity.fallDistance = 0;
                    entity.setFireTimer(0);
                    entity.clearActivePotions();
                    boolean creative = entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
                    if (!creative)
                    {
                        entity.setHealth(1);
                        entity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                        entity.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                    }
                    else
                    {
                        entity.setHealth(entity.getMaxHealth());
                    }
                    entity.changeDimension(DimensionType.OVERWORLD, new DeathPaintingTeleporter(data.getSourcePainting()));
                    data.setSourcePainting(null);
                }
            });
        }
    }
}
