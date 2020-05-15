package io.github.ocelot.block;

import io.github.ocelot.common.BaseBlock;
import io.github.ocelot.common.VoxelShapeHelper;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.painting.Painting;
import io.github.ocelot.tileentity.EaselTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class EaselBlock extends BaseBlock implements IWaterLoggable
{
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape[] SHAPES = getShapes(false);
    private static final VoxelShape[] FULL_SHAPES = getShapes(true);

    public EaselBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    private Vec3d raytrace(Vec3d planePoint, Vec3d planeNormal, Vec3d start, Vec3d end)
    {
        Vec3d u = end.subtract(start);
        Vec3d w = start.subtract(planePoint);

        double d = planeNormal.dotProduct(u);
        double n = -planeNormal.dotProduct(w);

        if (Math.abs(d) < 1.0E-7D)
            return null;

        double si = n / d;
        if (si < 0 || si > 1)
            return null;
        return start.add(u.mul(si, si, si));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        BlockPos easelPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        if (world.getTileEntity(easelPos) instanceof EaselTileEntity)
        {
            EaselTileEntity te = (EaselTileEntity) Objects.requireNonNull(world.getTileEntity(easelPos));
            ItemStack stack = player.getHeldItem(hand);
            if (te.getPaintingId() != null)
            {
                if (stack.getItem() == PainterItems.PAINT_BRUSH.get())
                {
                    if (!world.isRemote())
                    {
                        Direction facing = state.get(HORIZONTAL_FACING);
                        Vec3d lookVec = player.getLookVec();
                        double reach = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
                        Vec3d start = player.getEyePosition(0);
                        Vec3d end = start.add(lookVec.mul(reach, reach, reach));

                        Vec3d result = raytrace(new Vec3d(pos.add(facing == Direction.SOUTH || facing == Direction.WEST ? 0 : 1, 0, facing == Direction.SOUTH || facing == Direction.WEST ? 1 : 0)).subtract(facing.getXOffset() * 0.425, 0, facing.getZOffset() * 0.425), new Vec3d(facing.getXOffset(), 0.45, facing.getZOffset()), start, end);

                        // 0, 0, 0.5

                        // 0, 14, 15.6

                        if (result != null)
                        {
                            Vec3d normalResult = result.subtract(pos.getX(), pos.getY(), pos.getZ());
                            if(normalResult.getX() >= 0 && normalResult.getX() < 1 && normalResult.getY() >= 0 && normalResult.getY() < 1)
                            {
                                int pixelX = (int) Math.round(normalResult.getX() * Painting.SIZE);
                                int pixelY = (int) Math.round((1.0 - normalResult.getY()) * Painting.SIZE);
                            System.out.println(pixelX + ", " + pixelY);
                            ((ServerWorld) world).spawnParticle(ParticleTypes.MYCELIUM, result.getX(), result.getY(), result.getZ(), 100, 0, 0, 0, 0);}
                        }
                    }
                    return ActionResultType.SUCCESS;
                }
                else
                {
                    if (!world.isRemote())
                    {
                        UUID paintingId = te.getPaintingId();
                        te.setPainting(null);
                        ItemStack teStack = new ItemStack(PainterItems.WORLD_PAINTING.get());
                        PainterItems.WORLD_PAINTING.get().setPainting(teStack, paintingId);
                        world.addEntity(new ItemEntity(world, pos.getX() + 0.5 + state.get(HORIZONTAL_FACING).getXOffset() / 1.9, pos.getY() + 0.5 + (state.get(HALF) == DoubleBlockHalf.LOWER ? 1 : 0), pos.getZ() + 0.5 + state.get(HORIZONTAL_FACING).getZOffset() / 1.9, teStack));
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            else if (stack.getItem() == PainterItems.WORLD_PAINTING.get())
            {
                if (!world.isRemote())
                {
                    UUID paintingId = PainterItems.WORLD_PAINTING.get().getPaintingId(stack);
                    if (paintingId == null)
                    {
                        te.addNewPainting();
                    }
                    else
                    {
                        te.setPainting(paintingId);
                    }
                    stack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        boolean full = false;
        BlockPos easelPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        if (world.getTileEntity(easelPos) instanceof EaselTileEntity)
            full = Objects.requireNonNull((EaselTileEntity) world.getTileEntity(easelPos)).hasPainting();
        return (full ? FULL_SHAPES : SHAPES)[state.get(HORIZONTAL_FACING).getHorizontalIndex() + (state.get(HALF) == DoubleBlockHalf.UPPER ? 4 : 0)];
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf)
        {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.isValidPosition(world, currentPos) ? world.getFluidState(currentPos).getBlockState() : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
        }
        else
        {
            return world.getFluidState(currentPos).getBlockState();
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockpos = context.getPos();
        return blockpos.getY() < context.getWorld().getDimension().getHeight() - 1 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context) ? super.getStateForPlacement(context) : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        world.setBlockState(pos.up(), this.getDefaultState().with(HORIZONTAL_FACING, world.getBlockState(pos).get(HORIZONTAL_FACING)).with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, world.getFluidState(pos.up()).getFluid() == Fluids.WATER), 3);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        if (state.get(HALF) != DoubleBlockHalf.UPPER)
        {
            return super.isValidPosition(state, world, pos);
        }
        else
        {
            BlockState blockstate = world.getBlockState(pos.down());
            if (state.getBlock() != this)
                return super.isValidPosition(state, world, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(world, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf)
        {
            world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            world.playEvent(player, 2001, blockpos.up(), Block.getStateId(blockstate));
            if (!world.isRemote() && !player.isCreative())
            {
                spawnDrops(state, world, doubleblockhalf == DoubleBlockHalf.UPPER ? pos.down() : pos, null, player, player.getHeldItemMainhand());
            }
        }

//        Huh?
//        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return state.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new EaselTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, HALF, WATERLOGGED);
    }

    private static VoxelShape[] getShapes(boolean full)
    {
        VoxelShape[] shapes = new VoxelShape[8];
        VoxelShapeHelper.Builder bottom = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(0, 0, 0, 16, full ? 32 : 28, 10));
        VoxelShapeHelper.Builder top = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(0, -16, 0, 16, full ? 16 : 12, 10));
        for (int i = 0; i < 2; i++)
        {
            for (Direction facing : Direction.values())
            {
                if (facing.getHorizontalIndex() == -1)
                    continue;
                int index = i * 4 + facing.getHorizontalIndex();
                shapes[index] = i == 1 ? top.rotate(facing).build() : bottom.rotate(facing).build();
            }
        }
        return shapes;
    }

//    private static VoxelShape[] getPaintingShapes()
//    {
//        VoxelShape[] shapes = new VoxelShape[4];
//        VoxelShapeHelper.Builder partBuilder = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(0, 0.5, 7.75, 16, 2, 8.25));
//        for (Direction facing : Direction.values())
//        {
//            if (facing.getHorizontalIndex() == -1)
//                continue;
//            shapes[facing.getHorizontalIndex()] = partBuilder.rotate(facing).build();
//        }
//        return shapes;
//    }
}
