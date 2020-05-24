package io.github.ocelot.datagen;

import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.init.PainterItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.functions.CopyNbt;

/**
 * @author Ocelot
 */
public class PainterLootTableProvider extends BaseLootTableProvider
{
    public PainterLootTableProvider(DataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void addTables()
    {
        this.lootTables.put(PainterBlocks.OAK_EASEL.get(), this.getEaselLootTable(PainterBlocks.OAK_EASEL.get()));
        this.lootTables.put(PainterBlocks.SPRUCE_EASEL.get(), this.getEaselLootTable(PainterBlocks.SPRUCE_EASEL.get()));
        this.lootTables.put(PainterBlocks.BIRCH_EASEL.get(), this.getEaselLootTable(PainterBlocks.BIRCH_EASEL.get()));
        this.lootTables.put(PainterBlocks.JUNGLE_EASEL.get(), this.getEaselLootTable(PainterBlocks.JUNGLE_EASEL.get()));
        this.lootTables.put(PainterBlocks.ACACIA_EASEL.get(), this.getEaselLootTable(PainterBlocks.ACACIA_EASEL.get()));
        this.lootTables.put(PainterBlocks.DARK_OAK_EASEL.get(), this.getEaselLootTable(PainterBlocks.DARK_OAK_EASEL.get()));
        this.lootTables.put(PainterBlocks.PAINT_BUCKET.get(), this.getPaintBucketLootTable());
        this.entityLootTables.put(PainterEntities.BOB_ROSS.get(), this.getBobRossLootTable());
    }

    private LootTable.Builder getEaselLootTable(Block block)
    {
        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                );
        return LootTable.builder().addLootPool(builder);
    }

    private LootTable.Builder getPaintBucketLootTable()
    {
        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(PainterBlocks.PAINT_BUCKET.get())
                        .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).addOperation("color", "display.color", CopyNbt.Action.REPLACE))
                );
        return LootTable.builder().addLootPool(builder);
    }

    private LootTable.Builder getBobRossLootTable()
    {
        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(PainterItems.AFRO.get()));
        return LootTable.builder().addLootPool(builder);
    }
}
