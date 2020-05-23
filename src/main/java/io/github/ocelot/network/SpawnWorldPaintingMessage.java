package io.github.ocelot.network;

import io.github.ocelot.entity.WorldPaintingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class SpawnWorldPaintingMessage
{
    private final int entityID;
    private final UUID uniqueId;
    private final BlockPos position;
    private final Direction facing;
    private final boolean teleportation;
    private final UUID paintingId;

    private SpawnWorldPaintingMessage(int entityID, UUID uniqueId, BlockPos position, Direction facing, boolean teleportation, UUID paintingId)
    {
        this.entityID = entityID;
        this.uniqueId = uniqueId;
        this.position = position;
        this.facing = facing;
        this.teleportation = teleportation;
        this.paintingId = paintingId;
    }

    public SpawnWorldPaintingMessage(WorldPaintingEntity entity)
    {
        this(entity.getEntityId(), entity.getUniqueID(), entity.getHangingPosition(), entity.getHorizontalFacing(), entity.isTeleportation(), entity.getPaintingId());
    }

    public static void encode(SpawnWorldPaintingMessage msg, PacketBuffer buf)
    {
        buf.writeVarInt(msg.entityID);
        buf.writeUniqueId(msg.uniqueId);
        buf.writeBlockPos(msg.position);
        buf.writeByte(msg.facing.getHorizontalIndex());
        buf.writeBoolean(msg.teleportation);
        buf.writeBoolean(msg.paintingId != null);
        if (msg.paintingId != null)
            buf.writeUniqueId(msg.paintingId);
    }

    public static SpawnWorldPaintingMessage decode(PacketBuffer buf)
    {
        return new SpawnWorldPaintingMessage(buf.readVarInt(), buf.readUniqueId(), buf.readBlockPos(), Direction.byHorizontalIndex(buf.readUnsignedByte()), buf.readBoolean(), buf.readBoolean() ? buf.readUniqueId() : null);
    }

    @OnlyIn(Dist.CLIENT)
    public int getEntityID()
    {
        return entityID;
    }

    @OnlyIn(Dist.CLIENT)
    public UUID getUniqueId()
    {
        return uniqueId;
    }

    @OnlyIn(Dist.CLIENT)
    public BlockPos getPosition()
    {
        return position;
    }

    @OnlyIn(Dist.CLIENT)
    public Direction getFacing()
    {
        return facing;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isTeleportation()
    {
        return teleportation;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public UUID getPaintingId()
    {
        return paintingId;
    }
}
