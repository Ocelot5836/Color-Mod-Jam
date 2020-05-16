package io.github.ocelot.painting;

import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class Painting
{
    public static final int SIZE = 32;
    public static final Painting PLAD_PAINTING;

    private static final Logger LOGGER = LogManager.getLogger();

    static
    {
        int[] pixels = new int[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++)
        {
            for (int x = 0; x < SIZE; x++)
            {
                pixels[x + y * SIZE] = (Math.abs(x * 128) & 0xff) << 16 | Math.abs(y * 128) & 0xff;
            }
        }
        PLAD_PAINTING = new Painting(pixels, UUID.fromString("56208243-31d3-4438-a9d1-20b10bda1314"), true);
    }

    private final int[] pixels;
    private UUID id;
    private boolean hasBorder;
    private int index;
    private PaintingManagerSavedData paintingManager;

    private Painting(int[] pixels, UUID id, boolean hasBorder)
    {
        if (validateSize(pixels))
        {
            this.pixels = new int[pixels.length];
            System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
        }
        else
        {
            this.pixels = new int[SIZE * SIZE];
            Arrays.fill(this.pixels, DyeColor.WHITE.getColorValue());
        }
        this.id = id;
        this.hasBorder = hasBorder;
        this.index = -1;
    }

    public Painting()
    {
        this.pixels = new int[SIZE * SIZE];
        Arrays.fill(this.pixels, DyeColor.WHITE.getColorValue());
        this.id = UUID.randomUUID();
        this.hasBorder = false;
        this.index = -1;
    }

    public Painting(Painting parent)
    {
        this.pixels = new int[SIZE * SIZE];
        System.arraycopy(parent.pixels, 0, this.pixels, 0, parent.pixels.length);
        this.id = UUID.randomUUID();
        this.hasBorder = parent.hasBorder;
        this.index = -1;
    }

    public Painting(CompoundNBT nbt)
    {
        int[] pixels = nbt.getIntArray("pixels");
        if (validateSize(pixels))
        {
            this.pixels = new int[pixels.length];
            System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
        }
        else
        {
            this.pixels = new int[SIZE * SIZE];
            Arrays.fill(this.pixels, DyeColor.WHITE.getColorValue());
        }
        this.id = nbt.hasUniqueId("id") ? nbt.getUniqueId("id") : UUID.randomUUID();
        this.hasBorder = nbt.getBoolean("hasBorder");
        this.index = nbt.getInt("index");
    }

    protected void setPaintingManager(PaintingManagerSavedData paintingManager)
    {
        this.paintingManager = paintingManager;
    }

    /**
     * Fills in the specified area with the provided pixel color.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param width  The width of the area
     * @param height The height of the area
     * @param color  The color to fill with
     */
    public void fill(int x, int y, int width, int height, int color)
    {
        if (x + width < 0 || x >= SIZE || y + height < 0 || y >= SIZE)
            return;
        boolean boundsCheck = width <= 0 || height <= 0 || !checkBounds(x, y) || !checkBounds(x + width, y + height);
        for (int xp = 0; xp < width; xp++)
            for (int yp = 0; yp < height; yp++)
                if (!boundsCheck || checkBounds(x + xp, y + yp))
                    this.pixels[(x + xp) + (y + yp) * SIZE] = color;
        if (this.paintingManager != null)
            this.paintingManager.sync(this);
    }

    /**
     * Sets the pixel at the specified position to the specified color.
     *
     * @param x     The x position to set
     * @param y     The y position to set
     * @param color The color to set at that location
     */
    public void setPixel(int x, int y, int color)
    {
        if (!checkBounds(x, y))
            return;
        this.pixels[x + y * SIZE] = color;
        if (this.paintingManager != null)
            this.paintingManager.sync(this);
    }

    /**
     * Randomizes the id of this painting.
     */
    public void shuffleId()
    {
        if (this != PLAD_PAINTING)
            this.id = UUID.randomUUID();
        if (this.paintingManager != null)
            this.paintingManager.sync(this);
    }

    /**
     * Checks the image for the pixel at the specified position or <code>0x000000</code> if the position is out of bounds.
     *
     * @param x The x position to fetch
     * @param y The y position to fetch
     * @return The color of the pixel at that position
     */
    public int getPixel(int x, int y)
    {
        return checkBounds(x, y) ? this.pixels[x + y * SIZE] : 0;
    }

    /**
     * @return The entire internal pixels array
     */
    public int[] getPixels()
    {
        return pixels;
    }

    /**
     * @return The id of this painting
     */
    public UUID getId()
    {
        return id;
    }

    /**
     * @return Whether or not a border should overlay on this painting
     */
    public boolean hasBorder()
    {
        return hasBorder;
    }

    /**
     * @return Whether or not a dimension has been generated for this painting
     */
    public boolean hasDimension()
    {
        return this.index != -1;
    }

    /**
     * @return The dimension index of this painting
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Sets whether or not this painting shows a border.
     *
     * @param hasBorder Whether or not a border should render
     */
    public void setHasBorder(boolean hasBorder)
    {
        this.hasBorder = hasBorder;
        if (this.paintingManager != null)
            this.paintingManager.sync(this);
    }

    /**
     * Sets the dimension of this painting.
     *
     * @param index The new dimension index
     */
    public void setIndex(int index)
    {
        this.index = index;
        if (this.paintingManager != null)
            this.paintingManager.sync(this);
    }

    /**
     * Serializes the image into a compound tag.
     *
     * @return The tag full of data
     */
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray("pixels", this.pixels);
        nbt.putUniqueId("id", this.id);
        nbt.putBoolean("hasBorder", this.hasBorder);
        nbt.putInt("index", this.index);
        return nbt;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Painting)) return false;
        Painting painting = (Painting) o;
        return this.id.equals(painting.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    private static boolean validateSize(int[] pixels)
    {
        if (pixels.length != SIZE * SIZE)
        {
            LOGGER.warn("Read painting pixels with unexpected size ({}) when {} was expected.", pixels.length, SIZE * SIZE);
            return false;
        }
        return true;
    }

    private static boolean checkBounds(int x, int y)
    {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
}
