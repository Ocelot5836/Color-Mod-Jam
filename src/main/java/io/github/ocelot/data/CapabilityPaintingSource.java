package io.github.ocelot.data;

import io.github.ocelot.WorldPainter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class CapabilityPaintingSource
{
    public static final IDataSerializer<PaintingData> SOURCE_PAINTING_HOLDER = new IDataSerializer<PaintingData>()
    {
        @Override
        public void write(PacketBuffer buf, PaintingData value)
        {
            buf.writeBoolean(value.getSourcePainting() != null);
            if (value.getSourcePainting() != null)
                buf.writeUniqueId(value.getSourcePainting());
        }

        @Override
        public PaintingData read(PacketBuffer buf)
        {
            PaintingData paintingData = new PaintingDataImpl();
            if (buf.readBoolean())
                paintingData.setSourcePainting(buf.readUniqueId());
            return paintingData;
        }

        @Override
        public PaintingData copyValue(PaintingData value)
        {
            return new PaintingDataImpl(value);
        }
    };
    public static final DataParameter<PaintingData> SOURCE_PAINTING = EntityDataManager.createKey(Entity.class, SOURCE_PAINTING_HOLDER);

    @CapabilityInject(PaintingData.class)
    public static Capability<PaintingData> SOURCE_PAINTING_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(PaintingData.class, new Storage(), PaintingDataImpl::new);
        MinecraftForge.EVENT_BUS.register(CapabilityPaintingSource.class);
    }

    @SubscribeEvent
    public static void onEvent(AttachCapabilitiesEvent<Entity> event)
    {
        event.addCapability(new ResourceLocation(WorldPainter.MOD_ID, "source_painting"), new Provider());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.Clone event)
    {
        PlayerEntity player = event.getPlayer();
        LazyOptional<PaintingData> newPaintingData = event.getPlayer().getCapability(SOURCE_PAINTING_CAPABILITY);
        LazyOptional<PaintingData> oldPaintingData = event.getOriginal().getCapability(SOURCE_PAINTING_CAPABILITY);
        oldPaintingData.ifPresent(oldData ->
        {
            newPaintingData.ifPresent(newData -> newData.setSourcePainting(oldData.getSourcePainting()));
            player.getDataManager().set(SOURCE_PAINTING, new PaintingDataImpl(oldData));
        });
    }

    /**
     * <p>Specifies the source painting an entity came from.</p>
     *
     * @author Ocelot
     */
    public interface PaintingData
    {
        /**
         * @return The id of the painting the entity came from.
         */
        @Nullable
        UUID getSourcePainting();

        /**
         * Sets the painting the entity came out of to enter the dimension.
         *
         * @param paintingId The id of the painting
         */
        void setSourcePainting(@Nullable UUID paintingId);
    }

    private static class PaintingDataImpl implements PaintingData
    {
        private UUID paintingId;

        private PaintingDataImpl()
        {
        }

        private PaintingDataImpl(PaintingData other)
        {
            this.paintingId = other.getSourcePainting();
        }

        @Nullable
        @Override
        public UUID getSourcePainting()
        {
            return paintingId;
        }

        @Override
        public void setSourcePainting(@Nullable UUID paintingId)
        {
            this.paintingId = paintingId;
        }
    }

    private static class Storage implements Capability.IStorage<PaintingData>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<PaintingData> capability, PaintingData instance, Direction side)
        {
            CompoundNBT nbt = new CompoundNBT();
            if (instance.getSourcePainting() != null)
                nbt.putUniqueId("paintingId", instance.getSourcePainting());
            return nbt;
        }

        @Override
        public void readNBT(Capability<PaintingData> capability, PaintingData instance, Direction side, INBT nbt)
        {
            if (nbt instanceof CompoundNBT)
                instance.setSourcePainting(((CompoundNBT) nbt).getUniqueId("paintingId"));
        }
    }

    private static class Provider implements ICapabilitySerializable<INBT>
    {
        private final PaintingData paintingData;
        private final LazyOptional<PaintingData> handler;

        private Provider()
        {
            this.paintingData = new PaintingDataImpl();
            this.handler = LazyOptional.of(() -> this.paintingData);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return cap == SOURCE_PAINTING_CAPABILITY ? this.handler.cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT()
        {
            return SOURCE_PAINTING_CAPABILITY.getStorage().writeNBT(SOURCE_PAINTING_CAPABILITY, this.paintingData, null);
        }

        @Override
        public void deserializeNBT(INBT nbt)
        {
            SOURCE_PAINTING_CAPABILITY.getStorage().readNBT(SOURCE_PAINTING_CAPABILITY, this.paintingData, null, nbt);
        }
    }
}
