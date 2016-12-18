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

	public void pushUserList() {
		Packet packet = null;
		Set<String> names = null;

		names = users.getNames();
		for (String to: names) {
			packet = null;
			packet = new Packet(Code.GET_ULIST, to, null, to);
			packet.setUserList(names);
			this.addPacketToQueue(packet);
		}
	}

	public void broadcast(Packet packet) {
		Packet copy = null;
		Set<String> names = null;

		names = users.getNames();

		System.out.printf("%s is broadcasting %s\n", packet.name, packet.data);
		for(String to: names) {
			System.out.printf("%s to be added for broadcasting\n", to);
			copy = null;
			copy = new Packet(packet.code, packet.name, packet.data, to);
			/*
			if (packet.code == Code.GET_ULIST) {
				copy.setUserList(names);
			}
			*/
			this.addPacketToQueue(copy);
		}
	}

	public void run() {
		Packet packet = null;
		Set<String> onlineUsers = null;
		while(true) {
			try {
				this.sem.acquire();
			} catch (InterruptedException e) {
				System.out.printf("Speaker interrupted\n");
				break;
			}
			packet = null;
			packet = q.poll();
			if (packet.code == Code.SEND) {
				System.out.printf("Sending message: %s -> %s %s\n", packet.name, packet.to, packet.data);
			} else if (packet.code == Code.GET_ULIST) {
				onlineUsers = null;
				if (packet.users == null) {
					onlineUsers = users.getNames();
					packet.setUserList(onlineUsers);
				}
				if (packet.to == null) {
					/* only when an individual asked, not when being broadcast */
					packet.to = packet.name;
				} else {
					System.out.println(packet.to + " " + packet.to.length());
				}
				packet.name = null;
				System.out.printf("Sending list of online users to %s\n", packet.to);
			}
			this.users.sendPacket(packet);
			packet = null;
		}
	}
}
