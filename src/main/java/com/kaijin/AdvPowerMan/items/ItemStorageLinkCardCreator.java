/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan.items;

import com.kaijin.AdvPowerMan.AdvancedPowerManagement;
import com.kaijin.AdvPowerMan.Info;

import ic2.api.tile.IEnergyStorage;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemStorageLinkCardCreator extends ItemCardBase
{
	public ItemStorageLinkCardCreator(String name)
	{
		super(name);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
		setTextureName(Info.TITLE_PACKED + ":LinkCardCreator");
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (entityplayer instanceof EntityPlayerMP && tile instanceof IEnergyStorage)
		{
			//if (Info.isDebugging) System.out.println("Clicked on X:" + x + " Y:" + y + " Z:" + z + " Dim:" + world.provider.dimensionId);
			ItemStack newcard = new ItemStack(AdvancedPowerManagement.itemStorageLinkCard);
			setCoordinates(newcard, x, y, z, world.provider.dimensionId);
			entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = newcard;
			return true;
		}
		return false;
	}
}
