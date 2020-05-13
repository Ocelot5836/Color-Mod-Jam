package io.github.ocelot.dimension;

import com.mojang.datafixers.Dynamic;
import io.github.ocelot.init.PainterDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class PaintedDimension extends Dimension
{
    public PaintedDimension(World world, DimensionType dimensionType)
    {
        super(world, dimensionType, 0.8f);
    }

    private void addLayer(ListNBT layers, Block block, int height)
    {
        CompoundNBT layer = new CompoundNBT();
        layer.putString("block", String.valueOf(block.getRegistryName()));
        layer.putByte("height", (byte) height);
        layers.add(layer);
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator()
    {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("biome", String.valueOf(PainterDimensions.PAINTED_BIOME.getId()));

        ListNBT layers = new ListNBT();
        {
            this.addLayer(layers, Blocks.BEDROCK, 1);
            this.addLayer(layers, Blocks.STONE, 54);
            this.addLayer(layers, Blocks.DIRT, 7);
            this.addLayer(layers, Blocks.GRASS_BLOCK, 1);
        }
        nbt.put("layers", layers);

        // Adds void platform TODO remove
//        CompoundNBT structures = new CompoundNBT();
//        structures.put("decoration", new CompoundNBT());
//        nbt.put("structures", structures);

        FlatGenerationSettings generationSettings = FlatGenerationSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
        SingleBiomeProviderSettings settings = BiomeProviderType.FIXED.createSettings(this.world.getWorldInfo()).setBiome(generationSettings.getBiome());
        return ChunkGeneratorType.FLAT.create(this.world, BiomeProviderType.FIXED.create(settings), generationSettings);
    }

    @Nullable
    public BlockPos findSpawn(ChunkPos chunkPos, boolean checkValid)
    {
        return null;
    }

    @Nullable
    public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
    {
        return null;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        return 0.5F;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks)
    {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    @Override
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }
}
