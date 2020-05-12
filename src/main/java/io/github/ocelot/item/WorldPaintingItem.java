package io.github.ocelot.item;

import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.init.PainterEntities;
import io.github.ocelot.painting.Painting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HangingEntityItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class WorldPaintingItem extends HangingEntityItem
{
    public WorldPaintingItem(Properties properties)
    {
        super(EntityType.PAINTING, properties);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        super.fillItemGroup(group, items);
        if (this.isInGroup(group))
        {
            ItemStack stack = new ItemStack(this);
            this.setPainting(stack, Painting.PLAD_PAINTING.getId());
            items.add(stack);
        }
    }

    public ActionResultType onItemUse(ItemUseContext context)
    {
        BlockPos blockpos = context.getPos();
        Direction direction = context.getFace();
        BlockPos blockpos1 = blockpos.offset(direction);
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItem();
        if (playerentity != null && !this.canPlace(playerentity, direction, itemstack, blockpos1))
        {
            return ActionResultType.FAIL;
        }
        else
        {
            World world = context.getWorld();
            HangingEntity hangingentity = new WorldPaintingEntity(world, blockpos1, direction);

            CompoundNBT compoundnbt = itemstack.getTag();
            if (compoundnbt != null)
            {
                EntityType.applyItemNBT(world, playerentity, hangingentity, compoundnbt);
            }

            if (hangingentity.onValidSurface())
            {
                if (!world.isRemote())
                {
                    hangingentity.playPlaceSound();
                    world.addEntity(hangingentity);
                }

                itemstack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            else
            {
                return ActionResultType.CONSUME;
            }
        }
    }

    public void setPainting(ItemStack stack, UUID paintingId)
    {
        stack.getOrCreateChildTag("EntityTag").putUniqueId("paintingId", paintingId);
    }

    public boolean hasPainting(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.hasUniqueId("paintingId");
    }

    @Nullable
    public UUID getPaintingId(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.hasUniqueId("paintingId") ? compoundnbt.getUniqueId("paintingId") : null;
    }

    public void removePainting(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        if (compoundnbt != null && compoundnbt.hasUniqueId("color"))
        {
            compoundnbt.removeUniqueId("color");
        }
    }
}
