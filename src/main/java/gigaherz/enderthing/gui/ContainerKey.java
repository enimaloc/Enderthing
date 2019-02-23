package gigaherz.enderthing.gui;

import gigaherz.enderthing.blocks.TileEnderKeyChest;
import gigaherz.enderthing.storage.IInventoryManager;
import gigaherz.enderthing.storage.InventoryManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class ContainerKey extends Container
{
    private World world;
    private BlockPos pos;

    public ContainerKey(InventoryPlayer playerInventory, int id, boolean isPack, boolean isPriv, EntityPlayer player, World world, BlockPos pos)
    {

        int lockedSlot = -1;

        UUID bound = player.getUniqueID();

        if (isPack)
        {
            lockedSlot = pos.getX();
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEnderKeyChest)
            {
                TileEnderKeyChest chest = (TileEnderKeyChest) te;

                if (chest.isBoundToPlayer())
                    bound = chest.getPlayerBound();

                chest.openChest();
            }
            else if (te instanceof TileEntityEnderChest)
            {
                TileEntityEnderChest chest = (TileEntityEnderChest) te;

                chest.openChest();
            }

            this.world = world;
            this.pos = pos;
        }

        IInventoryManager mgr = isPriv ?
                InventoryManager.get(world).getPrivate(bound) :
                InventoryManager.get(world);

        IItemHandler inventory = mgr.getInventory(id);

        for (int j = 0; j < 3; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlot(new SlotItemHandler(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int py = 0; py < 3; ++py)
        {
            for (int px = 0; px < 9; ++px)
            {
                int slot = px + py * 9 + 9;
                if (slot == lockedSlot)
                    this.addSlot(new SlotNoAccess(playerInventory, slot, 8 + px * 18, 103 + py * 18 - 18));
                else
                    this.addSlot(new Slot(playerInventory, slot, 8 + px * 18, 103 + py * 18 - 18));
            }
        }

        for (int slot = 0; slot < 9; ++slot)
        {
            if (slot == lockedSlot)
                this.addSlot(new SlotNoAccess(playerInventory, slot, 8 + slot * 18, 143));
            else
                this.addSlot(new Slot(playerInventory, slot, 8 + slot * 18, 143));
        }
    }

    public static class SlotNoAccess extends Slot
    {
        public SlotNoAccess(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return false;
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack)
        {
            return false;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (world != null)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEnderKeyChest)
            {
                TileEnderKeyChest chest = (TileEnderKeyChest) te;
                chest.closeChest();
            }
            else if (te instanceof TileEntityEnderChest)
            {
                TileEntityEnderChest chest = (TileEntityEnderChest) te;
                chest.closeChest();
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack stackCopy = stack.copy();

        if (index < 3 * 9)
        {
            if (!this.mergeItemStack(stack, 3 * 9, this.inventorySlots.size(), true))
            {
                return ItemStack.EMPTY;
            }
        }
        else if (!this.mergeItemStack(stack, 0, 3 * 9, false))
        {
            return ItemStack.EMPTY;
        }

        if (stack.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }

        return stackCopy;
    }
}
