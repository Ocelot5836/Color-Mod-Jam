package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.block.EaselBlock;
import io.github.ocelot.block.PaintBucketBlock;
import io.github.ocelot.item.PaintBucketItem;
import io.github.ocelot.tileentity.EaselTileEntity;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
public class PainterBlocks
{
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, WorldPainter.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTTIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, WorldPainter.MOD_ID);

    public static final RegistryObject<LeavesBlock> PAINTED_LEAVES = register("painted_leaves", () -> new LeavesBlock(Block.Properties.from(Blocks.OAK_LEAVES)), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<PaintBucketBlock> PAINT_BUCKET = register("paint_bucket", PaintBucketBlock::new, object -> new PaintBucketItem(object.get(), new Item.Properties().group(WorldPainter.TAB)));
    public static final RegistryObject<EaselBlock> OAK_EASEL = register("oak_easel", () -> new EaselBlock(Block.Properties.from(Blocks.OAK_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> SPRUCE_EASEL = register("spruce_easel", () -> new EaselBlock(Block.Properties.from(Blocks.SPRUCE_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> BIRCH_EASEL = register("birch_easel", () -> new EaselBlock(Block.Properties.from(Blocks.BIRCH_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> JUNGLE_EASEL = register("jungle_easel", () -> new EaselBlock(Block.Properties.from(Blocks.JUNGLE_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> ACACIA_EASEL = register("acacia_easel", () -> new EaselBlock(Block.Properties.from(Blocks.ACACIA_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> DARK_OAK_EASEL = register("dark_oak_easel", () -> new EaselBlock(Block.Properties.from(Blocks.DARK_OAK_LOG).notSolid()), new Item.Properties().group(WorldPainter.TAB));

    public static final RegistryObject<TileEntityType<PaintBucketTileEntity>> PAINT_BUCKET_TE = TILE_ENTTIES.register("paint_bucket", () -> TileEntityType.Builder.create(PaintBucketTileEntity::new, PAINT_BUCKET.get()).build(null));
    public static final RegistryObject<TileEntityType<EaselTileEntity>> EASEL_TE = TILE_ENTTIES.register("easel", () -> TileEntityType.Builder.create(EaselTileEntity::new, OAK_EASEL.get(), SPRUCE_EASEL.get(), BIRCH_EASEL.get(), JUNGLE_EASEL.get(), ACACIA_EASEL.get(), DARK_OAK_EASEL.get()).build(null));

    /**
     * Registers a new block and {@link BlockItem} under the specified registry name.
     *
     * @param registryName   The register name of the block
     * @param blockSupplier  The block generator
     * @param itemProperties The properties of the item to generate
     * @param <T>            The type of block registered
     * @return The registry object of the registered block
     */
    private static <T extends Block> RegistryObject<T> register(String registryName, Supplier<T> blockSupplier, Item.Properties itemProperties)
    {
        return register(registryName, blockSupplier, object -> new BlockItem(object.get(), itemProperties));
    }

    /**
     * Registers a new block and item under the specified registry name.
     *
     * @param registryName  The register name of the block
     * @param blockSupplier The block generator
     * @param itemSupplier  The item generator
     * @param <T>           The type of block registered
     * @return The registry object of the registered block
     */
    private static <T extends Block> RegistryObject<T> register(String registryName, Supplier<T> blockSupplier, Function<RegistryObject<T>, Item> itemSupplier)
    {
        RegistryObject<T> object = register(registryName, blockSupplier);
        PainterItems.ITEMS.register(registryName, () -> itemSupplier.apply(object));
        return object;
    }

    /**
     * Registers a new block under the specified registry name.
     *
     * @param registryName  The register name of the block
     * @param blockSupplier The block generator
     * @param <T>           The type of block registered
     * @return The registry object of the registered block
     */
    private static <T extends Block> RegistryObject<T> register(String registryName, Supplier<T> blockSupplier)
    {
        Validate.notNull(registryName);
        return BLOCKS.register(registryName, blockSupplier);
    }
}
