package server;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.lang.Thread;
*/

import packet.Packet;

/**
 *
 * @author Murray Heymann
 */
public class Connection {

	private String name = null;
	private SocketChannel socketChannel = null;

	public Connection(String name, SocketChannel socketChannel) {
		this.name = name;
		this.socketChannel = socketChannel;
	}
	

	public Packet receivePacket() throws ClassNotFoundException, IOException 
	{
		Packet packet = null;
        packet = Packet.receivePacket(this.socketChannel);
		return packet;
	}

	public void sendPacket(Packet packet) throws ClassNotFoundException, IOException 
	{
        Packet.sendPacket(packet, this.socketChannel);
	}

   public String getName() 
   {
	   return this.name;
   }

   public void setName(String name) 
   {
	   this.name = name;
   }

   public SocketChannel getSocketChannel() 
   {
	   return this.socketChannel;
   }

   public void setSocketChannel(SocketChannel socketC) 
   {
	   this.socketChannel = socketC;
   }

}
