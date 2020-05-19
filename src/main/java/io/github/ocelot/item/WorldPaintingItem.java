package io.github.ocelot.item;

import io.github.ocelot.entity.WorldPaintingEntity;
import io.github.ocelot.painting.FixedPaintingType;
import net.minecraft.entity.EntityType;
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
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class WorldPaintingItem extends HangingEntityItem
{
    private final boolean teleporation;

    public WorldPaintingItem(boolean teleporation, Properties properties)
    {
        super(EntityType.PAINTING, properties);
        this.teleporation = teleporation;
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (this.hasPainting(stack))
        {
            UUID paintingId = this.getPaintingId(stack);
            if (FixedPaintingType.isFixed(paintingId))
                return FixedPaintingType.getType(paintingId).getTranslationKey();
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (!this.teleporation)
            super.fillItemGroup(group, items);
        if (this.isInGroup(group))
        {
            for (FixedPaintingType type : FixedPaintingType.values())
            {
                if (type.isTeleportation() == this.teleporation)
                {
                    ItemStack stack = new ItemStack(this);
                    this.setPainting(stack, type.getPainting().getId());
                    this.setTeleportation(stack, this.teleporation);
                    items.add(stack);
                }
            }
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
        } else
        {
            World world = context.getWorld();
            WorldPaintingEntity hangingentity = new WorldPaintingEntity(world, blockpos1, direction);

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
            } else
            {
                return ActionResultType.CONSUME;
            }
        }
    }

    public boolean hasPainting(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.hasUniqueId("paintingId");
    }

    public void setPainting(ItemStack stack, UUID paintingId)
    {
        stack.getOrCreateChildTag("EntityTag").putUniqueId("paintingId", paintingId);
    }

    @Nullable
    public UUID getPaintingId(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.hasUniqueId("paintingId") ? compoundnbt.getUniqueId("paintingId") : null;
    }

    public boolean hasTeleportation(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.contains("teleportation", Constants.NBT.TAG_BYTE);
    }

    public void setTeleportation(ItemStack stack, boolean teleportation)
    {
        stack.getOrCreateChildTag("EntityTag").putBoolean("teleportation", teleportation);
    }

    public boolean getTeleportation(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getChildTag("EntityTag");
        return compoundnbt != null && compoundnbt.hasUniqueId("teleportation") && compoundnbt.getBoolean("teleportation");
    }
}
