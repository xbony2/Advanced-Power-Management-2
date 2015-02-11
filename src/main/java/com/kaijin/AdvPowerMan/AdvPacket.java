package com.kaijin.AdvPowerMan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class AdvPacket
{
	byte[] data;

	public AdvPacket () {}

	public AdvPacket(byte[] packet) 
	{
		this.data = packet.clone();
	}
}