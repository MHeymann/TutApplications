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
import packet.*;
import packet.Serializer;
import java.util.Scanner;

/**
 *
 * @author 15988694
 */
public class ClientSpeaker implements Runnable {
	
	private String name;
	private int connectionPort = -1;
	private String connectionAddress = null;
	private SocketChannel socketChannel = null;
	private boolean gui = false;

	public void attachClientShutDownHook(SocketChannel socketChannel){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.printf("JVM Shutting down\n");
			}
		});
	}

    public ClientSpeaker(String name, String connectionAddress, int connectionPort, boolean gui) {
		this.name = name;
		this.connectionPort = connectionPort;
		this.connectionAddress = connectionAddress;
		this.socketChannel = null;
		this.gui = gui;
	}

    public ClientSpeaker(String name, String connectionAddress, int connectionPort) {
		this.name = name;
		this.connectionPort = connectionPort;
		this.connectionAddress = connectionAddress;
		this.socketChannel = null;
		this.gui = true;
    }

	public void setGui(boolean gui) {
		this.gui = gui;
	}

    public boolean connect() {
		try {
			this.socketChannel = SocketChannel.open();
			this.socketChannel.configureBlocking(false);
			this.socketChannel.connect(new InetSocketAddress(this.connectionAddress, this.connectionPort));
			while (!this.socketChannel.finishConnect());
			attachClientShutDownHook(this.socketChannel);
		} catch (IOException e) {
			System.out.printf("IOException:  failed to create SocketChannel\n");
			return false;
		}
		
		System.out.println("opened new channel: " + this.socketChannel);
		return true;
    }

	public boolean sendPacket(Packet packet)
	{
		try {
			Packet.sendPacket(packet, this.socketChannel);
		} catch (IOException e) {
			System.err.printf("Error sending packet\n");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	public Packet receivePacket() throws ClassNotFoundException, IOException {
		Packet packet = null;

		packet = Packet.receivePacket(this.socketChannel);
		return packet;
		
	}

	public boolean login(String password) 
	{
		Packet packet = null;

		if (!this.connect()) {
			System.err.printf("Failed to connect.  Couldn't login\n");
			return false;
		}

		packet = new Packet(Code.LOGIN, this.name, password, null);

		if(!this.sendPacket(packet)) {
			System.out.println("Failed to send login details");
			return false;
		}
		return true;
	}

	public boolean logoff() 
	{
		Packet packet = null;
		packet = new Packet(Code.QUIT, this.name, null, null);
		if(!this.sendPacket(packet)) {
			System.out.println("Failed to send Offline signal");
			return false;
		}
		return true;
	}

	public void run() {
		if (gui) {
			init_gui();
		} else {
			go();
		}
	}

	private void init_gui()
	{
	}

	private void go() {
		Packet packet = null;
		String line = null;
		String to = null;
		Scanner scanner = new Scanner(System.in);
		byte[] bytes = null;
		ByteBuffer buffer = null;

		System.out.printf("> ");
		while (scanner.hasNextLine()) {
			line = null;
			line = scanner.nextLine();
			packet = null;
			if (line.equals("quit") || line.equals("exit")) {
				break;
			} else if (line.equals("send")) {
				System.out.printf("[recipient] > ");
				to = scanner.nextLine();
				System.out.printf("[ message ] > ");
				line = scanner.nextLine();
				packet = new Packet(Code.SEND, this.name, line, to);
			} else if (line.equals("echo")) {
				System.out.printf("[ message ] > ");
				line = scanner.nextLine();
				packet = new Packet(Code.ECHO, this.name, line, null);
			} else if (line.equals("broadcast")) {
				System.out.printf("[ message ] > ");
				line = scanner.nextLine();
				packet = new Packet(Code.BROADCAST, this.name, line, null);
			} else {
				System.out.printf("> ");
				continue;
			}
			line = null;
			to = null;

			if(!this.sendPacket(packet)) {
				System.out.println("Sending Failed");
			}
			packet = null;
			
			System.out.printf("> ");
		}

		if (!this.logoff()) {
			System.out.printf("well, this is weird\n");
		}
		scanner.close();
		try {
			this.socketChannel.close();
			this.socketChannel = null;
		} catch (Exception e) {
			System.out.println("Somehow doesn't want to close socketChannel");
		}

		System.exit(0);
	}
}
