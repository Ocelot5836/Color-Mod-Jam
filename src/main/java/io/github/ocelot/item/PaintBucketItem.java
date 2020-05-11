package io.github.ocelot.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class PaintBucketItem extends BlockItem implements PaintDyeable
{
    public PaintBucketItem(Block block, Properties builder)
    {
        super(block, builder);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        super.fillItemGroup(group, items);
        if (this.isInGroup(group))
        {
            for (DyeColor color : DyeColor.values())
            {
                ItemStack stack = new ItemStack(this);
                this.setColor(stack, color.getColorValue());
                items.add(stack);
            }
        }
    }

    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        ItemStack newStack = stack.copy();
        CompoundNBT blockEntityTag = newStack.getOrCreateChildTag("BlockEntityTag");
        blockEntityTag.putInt("color", this.getColor(stack));
        // TODO fix black on place
        return setTileEntityNBT(world, player, pos, newStack);
    }
}
