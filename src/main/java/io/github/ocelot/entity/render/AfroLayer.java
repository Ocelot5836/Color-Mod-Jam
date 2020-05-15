package io.github.ocelot.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.init.PainterItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosAPI;

/**
 * @author Ocelot
 */
public class AfroLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    public AfroLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer)
    {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (stack.getItem() != PainterItems.AFRO.get() && ModList.get().isLoaded("curios"))
        {
            CuriosAPI.getCurioEquipped(PainterItems.AFRO.get(), player).ifPresent(curio ->
            {
                ((IHasHead)this.getEntityModel()).getModelHead().translateRotate(matrixStack);
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(player, curio.getRight(), ItemCameraTransforms.TransformType.HEAD, false, matrixStack, buffer, packedLight);
            });
        }
    }
}
