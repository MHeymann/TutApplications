// $Id$
package chat.server;

import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import chat.packet.*;

/*
 * Author: Murray Heymann
 *
 * This class runs a thread that listen's for incoming connections and incoming
 * messages to be relayed. 
 *
 * “Such a blunder/ Sometimes it makes me wonder/ why I even bring the thunder.”
 * Cabinet Batle No.1, Hamilton
 */

public class ServerListener implements Runnable
{
	private int ports[];
	private boolean runningStatus = true;
	private ReadWriteLock statusLock = null;
	private Users users = null;
	private ServerSpeaker speaker = null;

	public ServerListener(int ports[], Users users, ServerSpeaker speaker) {
		this.ports = ports;
		this.runningStatus = true;
		this.statusLock = new ReentrantReadWriteLock();
		this.users = users;
		this.speaker = speaker;
	}

	public void run() {
		try {
			go();
		} catch (Exception e) {
			System.out.printf("IOException on running listener\n");
			e.printStackTrace();
			return;
		}
	}

	public void kill() {
		this.statusLock.writeLock().lock();
		runningStatus = false;
		this.statusLock.writeLock().unlock();
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public boolean running() 
	{
		boolean status = false;

		this.statusLock.readLock().lock();
		status = runningStatus;
		this.statusLock.readLock().unlock();

		return status;
	}

	private void go() throws Exception
	{
		int i = 0;
		ServerSocketChannel serverSocketChannel = null;
		Selector selector = null;
		ServerSocket ss = null;
		InetSocketAddress address = null;
		SelectionKey key = null;
		Set<SelectionKey> selectedKeys = null;
		Iterator<SelectionKey> it = null;
		SocketChannel sc = null;
		Packet packet = null;

		if (this.users == null) {
			System.err.printf("users not initialized]n");
			System.exit(3);
		}

		// Create a new selector
		selector = Selector.open();

		// Open a listener on each port, and register each one
		// with the selector
		for (i = 0; i < ports.length; i++) {
			serverSocketChannel = null;
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);

			ss = null;
			ss = serverSocketChannel.socket();

			address = null;
			address = new InetSocketAddress(ports[i]);
			ss.bind(address);

			key = null;
			key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.printf( "Going to listen on %d\n", ports[i] );
		}

		while (this.running()) {
			selector.select();

			selectedKeys = null;
			selectedKeys = selector.selectedKeys();
			it = null;
			it = selectedKeys.iterator();

			while (it.hasNext()) {
				if (!this.running()) {
					break;
				}
				key = null;
				key = it.next();

				if ((key.readyOps() & SelectionKey.OP_ACCEPT)
						== SelectionKey.OP_ACCEPT) {
					/* Accept the new connection */
					serverSocketChannel = null;
					serverSocketChannel = (ServerSocketChannel)key.channel();

					sc = null;
					sc = serverSocketChannel.accept();
					sc.configureBlocking(false);

					/* Add the new connection to the selector */
					sc.register(selector, SelectionKey.OP_READ);
					it.remove();

					System.out.printf("Got connection from %s\n", sc.toString());
				} else if ((key.readyOps() & SelectionKey.OP_READ)
						== SelectionKey.OP_READ) {
					/* Read the data */
					sc = null;
					sc = (SocketChannel)key.channel();
					packet = null;
					System.out.printf("Receiving packet\n");
					packet = Packet.receivePacket(sc);
					System.out.printf("Received packet\n");
					

					/* Process data */
					if (packet == null) {
						users.removeChannel(sc);
						speaker.pushUserList();
						
						/* doubly redundant */
						sc.close();
					} else if (packet.code == Code.QUIT) {
						users.removeName(packet.name);
						speaker.pushUserList();
						packet = null;
						/* redundant */
						sc.close();
					} else if (packet.code == Code.SEND) {
						this.speaker.addPacketToQueue(packet);
					} else if (packet.code == Code.ECHO) {
						Packet.sendPacket(packet, sc);
					} else if (packet.code == Code.BROADCAST) {
						this.speaker.broadcast(packet);
					} else if (packet.code == Code.LOGIN) {
						Packet p;
						if (this.checkUserPassword(packet.name, packet.data) && 
								this.users.addConnection(packet.name, sc)) {
							p = new Packet(Code.LOGIN, "admin", "accept", packet.name);
							Packet.sendPacket(p, sc);
							speaker.pushUserList();
						} else {
							p = new Packet(Code.LOGIN, "admin", "denial", packet.name);
							Packet.sendPacket(p, sc);
							sc.close();
						}
					} else if (packet.code == Code.GET_ULIST) {
						this.speaker.addPacketToQueue(packet);
					}
				
					it.remove();
				}
			}
		}
	}

	private boolean checkUserPassword(String name, String pw) {
		return true;
	}

	public static void main( String args[] ) throws Exception {
		ServerListener listener = null;
		ServerSpeaker speaker = null;
		Thread thread = null;
		Users users = null;
		if (args.length <= 0) {
			System.err.printf("Usage: java ServerListener port [port port ...]\n");
			System.exit(1);
		}

		int ports[] = new int[args.length];

		for (int i=0; i<args.length; ++i) {
			ports[i] = Integer.parseInt( args[i] );
		}

		users = new Users();
		speaker = new ServerSpeaker(users);
		listener = new ServerListener(ports, users, speaker);
		thread = new Thread(listener);
		thread.start();
	}
}
