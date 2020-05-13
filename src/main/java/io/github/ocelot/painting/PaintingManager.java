package io.github.ocelot.painting;

import io.github.ocelot.WorldPainter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

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
     * Adds the specified painting to the cache.
     *
     * @param painting The painting to add
     * @throws IllegalStateException If another painting with the same id exists
     */
    void addPainting(Painting painting) throws IllegalStateException;

    /**
     * Removes the painting with the specified id.
     *
     * @param id The id of the painting to remove
     */
    void removePainting(UUID id);

    /**
     * Checks to see if the painting exists.
     *
     * @param painting The painting to check
     * @return Whether or not that painting has been created
     */
    default boolean hasPainting(@Nullable Painting painting)
    {
        return painting != null && this.hasPainting(painting.getId());
    }

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
     * @return A collection of every painting stored
     */
    Collection<Painting> getAllPaintings();

    /**
     * Calculates the starting position of the specified dimension.
     *
     * @param dimension The dimension to get
     * @return The position the specified dimension starts
     */
    static BlockPos getDimensionPos(int dimension)
    {
        return new BlockPos(dimension * 500, 63, 0);
    }

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
        PaintingManagerSavedData data = (((ServerWorld) world).getServer().getWorld(DimensionType.OVERWORLD).getSavedData()).getOrCreate(PaintingManagerSavedData::new, DATA_NAME);
        data.setWorld((ServerWorld) world);
        return data;
    }
}
