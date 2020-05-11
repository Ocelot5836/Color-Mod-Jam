package io.github.ocelot.dimension;

import com.mojang.datafixers.Dynamic;
import io.github.ocelot.init.PainterDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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
import java.util.Random;

public class PlaidDimension extends Dimension
{
    public PlaidDimension(World world, DimensionType dimensionType)
    {
        super(world, dimensionType, 0.0f);
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator()
    {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("biome", String.valueOf(PainterDimensions.PLAID_BIOME.getId()));

        ListNBT layers = new ListNBT();
        {
            CompoundNBT layer = new CompoundNBT();
            layer.putString("block", String.valueOf(Blocks.GRASS_BLOCK.getRegistryName()));
            layer.putByte("height", (byte) 1);
            layers.add(layer);
        }
        nbt.put("layers", layers);

        FlatGenerationSettings generationSettings = FlatGenerationSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
        SingleBiomeProviderSettings settings = BiomeProviderType.FIXED.createSettings(this.world.getWorldInfo()).setBiome(PainterDimensions.PAINTED_BIOME.get());
        return ChunkGeneratorType.FLAT.create(this.world, BiomeProviderType.FIXED.create(settings), generationSettings);
    }

    @Nullable
    @Override
    public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid)
    {
        Random random = new Random(this.world.getSeed());
        BlockPos blockpos = new BlockPos(chunkPosIn.getXStart() + random.nextInt(15), 0, chunkPosIn.getZEnd() + random.nextInt(15));
        return this.world.getGroundAboveSeaLevel(blockpos).getMaterial().blocksMovement() ? blockpos : null;
    }

    @Nullable
    @Override
    public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
    {
        return this.findSpawn(new ChunkPos(posX >> 4, posZ >> 4), checkValid);
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        double d0 = MathHelper.frac((double) worldTime / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks)
    {
        float f = MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        float f1 = 0.7529412F;
        float f2 = 0.84705883F;
        float f3 = 1.0F;
        f1 = f1 * (f * 0.94F + 0.06F);
        f2 = f2 * (f * 0.94F + 0.06F);
        f3 = f3 * (f * 0.91F + 0.09F);
        return new Vec3d(f1, f2, f3);
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
