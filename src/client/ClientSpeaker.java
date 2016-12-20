package client;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import packet.*;
import packet.Serializer;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

/*
 * Author Murray Heymann
 *
 * This class runs a thread that sends data to the server over the 
 * network.  
 *
 * "In my life
 * She has burst like the music of angels
 * The light of the sun
 * And my life seems to stop
 * As if something is over
 * And something has scarcely begun."
 * In My Life, Les Miserables
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
		} catch (IOException e) {
			System.out.printf("IOException:  failed to create SocketChannel\n");
			return false;
		}
		
		System.out.println("opened new channel: " + this.socketChannel);
		return true;
    }

	public void getOnlineNames() {
		Packet packet = new Packet(Code.GET_ULIST, this.name, null, null);
		if (!this.sendPacket(packet)) {
			System.err.printf("Couldn't request userlist\n");
		}
	}

	public boolean sendString(String s, String to) {
		Packet packet = new Packet(Code.SEND, this.name, s, to);
		return this.sendPacket(packet);
	}


	public boolean echoString(String s) {
		Packet packet = new Packet(Code.ECHO, this.name, s, null);
		return this.sendPacket(packet);
	}

	public boolean broadcastString(String s) {
		Packet packet = new Packet(Code.BROADCAST, this.name, s, null);
		return this.sendPacket(packet);
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
		Selector selector = null;

		System.out.printf("Connecting\n");
		if (!this.connect()) {
			showMessageDialog(null, "Failed to connect to server");
			System.err.printf("Failed to connect.  Couldn't login\n");
			return false;
		}
		System.out.printf("Connected\n");

		try {
			selector = Selector.open();
		} catch (Exception e) {
		}

		try {
			this.socketChannel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
		}

		packet = new Packet(Code.LOGIN, this.name, password, null);

		System.out.printf("Sending log in packet\n");
		if(!this.sendPacket(packet)) {
			System.out.println("Failed to send login details");
			return false;
		}


		do {
			try {
				selector.select();
			} catch (Exception e) {
			}
			try {
				packet = Packet.receivePacket(this.socketChannel);
			} catch (Exception e) {
			}

			if (packet == null) {
				System.err.printf(
						"No packet received while awaiting login info, ClientSpeaker.c\n");
				return false;
			}

			
		} while (packet.code != Code.LOGIN);

		if (packet.data == null) {
			System.err.printf("No data received\n");
			return false;
		}

		if (packet.data.equals("accept")) {
			attachClientShutDownHook(this.socketChannel);
			return true;
		} else {
			return false;
		}
		
	}

	public boolean logoff() 
	{
		Packet packet = null;
		packet = new Packet(Code.QUIT, this.name, null, null);
		if(!this.sendPacket(packet)) {
			System.err.println("Failed to send Offline signal");
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
			} else if (line.equals("getlist")) {
				this.getOnlineNames();
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
