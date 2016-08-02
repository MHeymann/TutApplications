package server;

import java.util.Scanner;
import server.Users;
import server.ServerListener;
import server.ServerSpeaker;

/*
 * Author Murray Heymann
 *
 * This is the class that starts up the server's threads for listening (a so
 * called 'listener') for incoming data, processing it and possibly 
 * relaying it to another user through the so called 'speaker' thread.
 *
 * "I dreamed I saw eleven stars, the sun the moon and sky
 * Bowing down before my star it made me wonder why."
 * Joseph and his amazing technicolour dreamcoat.
 *
 *
 * "What is even an IDE?"
 * --> Murray Heymann, or maybe someone else before me... how must I know
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
