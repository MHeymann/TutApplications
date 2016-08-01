package client;

import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import packet.*;


public class ClientListener implements Runnable {

	private SocketChannel socketChannel = null;

	public ClientListener(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
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
	public void go() throws Exception 
	{
		SocketChannel sc = null;
		Selector selector = null;
		SelectionKey key = null;
		Set selectedKeys = null;
		Iterator it = null;
		SelectionKey newKey = null;
		Packet packet = null;
		int num = -1;

		selector = Selector.open();
		newKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
		while (true) {
			num = selector.select();

			selectedKeys = null;
			selectedKeys = selector.selectedKeys();
			it = null;
			it = selectedKeys.iterator();

			while (it.hasNext()) {
				key = null;
				key = (SelectionKey)it.next();
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
						System.out.println("Server went offline\n");
						sc.close();
					} else if (packet.code == Code.SEND) {
						System.out.printf("\b\b%s: %s\n> ", packet.name, packet.data);
					} else if (packet.code == Code.ECHO) {
						System.out.printf("\b\bYOU echoed: %s\n> ", packet.data);
					} else if (packet.code == Code.BROADCAST) {
						System.out.printf("\b\b%s Broadcast: %s\n> ", packet.name, packet.data);
					} else {
						System.out.printf("This is weird behaviour by the server\n");
					}
				}
				it.remove();
			}
		}
	}
}
