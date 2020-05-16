package io.github.ocelot.item;

import io.github.ocelot.init.PainterStats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        if (this.hasColor(stack) && world.getBlockState(pos).getBlock() == Blocks.CAULDRON)
        {
            BlockState state = world.getBlockState(pos);
            int level = state.get(CauldronBlock.LEVEL);
            if (level > 0)
            {
                if (!world.isRemote())
                {
                    this.removeColor(stack);
                    world.setBlockState(pos, state.with(CauldronBlock.LEVEL, level - 1));
                    if (player != null && !player.isCreative())
                        player.addStat(PainterStats.CLEAN_PAINT_BUCKET);
                    world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.PLAYERS, 0.1F, 2.0F);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
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
                items.add(stack);
            }
        }
    }

    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        ItemStack newStack = stack.copy();
        CompoundNBT blockEntityTag = newStack.getOrCreateChildTag("BlockEntityTag");
        blockEntityTag.putInt("color", this.getColor(stack));
        return setTileEntityNBT(world, player, pos, newStack);
    }
}
