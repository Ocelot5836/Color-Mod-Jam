package io.github.ocelot.painting;

import io.github.ocelot.WorldPainter;
import net.minecraft.nbt.CompoundNBT;

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
    private final UUID id;
    private boolean hasBorder;

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
            Arrays.fill(this.pixels, 0xFFFFFF);
        }
        this.id = id;
        this.hasBorder = hasBorder;
    }

    public Painting()
    {
        this.pixels = new int[SIZE * SIZE];
        Arrays.fill(this.pixels, 0xFFFFFF);
        this.id = UUID.randomUUID();
        this.hasBorder = false;
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
            Arrays.fill(this.pixels, 0xFFFFFF);
        }
        this.id = nbt.hasUniqueId("id") ? nbt.getUniqueId("id") : UUID.randomUUID();
        this.hasBorder = nbt.getBoolean("hasBorder");
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
        if (width <= 0 || height <= 0 || !checkBounds(x, y) || !checkBounds(x + width, y + height))
            return;
        for (int xp = 0; xp < width; xp++)
            for (int yp = 0; yp < height; yp++)
                this.pixels[(x + xp) + (y + yp) * SIZE] = color;
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
     * Checks the image for the pixel at the specified position or <code>0xFFFFFF</code> if the position is out of bounds.
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
     * Sets whether or not this painting shows a border.
     *
     * @param hasBorder Whether or not a border should render
     */
    public void setHasBorder(boolean hasBorder)
    {
        this.hasBorder = hasBorder;
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
            WorldPainter.LOGGER.warn("Read painting pixels with unexpected size ({}) when {} was expected.", pixels.length, SIZE * SIZE);
            return false;
        }
        return true;
    }

    private static boolean checkBounds(int x, int y)
    {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
}
