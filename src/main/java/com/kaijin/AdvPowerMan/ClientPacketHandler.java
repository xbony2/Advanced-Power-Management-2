/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.IOException;

import com.kaijin.AdvPowerMan.tileentities.TECommon;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class ClientPacketHandler{
	/*
	 * Packet format: 0: byte Packet Type 1: int x location of TileEntity 2: int
	 * y location of TileEntity 3: int z location of TileEntity
	 * 
	 * Currently used packet types
	 * 
	 * Server-to-Client: 0 = Universal description packet Charging Bench: 4: int
	 * charge level for texture 5: boolean activity state for texture
	 * 
	 * Battery Station: 4: boolean activity state for texture
	 * 
	 * Storage Monitor: 4: int charge level for texture 5: boolean power state
	 * for texture 6: boolean valid state for texture
	 */
	
	// @Override
	// public void onPacketData(INetworkManager network, Packet250CustomPayload
	// packet, Player player)
	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event){
		ByteBuf stream = event.packet.payload();
		// DataInputStream stream = new DataInputStream(new
		// ByteArrayInputStream(packet.data));
		
		// Determine packet type and coordinates of affected tile entity
		int packetType = -1;
		int x = 0;
		int y = 0;
		int z = 0;
		
		packetType = stream.readInt();
		x = stream.readInt();
		y = stream.readInt();
		z = stream.readInt();
		
		if(packetType == 0){
			World world = FMLClientHandler.instance().getClient().theWorld;
			TileEntity tile = world.getTileEntity(x, y, z);
			
			Exception e;
			try{
				((TECommon) tile).receiveDescriptionData(packetType, stream);
				return;
			}catch(ClassCastException ex){
				e = ex;
			}catch(NullPointerException ex){
				e = ex;
			}
			FMLLog.getLogger().info(
					"[AdvancedPowerManagement] " + "Client received description packet for " + x + ", " + y + ", " + z
							+ " but couldn't deliver to tile entity. (Details: " + e.toString() + ")");
			return;
		}
	}
}
