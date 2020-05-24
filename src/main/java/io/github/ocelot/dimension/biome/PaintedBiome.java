package io.github.ocelot.dimension.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

/**
 * @author Ocelot
 */
public class PaintedBiome extends Biome
{
    public PaintedBiome()
    {
        super(new Biome.Builder()
                .surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG)
                .precipitation(Biome.RainType.NONE)
                .category(Biome.Category.NONE)
                .depth(0.1F)
                .scale(0.2F)
                .temperature(0.5F)
                .downfall(0.5F)
                .waterColor(4159204)
                .waterFogColor(329011)
                .parent(null));
    }
}
