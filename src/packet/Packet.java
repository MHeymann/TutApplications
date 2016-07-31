/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import java.io.Serializable;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 *
 * @author 15988694
 */

public class Packet implements Serializable {

    public int code;
    public String data;

    
    public Packet(int code, String data) {
        this.code = code;
        this.data = data;
    }
    

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

	public static void sendPacket(Packet packet, SocketChannel socketChannel) throws IOException 
	{
		int size;
		ByteBuffer buffer = null;
		ByteBuffer sizeBuffer = null;
		ByteBuffer[] srcs = new ByteBuffer[2];
		byte[] byteArray = null;
		
		byteArray = Serializer.serialize(packet);

		sizeBuffer = ByteBuffer.allocate(4);

		size = byteArray.length;
		sizeBuffer.putInt(size);

		buffer = ByteBuffer.wrap(byteArray);

		sizeBuffer.flip();
		srcs[0] = sizeBuffer;
		srcs[1] = buffer;
		socketChannel.write(srcs);
	}

	public static Packet receivePacket(SocketChannel socketChannel) throws ClassNotFoundException, IOException {
		final int intSize = 4;
		int r = -1;
		int packetSize = -1;
		ByteBuffer buffer = null;
		byte[] byteArr = null;
		Packet packet = null;
		
		
		buffer = ByteBuffer.allocate(intSize);
		buffer.clear();
		r = -1;
		r = socketChannel.read(buffer);
		if (r == 0) {
			return null;
		}
		if (r == -1) {
			socketChannel.close();
			socketChannel = null;
			return null;
		}

		packetSize = buffer.getInt(0);
		if (packetSize <= 0) {
			return null;
		}
		System.out.printf("%d bytes\n", packetSize);
		buffer = null;
		buffer = ByteBuffer.allocate(packetSize);

		r = -1;
		r = socketChannel.read(buffer);
		if (r == 0) {
			return null;
		}
		if (r == -1) {
			socketChannel.close();
			socketChannel = null;
			return null;
		}
		byteArr = buffer.array();

        packet = (Packet)Serializer.deserialize(byteArr);;
		return packet;
	}
}
