package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
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
public class ServerListener implements Runnable {

	private ChatServer masterServer = null;
	private boolean listenStatus = true;
	private ReadWriteLock listenLock = null;

	public ServerListener(ChatServer masterServer) {
		this.masterServer = masterServer;
		this.listenStatus = true;
		this.listenLock = new ReentrantReadWriteLock();
	}

	public void kill() {
		this.listenLock.writeLock().lock();
		listenStatus = false;
		this.listenLock.writeLock().unlock();
	}

    public void run() {
		try {
			connectionListen();
		} catch (IOException e) {
			System.out.println("IOException...");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found...");
		}
    }

	public boolean listening()
	{
		boolean status;
		this.listenLock.readLock().lock();
		status = listenStatus;
		this.listenLock.readLock().unlock();
		return status;
	}

    public void connectionListen() throws IOException, ClassNotFoundException {
		String name = null;
		Connection connect = null;
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		Packet packet = null;
		ServerSocket server = new ServerSocket(8002);
		Thread thread = null;

        while (this.listening()) {
            Socket socket = server.accept();

			System.out.println("New Incoming connection...");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Flag");

			packet = (Packet)in.readObject();
			name = packet.data;

			connect = new Connection(name, socket, in, out);
			name = null;
			socket = null;
			thread = new Thread(connect);
			connect.addThread(thread);
			this.masterServer.addConnection(connect);
			System.out.printf("About to start thread for Connection\n");
			thread.start();
			System.out.println("Connection set up");
			connect = null;
			thread = null;
        }

	}

}
