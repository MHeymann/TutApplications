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
import java.io.Console;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import packet.*;
import packet.Serializer;
import java.util.Scanner;

/**
 *
 * @author 15988694
 */
public class ChatClient implements Runnable {
	
	private String name;
	private int connectionPort = -1;
	private String connectionAddress = null;
	private SocketChannel socketChannel = null;

	public void attachClientShutDownHook(SocketChannel socketChannel){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.printf("JVM Shutting down\n");
			}
		});
	}



    public ChatClient(String name, String connectionAddress, int connectionPort) {
		this.name = name;
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
			attachClientShutDownHook(this.socketChannel);
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
		Packet.sendPacket(packet, this.socketChannel);
	}

	public Packet receivePacket() throws ClassNotFoundException, IOException {
		Packet packet = null;

		packet = Packet.receivePacket(this.socketChannel);
		return packet;
		
	}

	public void run() {
		Packet packet = null;
		String line = null;
		Scanner scanner = new Scanner(System.in);
		byte[] bytes = null;
		ByteBuffer buffer = null;
		this.connect();

		System.out.printf("Please provide the password for %s: ", this.name);

		Console cons;
		char[] passwd;
		if ((cons = System.console()) != null &&
				(passwd = cons.readPassword()) != null) {
			line = new String(passwd);
			java.util.Arrays.fill(passwd, ' ');
		}

		/*
		*/
		System.out.printf("%s with password %s \n", this.name, line);
		packet = new Packet(Code.LOGIN, this.name, line, null);
		line = null;
		try {
			this.sendPacket(packet);
		} catch (Exception e) {
			System.out.println("Failed to send login details");
		}
		

		while (true) {
			line = scanner.nextLine();
			packet = null;
			if (line.equals("quit")) {
				packet = new Packet(Code.QUIT, this.name, null, null);
				try {
					this.sendPacket(packet);
				} catch (Exception e) {
					System.out.println("Failed to send Offline signal");
				}
				break;
			}
			packet = new Packet(Code.SEND, this.name, line, this.name);
			line = null;

			try {
				this.sendPacket(packet);
			} catch (Exception e) {
				System.out.println("Sending Failed");
				e.printStackTrace();
			}
			packet = null;

			while (packet == null) {
				try {
					packet = this.receivePacket();
					System.out.println(packet.data);
				} catch (Exception e) {
					/*
					System.out.println("Receiving Failed");
					e.printStackTrace();
					*/
				}
			}
		}
		scanner.close();
		try {
			this.socketChannel.close();
			this.socketChannel = null;
		} catch (Exception e) {
			System.out.println("Somehow doesn't want to close socketChannel");
		}

	}
}
