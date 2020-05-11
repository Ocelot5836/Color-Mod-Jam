package io.github.ocelot.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
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
                .surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG)
                .precipitation(Biome.RainType.NONE)
                .category(Biome.Category.NONE)
                .depth(0.1F)
                .scale(0.2F)
                .temperature(0.5F)
                .downfall(0.5F)
                .waterColor(4159204)
                .waterFogColor(329011)
                .parent(null));
        // Adds void platform TODO remove
        this.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.VOID_START_PLATFORM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }

    @Override
    public int getGrassColor(double posX, double posZ)
    {
        return (((int) Math.abs(posX * 8)) & 0xff) << 16 | ((int) Math.abs(posZ * 8)) & 0xff;
    }

    public int getFoliageColor(double posX, double posZ)
    {
        return getGrassColor(posX, posZ);
    }
}
