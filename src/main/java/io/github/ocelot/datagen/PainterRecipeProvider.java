package io.github.ocelot.datagen;

import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * @author Ocelot
 */
public class PainterRecipeProvider extends RecipeProvider
{
    public PainterRecipeProvider(DataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shapedRecipe(PainterBlocks.PAINT_BUCKET.get(), 1)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('N', Tags.Items.NUGGETS_IRON)
                .patternLine(" I ")
                .patternLine("N N")
                .patternLine("NNN")
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_nugget", this.hasItem(Tags.Items.NUGGETS_IRON))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(PainterItems.SMALL_PAINT_BRUSH.get(), 1)
                .key('S', Tags.Items.STRING)
                .key('T', Tags.Items.RODS_WOODEN)
                .patternLine("S")
                .patternLine("T")
                .patternLine("T")
                .setGroup("paint_brush")
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(PainterItems.MEDIUM_PAINT_BRUSH.get(), 1)
                .key('W', ItemTags.WOOL)
                .key('T', Tags.Items.RODS_WOODEN)
                .patternLine("W")
                .patternLine("T")
                .patternLine("T")
                .setGroup("paint_brush")
                .addCriterion("has_wool", this.hasItem(ItemTags.WOOL))
                .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(PainterItems.LARGE_PAINT_BRUSH.get(), 1)
                .key('W', ItemTags.WOOL)
                .key('S', Tags.Items.STRING)
                .key('T', Tags.Items.RODS_WOODEN)
                .patternLine("SWS")
                .patternLine("STS")
                .patternLine(" T ")
                .setGroup("paint_brush")
                .addCriterion("has_wool", this.hasItem(ItemTags.WOOL))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(PainterItems.WORLD_PAINTING.get(), 1)
                .key('P', ItemTags.PLANKS)
                .key('S', Tags.Items.RODS_WOODEN)
                .key('W', ItemTags.WOOL)
                .patternLine("SSS")
                .patternLine("PWP")
                .patternLine("SSS")
                .addCriterion("has_planks", this.hasItem(ItemTags.PLANKS))
                .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
                .addCriterion("has_wool", this.hasItem(ItemTags.WOOL))
                .build(consumer);
        registerEasel(PainterBlocks.OAK_EASEL.get(), Blocks.OAK_PLANKS, Blocks.OAK_LOG, consumer);
        registerEasel(PainterBlocks.SPRUCE_EASEL.get(), Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, consumer);
        registerEasel(PainterBlocks.BIRCH_EASEL.get(), Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, consumer);
        registerEasel(PainterBlocks.JUNGLE_EASEL.get(), Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, consumer);
        registerEasel(PainterBlocks.ACACIA_EASEL.get(), Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, consumer);
        registerEasel(PainterBlocks.DARK_OAK_EASEL.get(), Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, consumer);
    }

    private void registerEasel(IItemProvider easel, IItemProvider planks, IItemProvider log, Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shapedRecipe(easel, 1)
                .key('P', planks)
                .key('L', log)
                .key('S', Tags.Items.RODS_WOODEN)
                .patternLine("LLL")
                .patternLine(" P ")
                .patternLine("S S")
                .setGroup("easel")
                .addCriterion("has_planks", this.hasItem(planks))
                .addCriterion("has_log", this.hasItem(log))
                .addCriterion("has_stick", this.hasItem(Items.STICK))
                .build(consumer);
    }
}
