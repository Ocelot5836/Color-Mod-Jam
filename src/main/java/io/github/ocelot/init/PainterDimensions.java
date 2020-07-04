package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.dimension.PaintedDimension;
import io.github.ocelot.dimension.PlaidDimension;
import io.github.ocelot.dimension.biome.PaintedBiome;
import io.github.ocelot.dimension.biome.PlaidBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * @author Ocelot
 */
public class PainterDimensions
{
    public static final DeferredRegister<ModDimension> DIMENSIONS = DeferredRegister.create(ForgeRegistries.MOD_DIMENSIONS, WorldPainter.MOD_ID);
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, WorldPainter.MOD_ID);

    public static final RegistryObject<ModDimension> PAINTED_DIMENSION = DIMENSIONS.register("painted", () -> ModDimension.withFactory(PaintedDimension::new));
    public static final RegistryObject<ModDimension> PLAID_DIMENSION = DIMENSIONS.register("plaid", () -> ModDimension.withFactory(PlaidDimension::new));
    public static final RegistryObject<PaintedBiome> PAINTED_BIOME = BIOMES.register("painted", PaintedBiome::new);
    public static final RegistryObject<PlaidBiome> PLAID_BIOME = BIOMES.register("plaid", PlaidBiome::new);

    public static DimensionType getDimensionType(ModDimension dimension)
    {
        return DimensionManager.registerOrGetDimension(Objects.requireNonNull(dimension.getRegistryName()), dimension, null, true);
    }
}
