package io.github.ocelot.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.entity.render.AfroRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class AfroItem extends Item
{
    public AfroItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack)
    {
        return ModList.get().isLoaded("curios") ? null : EquipmentSlotType.HEAD;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if (ModList.get().isLoaded("curios"))
            return super.onItemRightClick(world, player, hand);

        ItemStack stack = player.getHeldItem(hand);
        EquipmentSlotType equipmentSlotType = MobEntity.getSlotForItemStack(stack);
        if (player.getItemStackFromSlot(equipmentSlotType).isEmpty())
        {
            player.setItemStackToSlot(equipmentSlotType, stack.copy());
            stack.setCount(0);
            return ActionResult.resultSuccess(stack);
        }
        else
        {
            return ActionResult.resultFail(stack);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        if (!ModList.get().isLoaded("curios"))
            return null;
        return new Provider(new ICurio()
        {
            @Override
            public boolean canRightClickEquip()
            {
                return true;
            }

            @Override
            public boolean hasRender(String identifier, LivingEntity livingEntity)
            {
                return true;
            }

            @Override
            public void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
            {
                AfroRenderer.render(livingEntity, matrixStack, buffer, packedLight);
            }
        });
    }

    public static class Provider implements ICapabilityProvider
    {
        final LazyOptional<ICurio> capability;

        private Provider(ICurio curio)
        {
            this.capability = LazyOptional.of(() -> curio);
        }

        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CuriosCapability.ITEM.orEmpty(cap, this.capability);
        }
    }
}
