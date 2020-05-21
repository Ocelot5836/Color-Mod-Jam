package io.github.ocelot.dimension;

import io.github.ocelot.painting.Painting;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class PaintedChunkGenerator extends ChunkGenerator<FlatGenerationSettings>
{
    public PaintedChunkGenerator(IWorld world, BiomeProvider biomeProvider, FlatGenerationSettings generationSettings)
    {
        super(world, biomeProvider, generationSettings);
    }

    private void generateBlocks(int[] pixels, IChunk chunk, int xOffset, int zOffset)
    {
        // TODO generate in biome when chunks generate
        int[] heights = new int[Painting.SIZE];
        Arrays.fill(heights, 0);
        for (int x = 0; x < Painting.SIZE; x++)
        {
            for (int y = 0; y < Painting.SIZE; y++)
            {
                if (pixels[x + y * Painting.SIZE] != DyeColor.WHITE.getColorValue())
                {
                    heights[x] = Painting.SIZE - y;
                    break;
                }
            }
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int z = 0; z < 16; z++)
        {
            for (int x = 0; x < 16; x++)
            {
                int height = 63 + (heights[x + xOffset * 16] + heights[z + zOffset * 16]);
                for (int y = 0; y < height; y++)
                {
                    BlockState state = y == 0 ? Blocks.BEDROCK.getDefaultState() : y < height - 4 ? Blocks.STONE.getDefaultState() : y < height - 1 ? Blocks.DIRT.getDefaultState() : Blocks.GRASS_BLOCK.getDefaultState();
                    chunk.setBlockState(mutable.setPos(x, y, z), state, false);
                    heightmap.update(x, y, z, state);
                    heightmap1.update(x, y, z, state);
                }
            }
        }
    }

    @Override
    public void generateSurface(WorldGenRegion region, IChunk chunk)
    {
    }

    @Override
    public int getGroundHeight()
    {
        IChunk ichunk = this.world.getChunk(0, 0);
        return ichunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 8, 8);
    }

    @Override
    protected Biome getBiome(BiomeManager biomeManager, BlockPos pos)
    {
        return this.settings.getBiome();
    }

    @Override
    public void makeBase(IWorld world, IChunk chunk)
    {
        PaintingManager paintingManager = PaintingManager.get(this.world);
        UUID realmPainting = paintingManager.getRealmPainting(chunk.getPos());
        if (realmPainting == null || !paintingManager.hasPainting(realmPainting))
            return;
        int realmOffset = paintingManager.getRealmOffset(chunk.getPos());
        this.generateBlocks(Objects.requireNonNull(paintingManager.getPainting(realmPainting)).getPixels(), chunk, realmOffset & 1, (realmOffset >> 1) & 1);
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType)
    {
        BlockState[] ablockstate = this.settings.getStates();

        for (int i = ablockstate.length - 1; i >= 0; --i)
        {
            BlockState blockstate = ablockstate[i];
            if (blockstate != null && heightmapType.getHeightLimitPredicate().test(blockstate))
            {
                return i + 1;
            }
        }

        return 0;
    }

    public boolean hasStructure(Biome biome, Structure<? extends IFeatureConfig> structure)
    {
        return this.settings.getBiome().hasStructure(structure);
    }

    @Nullable
    public <C extends IFeatureConfig> C getStructureConfig(Biome biome, Structure<C> structure)
    {
        return this.settings.getBiome().getStructureConfig(structure);
    }

    @Nullable
    public BlockPos findNearestStructure(World world, String name, BlockPos pos, int radius, boolean skipExistingChunks)
    {
        return !this.settings.getWorldFeatures().keySet().contains(name.toLowerCase(Locale.ROOT)) ? null : super.findNearestStructure(world, name, pos, radius, skipExistingChunks);
    }
}