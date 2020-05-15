package io.github.ocelot.block;

import io.github.ocelot.common.BaseBlock;
import io.github.ocelot.item.PaintBucketItem;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author Ocelot
 */
public class PaintBucketBlock extends BaseBlock implements IWaterLoggable
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(4, 0, 4, 12, 8, 12);

    public PaintBucketBlock()
    {
        super(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F));
        this.setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new PaintBucketTileEntity();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = new ItemStack(this);
        if (!(stack.getItem() instanceof PaintBucketItem))
            return super.getPickBlock(state, target, world, pos, player);

        int color = DyeColor.WHITE.getColorValue();
        if (world.getTileEntity(pos) instanceof PaintBucketTileEntity)
            color = Objects.requireNonNull((PaintBucketTileEntity) world.getTileEntity(pos)).getColor();
        ((PaintBucketItem) stack.getItem()).setColor(stack, color);

        return stack;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }
}
