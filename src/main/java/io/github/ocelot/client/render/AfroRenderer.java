package io.github.ocelot.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.init.PainterItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Ocelot
 */
public class AfroRenderer
{
    private static final ItemStack AFRO = new ItemStack(PainterItems.AFRO.get());

    @OnlyIn(Dist.CLIENT)
    public static void render(LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getRenderManager().getRenderer(livingEntity);
        if (!(renderer instanceof LivingRenderer))
            return;

        Model model = ((LivingRenderer<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>>) renderer).getEntityModel();
        if (!(model instanceof IHasHead))
            return;

        ((IHasHead) model).getModelHead().translateRotate(matrixStack);
        matrixStack.translate(0.0D, -0.25D, 0.0D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrixStack.scale(0.625F, -0.625F, -0.625F);
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(livingEntity, AFRO, ItemCameraTransforms.TransformType.HEAD, false, matrixStack, buffer, packedLight);
    }
}
