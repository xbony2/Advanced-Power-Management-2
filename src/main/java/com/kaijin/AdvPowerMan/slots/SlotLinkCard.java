/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan.slots;

import com.kaijin.AdvPowerMan.Info;
import com.kaijin.AdvPowerMan.items.ItemStorageLinkCard;
import com.kaijin.AdvPowerMan.tileentities.TECommon;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotLinkCard extends Slot{
	public SlotLinkCard(IInventory inv, int index, int xpos, int ypos) {
		super(inv, index, xpos, ypos);
	}
	
	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean isItemValid(ItemStack stack){
		// Decide if the item is a link card
		if(stack != null && stack.getItem() instanceof ItemStorageLinkCard){
			return true;
		}
		return false;
	}
	
	@Override
	public int getSlotStackLimit(){
		return 1;
	}
	
	@Override
	public IIcon getBackgroundIconIndex(){
		return Info.iconSlotLinkCard;
		// return 246;
	}
	
	@Override
	public void onSlotChanged(){
		if(this.inventory instanceof TECommon){
			((TECommon) inventory).markDirty(this.getSlotIndex());
		}else{
			inventory.markDirty();
		}
	}
}
