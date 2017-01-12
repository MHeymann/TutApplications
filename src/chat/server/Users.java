package chat.server;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.nio.channels.*;

import chat.packet.Packet;

/*
 * author Murray Heymann
 *
 * This data structure that is used to keep track of who is currently online.  
 * If a database used for keeping known users were added, it would be loaded
 * into a further data structure in here.
 *
 * "Look around, ook around, look at where your are!!"
 * That Would Be Enough, Hamilton
 */
public class Users {

	/* Some data structure for those currently online */
	private HashMap<String, SocketChannel> names = null;
	private HashMap<SocketChannel, String> socketChannels = null;
	/*
	private HashSet<Connection> userSet = null;
	*/
	private ReentrantLock hsProtect = null;

	public Users() {
		this.socketChannels = new HashMap<SocketChannel, String>();
		this.names = new HashMap<String, SocketChannel>();
		/*
		this.userSet = new HashSet<Connection>();
		*/
		this.hsProtect = new ReentrantLock(true);
	}

	public Set<String> getNames () {
		Set<String> nameSet = new HashSet<String>();
		this.hsProtect.lock();
		
		for(String s: names.keySet()) {
			nameSet.add(s);
			System.out.printf("%s added to names for broadcasting\n", s);
		}
		this.hsProtect.unlock();
		return nameSet;
	}
	
	public void sendPacket(Packet packet) {
		SocketChannel sc = null;
		this.hsProtect.lock();
		
		if (!this.names.containsKey(packet.to)) {
			System.out.printf("Failed to send message\n");
			System.out.printf("meep %s\n", packet.to);
			this.hsProtect.unlock();
			return;
		}

		sc = this.names.get(packet.to);
		try {
			Packet.sendPacket(packet, sc);
		} catch (IOException e) {
			System.out.printf("Users.class failed to deliver message]n");
			e.printStackTrace();
		}
		this.hsProtect.unlock();
	}

	public void removeChannel(SocketChannel sc) {
		String n = null;
		if (sc == null) {
			return;
		}
		this.hsProtect.lock();
		n = socketChannels.get(sc);
		if (n == null) {
			this.hsProtect.unlock();
			System.out.println("Problems!");
			return;
		}
		socketChannels.remove(sc);
		names.remove(n);
		System.out.printf("User '%s' went offline, %d still online\n", n, socketChannels.size());
		try {
			sc.close();
		} catch (Exception e) {
			System.out.printf("Exception when closing channel:  probably already closed\n");
		}
		this.hsProtect.unlock();

	}

	public void removeName(String n)
	{
		SocketChannel sc = null;
		if (n == null) {
			return;
		}
		this.hsProtect.lock();
		sc = names.get(n);
		if (sc == null) {
			this.hsProtect.unlock();
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
		System.out.printf("User '%s' went offline, %d still online\n", n, names.size());
		this.hsProtect.unlock();
	}

	public boolean addConnection(String n, SocketChannel sc) {
		boolean status;
		if (n == null) {
			return false;
		}
		if (sc == null) {
			return false;
		}
		/*
		 * DONE:  add Lock 
		 */
		this.hsProtect.lock();

		status = (!this.names.containsKey(n) && !this.socketChannels.containsKey(sc));
		if (status) {
			this.socketChannels.put(sc, n);
			this.names.put(n, sc);
		}

		this.hsProtect.unlock();
		if (status) {
			System.out.printf("%s connected\n", n);
		}
		return status;
	}
   
}
