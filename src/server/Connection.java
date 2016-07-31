package server;

import java.io.*;
import java.nio.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.lang.Thread;

import packet.Packet;

/**
 *
 * @author Murray Heymann
 */
public class Connection implements Runnable {

	private String name = null;
	private SocketChannel socketChannel = null;
	private boolean listenStatus = true;
	private Thread thread = null;
	private ReadWriteLock listenLock = null;

	public Connection(String name, SocketChannel socketChannel) {
		this.name = name;
		this.socketChannel = socketChannel;
		this.listenStatus = true;
		this.listenLock = new ReentrantReadWriteLock();
	}

	public void run() {
		Packet packet = null;
		if (thread == null) {
			return;
		}
		System.out.printf("Starting %s\n", this.name);
		while (this.listening()) {
			try {
				packet = receivePacket();
			} catch (Exception e) {
				break;
			}

			try {
				out.writeObject(packet);
				out.flush();
			} catch (IOException e) {
				System.out.println("IOException in Connection while trying to relay messages");
			}

		}
	}

	public Packet receivePacket() throws ClassNotFoundException, IOException {
		Packet packet;
        packet = (Packet)this.in.readObject();
		return packet;
	}


	public void kill() {
		this.listenLock.writeLock().lock();
		listenStatus = false;
		this.listenLock.writeLock().unlock();
	}
	public boolean listening()
	{
		boolean status;
		this.listenLock.readLock().lock();
		status = listenStatus;
		this.listenLock.readLock().unlock();
		return status;
	}

   
   public String getName() {
	   return this.name;
   }

   public void setName(String name) {
	   this.name = name;
   }

   public Socket getSocket() {
	   return this.socket;
   }

   public void setSocket(Socket socket) {
	   this.socket = socket;
   }

   public Thread getThread() {
	   return this.thread;
   }

   public void setThread(Thread thread) {
	   this.thread = thread;
   }

}
