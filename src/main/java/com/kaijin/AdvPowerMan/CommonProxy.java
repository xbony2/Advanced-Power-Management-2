/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.kaijin.AdvPowerMan.containers.ContainerAdjustableTransformer;
import com.kaijin.AdvPowerMan.containers.ContainerAdvEmitter;
import com.kaijin.AdvPowerMan.containers.ContainerBatteryStation;
import com.kaijin.AdvPowerMan.containers.ContainerChargingBench;
import com.kaijin.AdvPowerMan.containers.ContainerStorageMonitor;
import com.kaijin.AdvPowerMan.gui.GuiAdjustableTransformer;
import com.kaijin.AdvPowerMan.gui.GuiAdvEmitter;
import com.kaijin.AdvPowerMan.gui.GuiBatteryStation;
import com.kaijin.AdvPowerMan.gui.GuiChargingBench;
import com.kaijin.AdvPowerMan.gui.GuiStorageMonitor;
import com.kaijin.AdvPowerMan.tileentities.TEAdjustableTransformer;
import com.kaijin.AdvPowerMan.tileentities.TEAdvEmitter;
import com.kaijin.AdvPowerMan.tileentities.TEBatteryStation;
import com.kaijin.AdvPowerMan.tileentities.TEChargingBench;
import com.kaijin.AdvPowerMan.tileentities.TECommon;
import com.kaijin.AdvPowerMan.tileentities.TEStorageMonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IGuiHandler;
//import cpw.mods.fml.common.network.PacketDispatcher;
//import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler{
	public void load(){}
	
	public boolean isClient(){
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
	
	public boolean isServer(){
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		if(!world.blockExists(x, y, z))
			return null;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID == 1 && tile instanceof TEChargingBench){
			return new ContainerChargingBench(player.inventory, (TEChargingBench) tile);
		}else if(ID == 2 && tile instanceof TEBatteryStation){
			return new ContainerBatteryStation(player.inventory, (TEBatteryStation) tile);
		}else if(ID == 3 && tile instanceof TEStorageMonitor){
			return new ContainerStorageMonitor(player.inventory, (TEStorageMonitor) tile);
		}else if(ID == 4 && tile instanceof TEAdvEmitter){
			return new ContainerAdvEmitter((TEAdvEmitter) tile);
		}else if(ID == 5 && tile instanceof TEAdjustableTransformer){
			return new ContainerAdjustableTransformer((TEAdjustableTransformer) tile);
		}
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		if(!world.blockExists(x, y, z))
			return null;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID == 1 && tile instanceof TEChargingBench){
			return new GuiChargingBench(player.inventory, (TEChargingBench) tile);
		}else if(ID == 2 && tile instanceof TEBatteryStation){
			return new GuiBatteryStation(player.inventory, (TEBatteryStation) tile);
		}else if(ID == 3 && tile instanceof TEStorageMonitor){
			return new GuiStorageMonitor(player.inventory, (TEStorageMonitor) tile);
		}else if(ID == 4 && tile instanceof TEAdvEmitter){
			return new GuiAdvEmitter((TEAdvEmitter) tile);
		}else if(ID == 5 && tile instanceof TEAdjustableTransformer){
			return new GuiAdjustableTransformer((TEAdjustableTransformer) tile);
		}
		
		return null;
	}
	
	/*
	 * Packet format: 0: byte Packet Type 1: int x location of TileEntity 2: int
	 * y location of TileEntity 3: int z location of TileEntity
	 * 
	 * Currently used packet types
	 * 
	 * Client-to-Server: 0 = GUI button command 4: int Button ID clicked
	 */
	
	public void onPacketDataClient(ByteBuf source, EntityPlayer entityPlayer){
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(Arrays.copyOfRange(source.array(), 1, source.array().length)));
		
		// Determine packet type and coordinates of affected tile entity
		int packetType = -1;
		int x;
		int y;
		int z;
		try{
			packetType = stream.readInt();
			x = stream.readInt();
			y = stream.readInt();
			z = stream.readInt();
		}catch(IOException e){
			FMLLog.getLogger().info("[AdvancedPowerManagement] " + "Failed to read packet from client. (Details: " + e.toString() + ")");
			return;
		}
		
		if(packetType == 0){
			Exception e;
			try{
				World world = entityPlayer.worldObj;
				TileEntity tile = world.getTileEntity(x, y, z);
				
				int buttonID = stream.readInt();
				
				((TECommon) tile).receiveGuiButton(buttonID);
				return;
			}catch(ClassCastException ex){
				e = ex;
			}catch(NullPointerException ex){
				e = ex;
			}catch(IOException ex){
				e = ex;
			}
			
			FMLLog.getLogger().info(
					"[AdvancedPowerManagement] " + "Server received GUI button packet for " + x + ", " + y + ", " + z
							+ " but couldn't deliver to tile entity. (Details: " + e.toString() + ")");
			return;
		}
	}
}
