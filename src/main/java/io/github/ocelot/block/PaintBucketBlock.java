package io.github.ocelot.block;

import io.github.ocelot.common.BaseBlock;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class PaintBucketBlock extends BaseBlock implements IWaterLoggable
{
    public PaintBucketBlock()
    {
        super(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F));
        this.setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false));
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }
}
