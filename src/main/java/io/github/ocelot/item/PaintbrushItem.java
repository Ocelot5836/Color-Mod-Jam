package io.github.ocelot.item;

import io.github.ocelot.WorldPainter;
import io.github.ocelot.init.PainterStats;
import io.github.ocelot.tileentity.PaintBucketTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * @author Ocelot
 */
public class PaintbrushItem extends Item implements Paintbrush
{
    private final BrushSize brushSize;

    public PaintbrushItem(BrushSize brushSize, Properties properties)
    {
        super(properties);
        this.brushSize = brushSize;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey(stack) + ".brush", new TranslationTextComponent(this.brushSize.getTranslationKey())).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return "item." + WorldPainter.MOD_ID + ".paintbrush";
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        if (world.getTileEntity(pos) instanceof PaintBucketTileEntity)
        {
            int color = ((PaintBucketTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).getColor();
            if (!this.hasColor(stack) || this.getPaint(stack) < this.getBrush(stack).getMaxPaint() || this.getColor(stack) != color)
            {
                if (!world.isRemote())
                {
                    this.setColor(stack, color);
                    this.setPaint(stack, this.getBrush(stack).getMaxPaint());
                    world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.PLAYERS, 0.1F, 2.0F);
                }
                return ActionResultType.SUCCESS;
            }
        }
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
                        player.addStat(PainterStats.CLEAN_PAINT_BRUSH);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return this.hasColor(stack) && this.getPaint(stack) < this.getBrush(stack).getMaxPaint();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1.0 - (double) this.getPaint(stack) / (double) this.getBrush(stack).getMaxPaint();
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            ItemStack stack = new ItemStack(this);
            this.setPaint(stack, this.getBrush(stack).getMaxPaint());
            items.add(stack);
        }
    }

    @Override
    public BrushSize getBrush(ItemStack stack)
    {
        return brushSize;
    }
}
