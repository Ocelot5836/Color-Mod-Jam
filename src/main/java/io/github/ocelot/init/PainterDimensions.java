package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.dimension.PaintedBiome;
import io.github.ocelot.dimension.PaintedDimension;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.PlainFlowerBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Ocelot
 */
public class PainterDimensions
{
    public static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, WorldPainter.MOD_ID);
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, WorldPainter.MOD_ID);

    public static final RegistryObject<ModDimension> PAINTED_DIMENSION = DIMENSIONS.register("painted", () -> ModDimension.withFactory(PaintedDimension::new));
    public static final RegistryObject<PaintedBiome> PAINTED_BIOME = BIOMES.register("painted", PaintedBiome::new);
}
