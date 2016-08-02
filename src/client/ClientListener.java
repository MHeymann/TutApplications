package client;

import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import packet.*;


public class ClientListener implements Runnable {

	private SocketChannel socketChannel = null;
	private ChatClient guiClient = null;

	public ClientListener(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.guiClient = null;
	}

	public ClientListener(SocketChannel socketChannel, ChatClient guiClient) {
		this.socketChannel = socketChannel;
		this.guiClient = guiClient;
	}

	public void run() 
	{
		try {
			go();
		} catch (Exception e) {
			System.out.printf("Exception while listening\n");
			e.printStackTrace();
			return;
		}
	}

	private void outputString(String s) {
		if (guiClient != null) {
			this.guiClient.append(s);
		} else {
			System.out.printf("%s> ", s);
		}
	}

	public void go() throws Exception 
	{
		SocketChannel sc = null;
		Selector selector = null;
		SelectionKey key = null;
		Set<SelectionKey> selectedKeys = null;
		Iterator<SelectionKey> it = null;
		SelectionKey newKey = null;
		Packet packet = null;
		int num = -1;
		String output = null;


		selector = Selector.open();
		newKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
		selector.select();
		while (true) {
			num = selector.select();

			selectedKeys = null;
			selectedKeys = selector.selectedKeys();
			it = null;
			it = selectedKeys.iterator();

			while (it.hasNext()) {
				key = null;
				key = it.next();
				if ((key.readyOps() & SelectionKey.OP_READ)
						== SelectionKey.OP_READ) {
					/* Read the data */
					sc = null;
					sc = (SocketChannel)key.channel();
					packet = null;
					packet = Packet.receivePacket(sc);

					/* Process data */
					if (packet == null) {
						/* server is offline */
						output = String.format("Server went offline\n");
						outputString(output);
						sc.close();
					} else if (packet.code == Code.SEND) {
						output = String.format("%s: %s\n", packet.name, packet.data);
						outputString(output);
					} else if (packet.code == Code.ECHO) {
						output = String.format("YOU echoed: %s\n", packet.data);
						outputString(output);
					} else if (packet.code == Code.BROADCAST) {
						output = String.format("%s Broadcast: %s\n", packet.name, packet.data);
						outputString(output);
					} else if (packet.code == Code.GET_ULIST) {
						if (guiClient != null) {
							guiClient.showOnlineUsers(packet.users);
						} else {
							System.out.printf("Online Users:\n");
							for (String s: packet.users) {
								System.out.printf("%s\n", s);
							}
						}
					} else {
						output = String.format("This is weird behaviour by the server\n");
						outputString(output);
					}
				}
				it.remove();
			}
		}
	}
}
