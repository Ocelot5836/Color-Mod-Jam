package io.github.ocelot.client.render.painting;

import com.google.common.base.Stopwatch;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.WorldPainter;
import io.github.ocelot.painting.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>Manages the textures of {@link Painting} being rendered with</p>
 *
 * @author Ocelot
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WorldPainter.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class WorldPaintingTextureCache
{
    /**
     * How long a painting texture should be saved for after not being rendered.
     */
    public static final long CACHE_TIME = 10000;
    public static final ResourceLocation MISSING = new ResourceLocation(WorldPainter.MOD_ID, "textures/painting/world_painting.png");
    public static final ResourceLocation BORDER = new ResourceLocation(WorldPainter.MOD_ID, "textures/painting/world_painting_border.png");

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, MutablePair<ResourceLocation, Long>> CACHE = new HashMap<>();

    @Nullable
    private static DynamicTexture getTexture(UUID id)
    {
        if (!CACHE.containsKey(id))
            return null;
        ResourceLocation location = CACHE.get(id).getLeft();
        Texture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
        if (!(texture instanceof DynamicTexture))
            return null;
        return (DynamicTexture) texture;
    }

    private static MutablePair<ResourceLocation, Long> fillTexture(Painting painting)
    {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        DynamicTexture texture = getTexture(painting.getId());
        if (texture == null || texture.getTextureData() == null)
        {
            deleteTexture(painting.getId());
            texture = new DynamicTexture(new NativeImage(Painting.SIZE, Painting.SIZE, true));
            CACHE.put(painting.getId(), new MutablePair<>(textureManager.getDynamicTextureLocation("world_painting", texture), System.currentTimeMillis()));
        }

        NativeImage image = Objects.requireNonNull(texture.getTextureData());
        for (int y = 0; y < Painting.SIZE; y++)
        {
            for (int x = 0; x < Painting.SIZE; x++)
            {
                int color = painting.getPixel(x, y);
                image.setPixelRGBA(x, y, 0xFF000000 | ((color & 0xff) << 16) | (((color >> 8) & 0xFF) << 8) | ((color >> 16) & 0xFF));
            }
        }
        texture.updateDynamicTexture();
        return CACHE.get(painting.getId());
    }

    private static void deleteTexture(UUID key)
    {
        if (CACHE.containsKey(key))
        {
            LOGGER.debug("Deleting painting '" + key + "'");
            Minecraft.getInstance().getTextureManager().deleteTexture(CACHE.remove(key).getLeft());
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event)
    {
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, MutablePair<ResourceLocation, Long>> entry : CACHE.entrySet())
        {
            if (now - entry.getValue().getRight() >= CACHE_TIME)
            {
                RenderSystem.recordRenderCall(() -> deleteTexture(entry.getKey()));
            }
        }
    }

    /**
     * Refills the texture for the specified painting.
     *
     * @param painting The painting to update
     */
    public static void updateTexture(Painting painting)
    {
        if (CACHE.containsKey(painting.getId()))
        {
            fillTexture(painting);
        }
    }

    /**
     * Generates a texture based on the provided painting pixels.
     *
     * @param painting The painting to get the texture for
     * @return The location of the texture for the painting
     */
    public static ResourceLocation getTexture(@Nullable Painting painting)
    {
        if (painting == null)
            return MISSING;
        UUID id = painting.getId();
        if (CACHE.containsKey(id))
        {
            CACHE.get(id).setRight(System.currentTimeMillis());
        }
        else
        {
            Stopwatch stopwatch = Stopwatch.createStarted();
            fillTexture(painting);
            LOGGER.debug("Generated texture for painting '{} took {}", painting.getId(), stopwatch);
        }
        return CACHE.get(id).getLeft();
    }
}
