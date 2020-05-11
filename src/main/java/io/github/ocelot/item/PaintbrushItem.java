package io.github.ocelot.item;

import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;

/**
 * @author Ocelot
 */
public class PaintbrushItem extends Item implements PaintDyeable
{
    public PaintbrushItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        if (world.getTileEntity(pos) instanceof PaintBucketTileEntity)
        {
            int color = ((PaintBucketTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).getColor();
            if (!this.hasColor(stack) || this.getPaint(stack) < MAX_PAINT || this.getColor(stack) != color)
            {
                this.setColor(stack, color);
                this.setPaint(stack, MAX_PAINT);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return this.getPaint(stack) < MAX_PAINT;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (double) this.getPaint(stack) / (double) MAX_PAINT;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            ItemStack emptyStack = new ItemStack(this);
            this.setPaint(emptyStack, MAX_PAINT);
            items.add(emptyStack);

            for (DyeColor color : DyeColor.values())
            {
                ItemStack stack = new ItemStack(this);
                this.setColor(stack, color.getColorValue());
                this.setPaint(stack, MAX_PAINT);
                items.add(stack);
            }
        }
    }
    /**
     * Fetches the amount of paint from the specified stack.
     *
     * @param stack The stack to query
     * @return The amount of paint left in the stack
     */
    public int getPaint(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null && compoundnbt.contains("paint", Constants.NBT.TAG_ANY_NUMERIC) ? compoundnbt.getInt("paint") : 0;
    }

    /**
     * Sets the amount of paint for the specified stack.
     *
     * @param stack The stack to set the paint amount for
     * @param paint The new amount of paint for the stack
     */
    public void setPaint(ItemStack stack, int paint)
    {
        stack.getOrCreateTag().putInt("paint", paint);
    }
}
