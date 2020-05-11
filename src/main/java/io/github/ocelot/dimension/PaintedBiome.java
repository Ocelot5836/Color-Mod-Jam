package io.github.ocelot.dimension;

import io.github.ocelot.init.PainterDimensions;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Placement;
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

    @Override
    public int getGrassColor(double posX, double posZ)
    {
        return (((int) Math.abs(posX * 128)) & 0xff) << 16 | ((int) Math.abs(posZ * 128)) & 0xff;
    }

    public int getFoliageColor(double posX, double posZ)
    {
        return getGrassColor(posX, posZ);
    }
}
