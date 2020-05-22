package io.github.ocelot.painting;

import io.github.ocelot.WorldPainter;

import java.util.Locale;
import java.util.UUID;

/**
 * <p>Special painting types that do specific things.</p>
 *
 * @author Ocelot
 */
public enum FixedPaintingType
{
    PLAID(UUID.fromString("56208243-31d3-4438-a9d1-20b10bda1314"), getPlaid(), true);

    private final Painting painting;
    private final boolean teleportation;

    FixedPaintingType(UUID id, int[] pixels, boolean teleportation)
    {
        this.painting = new Painting(pixels, "Ocelot5836", id, true);
        this.teleportation = teleportation;
    }

    /**
     * @return The painting associated with this type
     */
    public Painting getPainting()
    {
        return painting;
    }

    /**
     * @return Whether or not this painting is a teleportation painting
     */
    public boolean isTeleportation()
    {
        return teleportation;
    }

    /**
     * @return The unlocalized name of this special painting type
     */
    public String getTranslationKey()
    {
        return "item." + WorldPainter.MOD_ID + ".fixed_painting." + this.name().toLowerCase(Locale.ROOT);
    }

    private static int[] getPlaid()
    {
        int[] pixels = new int[Painting.SIZE * Painting.SIZE];
        for (int y = 0; y < Painting.SIZE; y++)
        {
            for (int x = 0; x < Painting.SIZE; x++)
            {
                pixels[x + y * Painting.SIZE] = (Math.abs(x * 128) & 0xff) << 16 | Math.abs(y * 128) & 0xff;
            }
        }
        return pixels;
    }

    /**
     * Fetches the specified painting.
     *
     * @param id The id of the painting to get
     * @return The painting of that type or {@code PLAID} if the id is invalid
     */
    public static Painting get(UUID id)
    {
        return getType(id).getPainting();
    }

    /**
     * Fetches the specified painting type.
     *
     * @param id The id of the painting type to get
     * @return The painting type of that type or {@code PLAID} if the id is invalid
     */
    public static FixedPaintingType getType(UUID id)
    {
        for (FixedPaintingType paintingType : values())
            if (paintingType.getPainting().getId().equals(id))
                return paintingType;
        return PLAID;
    }

    /**
     * Checks to see if the specified painting is a fixed painting type.
     *
     * @param id The id of the painting to check
     * @return Whether or not the painting is a fixed painting
     */
    public static boolean isFixed(UUID id)
    {
        for (FixedPaintingType paintingType : values())
            if (paintingType.getPainting().getId().equals(id))
                return true;
        return false;
    }
}
