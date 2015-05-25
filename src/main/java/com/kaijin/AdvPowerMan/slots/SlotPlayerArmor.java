/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan.slots;

import com.kaijin.AdvPowerMan.Info;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotPlayerArmor extends SlotCustom{
	// The armor type that can be placed on that slot, it uses the same values
	// of armorType field on ItemArmor.
	final int armorType;
	
	public SlotPlayerArmor(IInventory inv, int index, int xpos, int ypos, int armorType) {
		super(inv, index, xpos, ypos);
		this.armorType = armorType;
	}
	
	@Override
	public int getSlotStackLimit(){
		return 1;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		if(stack == null)
			return false;
		return stack.getItem() instanceof ItemArmor ? ((ItemArmor) stack.getItem()).armorType == this.armorType : (Item
				.getIdFromItem(stack.getItem()) == Item.getIdFromItem(Item.getItemFromBlock(Blocks.pumpkin)) ? this.armorType == 0 : false);
	}
	
	@Override
	public IIcon getBackgroundIconIndex(){
		return Info.iconSlotPlayerArmor[armorType];
		// return 240 + armorType;
	}
}
