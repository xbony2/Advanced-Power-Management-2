/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.kaijin.AdvPowerMan.tileentities.TECommon;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{

	public static EntityPlayer getPlayer() 
	{	
		return Minecraft.getMinecraft().thePlayer;
	}
	/*	@Override
	public void load()
	{
		// MinecraftForgeClient.preloadTexture(Info.ITEM_PNG);
		// MinecraftForgeClient.preloadTexture(Info.BLOCK_PNG);
		// MinecraftForgeClient.preloadTexture(Info.GUI1_PNG);
		// MinecraftForgeClient.preloadTexture(Info.GUI2_PNG);
		// MinecraftForgeClient.preloadTexture(Info.GUI3_PNG);
		// MinecraftForgeClient.preloadTexture(Info.GUI4_PNG);
	}*/
}
