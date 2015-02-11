package com.kaijin.AdvPowerMan;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<AdvPacket> 
{
	public static ChannelHandler instance = new ChannelHandler();	

	public ChannelHandler() 
	{		
		addDiscriminator(0, AdvPacket.class);
	}	

	@Override
	public void encodeInto(ChannelHandlerContext ctx, AdvPacket msg, ByteBuf target) throws Exception 
	{
		target.writeBytes(msg.data);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AdvPacket msg) 
	{
		switch (FMLCommonHandler.instance().getEffectiveSide()) 
		{
		case CLIENT:
			AdvancedPowerManagement.instance.proxy.onPacketDataClient(source, ClientProxy.getPlayer());
			break;
		case SERVER:
			NetHandlerPlayServer netHandler = (NetHandlerPlayServer)(ctx.channel().attr(NetworkRegistry.NET_HANDLER).get());
			AdvancedPowerManagement.instance.proxy.onPacketDataClient(source, netHandler.playerEntity);
			break;
		}		
	}

	public static void sendToServer(AdvPacket packet)
	{
		AdvancedPowerManagement.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		AdvancedPowerManagement.channels.get(Side.CLIENT).writeOutbound(packet);
	}

	public static void sendToPlayer(AdvPacket packet, EntityPlayer player)
	{
		AdvancedPowerManagement.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		AdvancedPowerManagement.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		AdvancedPowerManagement.channels.get(Side.SERVER).writeOutbound(packet);
	}
/*
	public static void sendToAllPlayers(Packet packet)
	{
		AdvancedPowerManagement.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		AdvancedPowerManagement.channels.get(Side.SERVER).writeOutbound(packet);
	}	
*/
}