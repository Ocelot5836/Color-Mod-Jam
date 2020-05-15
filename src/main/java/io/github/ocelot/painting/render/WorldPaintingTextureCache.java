package io.github.ocelot.painting.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.WorldPainter;
import io.github.ocelot.painting.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
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
    private static final Map<Integer, MutablePair<ResourceLocation, Long>> CACHE = new HashMap<>();

    private static MutablePair<ResourceLocation, Long> generateTexture(Painting painting)
    {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(BORDER);
        NativeImage image = new NativeImage(Painting.SIZE, Painting.SIZE, true);
        for (int y = 0; y < Painting.SIZE; y++)
            for (int x = 0; x < Painting.SIZE; x++)
                image.setPixelRGBA(x, y, painting.getPixel(x, y));
        ResourceLocation location = textureManager.getDynamicTextureLocation("world_painting", new DynamicTexture(image));
        return new MutablePair<>(location, System.currentTimeMillis());
    }

    private static void deleteTexture(int key)
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
        for (Map.Entry<Integer, MutablePair<ResourceLocation, Long>> entry : CACHE.entrySet())
        {
            if (now - entry.getValue().getRight() >= CACHE_TIME)
            {
                RenderSystem.recordRenderCall(() -> deleteTexture(entry.getKey()));
            }
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
        int id = Objects.hash(painting, painting.hasBorder());
        if (CACHE.containsKey(id))
            CACHE.get(id).setRight(System.currentTimeMillis());
        return CACHE.computeIfAbsent(id, key -> generateTexture(painting)).getLeft();
    }
}
