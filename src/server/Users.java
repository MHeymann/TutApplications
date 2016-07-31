package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.channels.*;


import packet.Packet;


/**
 *
 * @author Murray Heymann
 */
public class Users {

	/* Some data structure for those currently online */
	private HashMap<String, SocketChannel> names = null;
	private HashMap<SocketChannel, String> socketChannels = null;
	private ReentrantReadWriteLock hsProtect = null;


	public Users() {
		this.socketChannels = new HashMap<SocketChannel, String>();
		this.names = new HashMap<String, SocketChannel>();
		this.hsProtect = new ReentrantReadWriteLock(true);
	}
	
	public void sendPacket(Packet packet) {
		SocketChannel sc = null;
		this.hsProtect.writeLock().lock();
		
		if (!this.names.containsKey(packet.to)) {
			System.out.printf("Failed to send message\n");
			return;
		}

		sc = this.names.get(packet.to);
		try {
			Packet.sendPacket(packet, sc);
		} catch (IOException e) {
			System.out.printf("Users.class failed to deliver message]n");
			e.printStackTrace();
		}
		this.hsProtect.writeLock().unlock();
	}

	public void removeChannel(SocketChannel sc) {
		String n = null;
		if (sc == null) {
			return;
		}
		this.hsProtect.writeLock().lock();
		n = socketChannels.get(sc);
		if (n == null) {
			this.hsProtect.writeLock().unlock();
			System.out.println("Problems!");
			return;
		}
		socketChannels.remove(sc);
		names.remove(n);
		System.out.printf("User %s went offline\n", n);
		try {
			sc.close();
		} catch (Exception e) {
			System.out.printf("Exception when closing channel:  probably already closed\n");
		}
		this.hsProtect.writeLock().unlock();

	}

	public void removeName(String n)
	{
		SocketChannel sc = null;
		if (n == null) {
			return;
		}
		this.hsProtect.writeLock().lock();
		sc = names.get(n);
		if (sc == null) {
			this.hsProtect.writeLock().unlock();
			System.out.println("Problems!");
			return;
		}
		socketChannels.remove(sc);
		names.remove(n);
		try {
			sc.close();
		} catch (Exception e) {
			System.out.printf("Exception when closing channel:  probably already closed\n");
		}
		System.out.printf("User %s went offline\n", n);
		this.hsProtect.writeLock().unlock();
	}

	public void addConnection(String n, SocketChannel sc) {
		if (n == null) {
			return;
		}
		if (sc == null) {
			return;
		}
		System.out.printf("Adding\n");
		/*
		 * DONE:  add Lock 
		 */
		this.hsProtect.writeLock().lock();
		socketChannels.put(sc, n);
		names.put(n, sc);
		this.hsProtect.writeLock().unlock();
		System.out.printf("Added\n");
	}
   
}
