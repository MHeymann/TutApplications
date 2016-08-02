package packet;

import java.io.Serializable;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*
 * Author: Murray Heymann 
 * 2016
 *
 * Packet is a data structure for sending data over a network.
 *
 * HAMILTON
 * Where are you taking me?
 *
 * ANGELICA
 * I'm about to change your life.
 *
 * HAMILTON
 * Then by all means, lead the way.
 * --> Helpless, Hamilton
 */

public class Packet implements Serializable {

    public int code;
	public String name;
    public String data;
	public String to;
	public Set<String> users;

    
    public Packet(int code, String name, String data, String to) {
		this.code = code;
		this.name = name;
		this.data = null;
		this.to = null;
		this.users = null;

		switch (code) {
			case Code.QUIT:
			case Code.GET_ULIST:
				break;
			case Code.SEND:
			case Code.BROADCAST:
				this.to = to;
			case Code.ECHO:
			case Code.LOGIN:
				this.data = data;
				break;
			default:
				System.err.printf("Invalid code upon creating packet.\n");
				break;
		}

    }
    
	public void setUserList(Set<String> users) {
		this.users = users;
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
		try {
			r = socketChannel.read(buffer);
		} catch (IOException e) {
			System.out.printf("io exceoption, user probably went offline in a rogue fassion\n");
			e.printStackTrace();
		}
		if (r == 0) {
			/* TODO 
			 * this is currently handled as user going offline.  is this still
			 * valid?
			 * */

			System.err.printf("This shouldn't happen!!!!!\n");
			System.err.printf("the buffer is empty for some reason.  \n");
			System.err.printf("****************************************************** \n");
			return null;
		}
		if (r == -1) {
			socketChannel.close();
			socketChannel = null;
			return null;
		}

		packetSize = buffer.getInt(0);
		/*
		System.out.printf("%d bytes\n", packetSize);
		*/
		if (packetSize <= 0) {
			/* TODO 
			 * this is currently handled as user going offline.  is this still
			 * valid?
			 * */
			return null;
		}
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
