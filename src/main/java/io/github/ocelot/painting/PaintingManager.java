package io.github.ocelot.painting;

import io.github.ocelot.WorldPainter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

/**
 * <p>Manages all painting dimensions in the world.</p>
 *
 * @author Ocelot
 */
public interface PaintingManager
{
    /**
     * The name of the server side saved data.
     */
    String DATA_NAME = WorldPainter.MOD_ID + "_PaintingManager";

    /**
     * The distance in blocks between each painting realm.
     */
    int REALM_DISTANCE = 2048;

    /**
     * Initializes the realm of the specified painting.
     *
     * @param id The id of the painting to initialize
     * @return Whether or not the realm exists to travel to
     */
    boolean initializeRealm(UUID id);

    /**
     * Adds the specified painting to the cache.
     *
     * @param painting The painting to add
     */
    void addPainting(Painting painting);

    /**
     * Checks to see if the painting with the specified id exists.
     *
     * @param id The id to check
     * @return Whether or not that painting has been created
     */
    boolean hasPainting(UUID id);

    /**
     * Fetches the specified painting from cache if it exists.
     *
     * @param id The id of the painting
     * @return The painting with that id or null if there is no painting with that id
     */
    @Nullable
    Painting getPainting(UUID id);

    /**
     * Fetches the spawn position of the specified painting if it exists.
     *
     * @param id The id of the painting
     * @return The dimension start position of the painting with that id or null if there is no painting with that id
     */
    @Nullable
    default BlockPos getPaintingStartPosition(UUID id)
    {
        if (!this.hasPainting(id))
            return null;
        int realmId = Arrays.hashCode(Objects.requireNonNull(this.getPainting(id)).getPixels());
        if (!this.getAllPaintingRealms().containsKey(realmId))
            return null;
        return new BlockPos(this.getAllPaintingRealms().getOrDefault(realmId, 0) * REALM_DISTANCE, 63, 0);
    }

    /**
     * Fetches the id of the painting that should exist at the specified chunk position.
     *
     * @param pos The position of the chunk
     * @return The id of the painting at that position or null if there is no painting realm there
     */
    @Nullable
    UUID getRealmPainting(ChunkPos pos);

    /**
     * Calculates the offset of the painting based on the specified position.
     *
     * @param pos The position of the chunk
     * @return The packed x and z offset of the painting realm
     */
    byte getRealmOffset(ChunkPos pos);

    /**
     * @return A collection of every painting stored
     */
    Collection<Painting> getAllPaintings();

    /**
     * @return A collection of every painting realm stored
     */
    Map<Integer, Integer> getAllPaintingRealms();

    /**
     * Checks the world for the painting manager for the world side.
     *
     * @param world The world to get the painting manager from
     * @return The manager on the correct side for the specified world
     */
    static PaintingManager get(IWorld world)
    {
        if (world.isRemote())
            return ClientPaintingManager.INSTANCE;
        if (!(world instanceof ServerWorld))
            throw new IllegalStateException("Server side world is not an instance of ServerWorld?");
        return (((ServerWorld) world).getServer().getWorld(DimensionType.OVERWORLD).getSavedData()).getOrCreate(PaintingManagerSavedData::new, DATA_NAME);
    }
}
