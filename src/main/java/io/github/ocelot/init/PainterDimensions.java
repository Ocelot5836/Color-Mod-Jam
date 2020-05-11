package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.dimension.PaintedBiome;
import io.github.ocelot.dimension.PaintedDimension;
import io.github.ocelot.dimension.PlaidBiome;
import io.github.ocelot.dimension.PlaidDimension;
import net.minecraft.world.biome.Biome;
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
    public static final RegistryObject<ModDimension> PLAID_DIMENSION = DIMENSIONS.register("plaid", () -> ModDimension.withFactory(PlaidDimension::new));
    public static final RegistryObject<PaintedBiome> PAINTED_BIOME = BIOMES.register("painted", PaintedBiome::new);
    public static final RegistryObject<PlaidBiome> PLAID_BIOME = BIOMES.register("plaid", PlaidBiome::new);
}
