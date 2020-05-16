package io.github.ocelot.tileentity;

import io.github.ocelot.init.PainterBlocks;
import io.github.ocelot.init.PainterItems;
import io.github.ocelot.painting.FixedPaintingType;
import io.github.ocelot.painting.Painting;
import io.github.ocelot.painting.PaintingHolder;
import io.github.ocelot.painting.PaintingManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class EaselTileEntity extends TileEntity implements PaintingHolder, ISidedInventory
{
    private static final int[] SLOTS = {0};

    private UUID paintingId;

    public EaselTileEntity()
    {
        super(PainterBlocks.EASEL_TE.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        this.serializePainting(nbt);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        this.paintingId = this.deserializePainting(nbt);
        if (this.world != null)
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
    }

    @Nullable
    @Override
    public UUID getPaintingId()
    {
        return paintingId;
    }

    public void addNewPainting()
    {
        if (this.world != null)
        {
            Painting painting = new Painting();
            PaintingManager.get(this.world).addPainting(painting);
            this.setPainting(painting.getId());
        }
    }

    @Override
    public void setPainting(@Nullable UUID paintingId)
    {
        this.paintingId = paintingId;
        this.markDirty();
        if (this.world != null)
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return this.paintingId == null;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        if (index != 0 || this.paintingId == null)
            return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(PainterItems.WORLD_PAINTING.get());
        PainterItems.WORLD_PAINTING.get().setPainting(stack, this.paintingId);
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return this.removeStackFromSlot(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = this.getStackInSlot(0);
        if (index == 0)
            this.setPainting(null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if (this.world != null && index == 0)
        {
            UUID stackId = PainterItems.WORLD_PAINTING.get().getPaintingId(stack);
            if (FixedPaintingType.isFixed(stackId))
            {
                this.setPainting(stackId);
                return;
            }
            Painting painting = new Painting(PaintingManager.get(this.world).getPainting(PainterItems.WORLD_PAINTING.get().getPaintingId(stack)));
            PaintingManager.get(this.world).addPainting(painting);
            this.setPainting(painting.getId());
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if (this.world == null || this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clear()
    {
        this.setPainting(null);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction)
    {
        return index == 0 && stack.getItem() == PainterItems.WORLD_PAINTING.get();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        return index == 0;
    }
}
