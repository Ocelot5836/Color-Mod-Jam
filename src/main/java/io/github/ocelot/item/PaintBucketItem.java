package io.github.ocelot.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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
        if (this.isInGroup(group))
        {
            for (DyeColor color : DyeColor.values())
            {
                ItemStack stack = new ItemStack(this);
                this.setColor(stack, color.getColorValue());
                this.setPaint(stack, MAX_PAINT);
                items.add(stack);
            }
        }
    }
}
