package io.github.ocelot.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.container.BobRossTradeContainer;
import io.github.ocelot.init.PainterMessages;
import io.github.ocelot.network.SelectBobRossTradeMessage;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class BobRossTradeScreen extends ContainerScreen<BobRossTradeContainer>
{
    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");

    private int selectedMerchantRecipe;
    private final BobRossTradeScreen.TradeButton[] tradeButtons = new BobRossTradeScreen.TradeButton[7];
    private int scroll;
    private boolean field_214140_o;

    public BobRossTradeScreen(BobRossTradeContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.xSize = 276;
    }

    private void syncServerSelectedRecipe()
    {
        this.container.setCurrentRecipeIndex(this.selectedMerchantRecipe);
        this.container.func_217046_g(this.selectedMerchantRecipe);
        PainterMessages.INSTANCE.send(PacketDistributor.SERVER.noArg(), new SelectBobRossTradeMessage(this.selectedMerchantRecipe));
    }

    @Override
    protected void init()
    {
        super.init();
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        for (int y = 0; y < 7; ++y)
        {
            this.tradeButtons[y] = this.addButton(new BobRossTradeScreen.TradeButton(guiLeft + 5, guiTop + 18 + y * 20, y, (button) ->
            {
                if (button instanceof BobRossTradeScreen.TradeButton)
                {
                    this.selectedMerchantRecipe = ((BobRossTradeScreen.TradeButton) button).getIndex() + this.scroll;
                    this.syncServerSelectedRecipe();
                }
            }));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String title = this.title.getFormattedText();
        this.font.drawString(title, (float) (49 + this.xSize / 2 - this.font.getStringWidth(title) / 2), 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, this.ySize - 94, 4210752);

        String trades = I18n.format("merchant.trades");
        int k1 = this.font.getStringWidth(trades);
        this.font.drawString(trades, (float) (5 - k1 / 2 + 48), 6.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        blit(guiLeft, guiTop, this.getBlitOffset(), 0.0F, 0.0F, this.xSize, this.ySize, 256, 512);
        MerchantOffers merchantoffers = this.container.getOffers();
        if (!merchantoffers.isEmpty())
        {
            int k = this.selectedMerchantRecipe;
            if (k < 0 || k >= merchantoffers.size())
            {
                return;
            }

            MerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.hasNoUsesLeft())
            {
                this.getMinecraft().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                blit(this.guiLeft + 83 + 99, this.guiTop + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
            }
        }

    }

    private void func_214129_a(int p_214129_1_, int p_214129_2_, MerchantOffers p_214129_3_)
    {
        int i = p_214129_3_.size() + 1 - 7;
        if (i > 1)
        {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, this.scroll * k);
            if (this.scroll == i - 1)
            {
                i1 = 113;
            }

            blit(p_214129_1_ + 94, p_214129_2_ + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
        }
        else
        {
            blit(p_214129_1_ + 94, p_214129_2_ + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
        }

    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        MerchantOffers merchantoffers = this.container.getOffers();
        if (!merchantoffers.isEmpty())
        {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            RenderSystem.pushMatrix();
            RenderSystem.enableRescaleNormal();
            this.getMinecraft().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            this.func_214129_a(i, j, merchantoffers);
            int i1 = 0;

            for (MerchantOffer merchantoffer : merchantoffers)
            {
                if (!this.func_214135_a(merchantoffers.size()) || (i1 >= this.scroll && i1 < 7 + this.scroll))
                {
                    ItemStack itemstack = merchantoffer.getBuyingStackFirst();
                    ItemStack itemstack1 = merchantoffer.func_222205_b();
                    ItemStack itemstack2 = merchantoffer.getBuyingStackSecond();
                    ItemStack itemstack3 = merchantoffer.getSellingStack();
                    this.itemRenderer.zLevel = 100.0F;
                    int j1 = k + 3;
                    this.func_214137_a(itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty())
                    {
                        this.itemRenderer.renderItemAndEffectIntoGUI(itemstack2, i + 5 + 35, j1);
                        this.itemRenderer.renderItemOverlays(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.func_214134_a(merchantoffer, i, j1);
                    this.itemRenderer.renderItemAndEffectIntoGUI(itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.renderItemOverlays(this.font, itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.zLevel = 0.0F;
                    k += 20;
                }
                ++i1;
            }

            int k1 = this.selectedMerchantRecipe;
            MerchantOffer merchantoffer1 = merchantoffers.get(k1);

            if (merchantoffer1.hasNoUsesLeft() && this.isPointInRegion(186, 35, 22, 21, p_render_1_, p_render_2_) && this.container.showLocked())
            {
                this.renderTooltip(I18n.format("merchant.deprecated"), p_render_1_, p_render_2_);
            }

            for (BobRossTradeScreen.TradeButton merchantscreen$tradebutton : this.tradeButtons)
            {
                if (merchantscreen$tradebutton.isHovered())
                {
                    merchantscreen$tradebutton.renderToolTip(p_render_1_, p_render_2_);
                }

                merchantscreen$tradebutton.visible = merchantscreen$tradebutton.index < this.container.getOffers().size();
            }

            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
        }

        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    private void func_214134_a(MerchantOffer p_214134_1_, int p_214134_2_, int p_214134_3_)
    {
        RenderSystem.enableBlend();
        this.getMinecraft().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        if (p_214134_1_.hasNoUsesLeft())
        {
            blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
        }
        else
        {
            blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
        }

    }

    private void func_214137_a(ItemStack p_214137_1_, ItemStack p_214137_2_, int p_214137_3_, int p_214137_4_)
    {
        this.itemRenderer.renderItemAndEffectIntoGUI(p_214137_1_, p_214137_3_, p_214137_4_);
        if (p_214137_2_.getCount() == p_214137_1_.getCount())
        {
            this.itemRenderer.renderItemOverlays(this.font, p_214137_1_, p_214137_3_, p_214137_4_);
        }
        else
        {
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_2_, p_214137_3_, p_214137_4_, p_214137_2_.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_1_, p_214137_3_ + 14, p_214137_4_, p_214137_1_.getCount() == 1 ? "1" : null);
            this.getMinecraft().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            this.setBlitOffset(this.getBlitOffset() + 300);
            blit(p_214137_3_ + 7, p_214137_4_ + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }

    }

    private boolean func_214135_a(int p_214135_1_)
    {
        return p_214135_1_ > 7;
    }

    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_)
    {
        int i = this.container.getOffers().size();
        if (this.func_214135_a(i))
        {
            int j = i - 7;
            this.scroll = (int) ((double) this.scroll - p_mouseScrolled_5_);
            this.scroll = MathHelper.clamp(this.scroll, 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_)
    {
        int i = this.container.getOffers().size();
        if (this.field_214140_o)
        {
            int j = this.guiTop + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float) p_mouseDragged_3_ - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scroll = MathHelper.clamp((int) f, 0, l);
            return true;
        }
        else
        {
            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        this.field_214140_o = false;
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (this.func_214135_a(this.container.getOffers().size()) && p_mouseClicked_1_ > (double) (i + 94) && p_mouseClicked_1_ < (double) (i + 94 + 6) && p_mouseClicked_3_ > (double) (j + 18) && p_mouseClicked_3_ <= (double) (j + 18 + 139 + 1))
        {
            this.field_214140_o = true;
        }

        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeButton extends Button
    {
        final int index;

        public TradeButton(int x, int y, int index, Button.IPressable pressable)
        {
            super(x, y, 89, 20, "", pressable);
            this.index = index;
            this.visible = false;
        }

        public int getIndex()
        {
            return this.index;
        }

        public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_)
        {
            if (this.isHovered && BobRossTradeScreen.this.container.getOffers().size() > this.index + BobRossTradeScreen.this.scroll)
            {
                if (p_renderToolTip_1_ < this.x + 20)
                {
                    ItemStack itemstack = BobRossTradeScreen.this.container.getOffers().get(this.index + BobRossTradeScreen.this.scroll).func_222205_b();
                    BobRossTradeScreen.this.renderTooltip(itemstack, p_renderToolTip_1_, p_renderToolTip_2_);
                }
                else if (p_renderToolTip_1_ < this.x + 50 && p_renderToolTip_1_ > this.x + 30)
                {
                    ItemStack itemstack2 = BobRossTradeScreen.this.container.getOffers().get(this.index + BobRossTradeScreen.this.scroll).getBuyingStackSecond();
                    if (!itemstack2.isEmpty())
                    {
                        BobRossTradeScreen.this.renderTooltip(itemstack2, p_renderToolTip_1_, p_renderToolTip_2_);
                    }
                }
                else if (p_renderToolTip_1_ > this.x + 65)
                {
                    ItemStack itemstack1 = BobRossTradeScreen.this.container.getOffers().get(this.index + BobRossTradeScreen.this.scroll).getSellingStack();
                    BobRossTradeScreen.this.renderTooltip(itemstack1, p_renderToolTip_1_, p_renderToolTip_2_);
                }
            }
        }
    }
}
