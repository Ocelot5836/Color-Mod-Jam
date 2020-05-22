package io.github.ocelot.dimension.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

/**
 * @author Ocelot
 */
public class PlaidBiome extends Biome
{
    public PlaidBiome()
    {
        super(new Builder()
                .surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG)
                .precipitation(RainType.NONE)
                .category(Category.NONE)
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

//    @Override
//    public int getFoliageColor(double posX, double posZ)
//    {
//        return this.getGrassColor(posX, posZ);
//    }
}
