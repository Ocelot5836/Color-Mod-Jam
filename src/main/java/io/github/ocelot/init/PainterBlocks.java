package io.github.ocelot.init;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.block.EaselBlock;
import io.github.ocelot.block.PaintBucketBlock;
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
public class PainterBlocks
{
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, WorldPainter.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTTIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, WorldPainter.MOD_ID);

    public static final RegistryObject<LeavesBlock> PAINTED_LEAVES = register("painted_leaves", () -> new LeavesBlock(Block.Properties.from(Blocks.OAK_LEAVES)), new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<PaintBucketBlock> PAINT_BUCKET = register("paint_bucket", PaintBucketBlock::new, new Item.Properties().group(WorldPainter.TAB));
    public static final RegistryObject<EaselBlock> OAK_EASEL = register("oak_easel", () -> new EaselBlock(Block.Properties.from(Blocks.OAK_LOG)), new Item.Properties().group(WorldPainter.TAB));

    public static final RegistryObject<TileEntityType<PaintBucketTileEntity>> PAINT_BUCKET_TE = TILE_ENTTIES.register("paint_bucket", () -> TileEntityType.Builder.create(PaintBucketTileEntity::new, PAINT_BUCKET.get()).build(null));

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
        PainterItems.REGISTRY.register(registryName, () -> itemSupplier.apply(object));
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
