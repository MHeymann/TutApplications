import java.util.Scanner;
import client.*;
import packet.*;

/**
 *
 * @author Murray Heymann
 */
public class StartClient {

	/* Some data structure for those currently online */


    public static void main(String[] args)  {
		String line = null;
		Thread thread = null;
		ChatClient client = null;
		Scanner scanner = new Scanner(System.in);
		String name = null;

		System.out.println("Starting up client");
		System.out.printf("Please enter your username: ");
		name = scanner.nextLine();
		System.out.printf("Hello %s!  we will now try to log you onto the server\n", name);
		client = new ChatClient(name, "127.0.0.1", 8002);

		thread = new Thread(client);
		thread.start();

		System.out.println("Client running");

		/*
		while(true) {
			line = scanner.nextLine();
			if (line.equals("quit")) {
				break;
			} else {
				System.out.println(line);
			}
		}
		*/

		/*
		scanner.close();
		*/
		System.out.println("Main Thread Waiting to exit");
	
    }

}
