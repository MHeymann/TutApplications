package server;

import java.util.Scanner;
import server.Users;
import server.ServerListener;
import server.ServerSpeaker;

/**
 *
 * @author Murray Heymann
 */
public class ChatServer {

	/* Some data structure for those currently online */

    public static void main(String[] args)  {
		Thread listenThread = null;
		Thread speakThread = null;
		Users users = null;
		ServerSpeaker speaker = null;
		ServerListener listener = null;
		Scanner scanner = null;
		int[] ports;

		scanner = new Scanner(System.in);
		String line;
		ports = new int[1];
		ports[0] = 8002;

		System.out.println("Starting up server");
		users = new Users();
		speaker = new ServerSpeaker(users);
		listener = new ServerListener(ports, users, speaker);

		listenThread = new Thread(listener);
		listenThread.start();
		speakThread = new Thread(speaker);
		speakThread.start();

		System.out.println("Server running");

		while(true) {
			line = scanner.nextLine();
			if (line.equals("quit")) {
				break;
			} else if (line.equals("check")) {
				System.out.println("Server running");
			} else {
			}
		}

		scanner.close();
		listener.kill();
		System.out.println("I'm Here");
		System.exit(0);
	
    }

}
