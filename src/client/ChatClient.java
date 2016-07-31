/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/*
import com.sun.corba.se.pept.encoding.InputObject;
import java.io.BufferedReader;
*/
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import packet.Packet;
import packet.Serializer;
import java.util.Scanner;

/**
 *
 * @author 15988694
 */
public class ChatClient implements Runnable {
	
	private int connectionPort = -1;
	private String connectionAddress = null;
	private ByteBuffer internalBuffer = null;
	private SocketChannel socketChannel = null;

    public ChatClient(String connectionAddress, int connectionPort) {
		this.internalBuffer = ByteBuffer.allocate(1024);
		this.connectionPort = connectionPort;
		this.connectionAddress = connectionAddress;
		this.socketChannel = null;
    }

    public void connect() {
		try {
			this.socketChannel = SocketChannel.open();
			this.socketChannel.configureBlocking(false);
			this.socketChannel.connect(new InetSocketAddress(this.connectionAddress, this.connectionPort));
			while (!this.socketChannel.finishConnect());
		} catch (IOException e) {
			System.out.printf("IOException:  failed to create SocketChannel\n");
		}
		if (this.socketChannel == null) {
			System.out.println("Whats going on?");
		} else {
			System.out.println("opened new channel: " + this.socketChannel);
		}
    }

	public void sendPacket(Packet packet) throws IOException 
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
		this.socketChannel.write(srcs);
		/*
		this.socketChannel.write(sizeBuffer);
		this.socketChannel.write(buffer);
		*/
	}

	public Packet receivePacket() throws ClassNotFoundException, IOException {
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
			this.socketChannel.close();
			this.socketChannel = null;
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
			this.socketChannel.close();
			this.socketChannel = null;
			return null;
		}
		byteArr = buffer.array();

        packet = (Packet)Serializer.deserialize(byteArr);;
		return packet;
	}

	public void run() {
		Packet packet = null;
		String line = null;
		Scanner scanner = new Scanner(System.in);
		byte[] bytes = null;
		ByteBuffer buffer = null;
		this.connect();

		while (true) {
			line = scanner.nextLine();
			if (line.equals("quit")) {
				break;
			}
			packet = new Packet(1, line);
			line = null;

			try {
				this.sendPacket(packet);
			} catch (Exception e) {
				System.out.println("Sending Failed");
			}
			packet = null;

			while (packet == null) {
				try {
					packet = this.receivePacket();
					System.out.println(packet.data);
				} catch (Exception e) {
					/*
					System.out.println("Receiving Failed");
					*/
				}
			}
		}
		scanner.close();
		try {
			this.socketChannel.close();
		} catch (Exception e) {
			System.out.println("Somehow doesn't want to close socketChannel");
		}

	}
}
