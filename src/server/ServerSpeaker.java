package server;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.Iterator;
import java.util.*;

import packet.Packet;
import packet.Code;

public class ServerSpeaker implements Runnable {
	private Users users = null;
	private ConcurrentLinkedQueue<Packet> q = null;
	private Semaphore sem = null;

	public ServerSpeaker(Users users) {
		this.users = users;
		this.q = new ConcurrentLinkedQueue<Packet>();
		this.sem = new Semaphore(0);
	}

	public void addPacketToQueue(Packet packet) 
	{
		this.q.add(packet);
		this.sem.release();
	}

	public void broadcast(Packet packet) {
		Packet copy = null;
		Set<String> names = users.getNames();	
		System.out.printf("Broadcasting\n");
		for(String to: names) {
			System.out.printf("%s to be added for broadcasting\n", to);
			copy = null;
			copy = new Packet(Code.BROADCAST, packet.name, packet.data, to);
			this.addPacketToQueue(copy);
		}
	}

	public void run() {
		Packet packet = null;
		Set<String> onlineUsers = null;
		while(true) {
			try {
				System.out.println("acquiring sem");
				this.sem.acquire();
			} catch (InterruptedException e) {
				System.out.printf("Speaker interrupted\n");
				break;
			}
			packet = null;
			packet = q.poll();
			if (packet.code == Code.SEND) {
				System.out.printf("Sending message: %s -> %s\n", packet.name, packet.to);
			} else if (packet.code == Code.GET_ULIST) {
				System.out.printf("Sending list of online users to %s\n", packet.name);
				onlineUsers = null;
				onlineUsers = users.getNames();
				packet.setUserList(onlineUsers);
				packet.to = packet.name;
				packet.name = null;
			}
			this.users.sendPacket(packet);
			packet = null;
		}
	}
}
