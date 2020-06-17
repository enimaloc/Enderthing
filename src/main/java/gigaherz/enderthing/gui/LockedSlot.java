package gigaherz.enderthing.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class LockedSlot extends Slot
{
    public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn)
    {
        return false;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack)
    {
        return false;
    }
}
