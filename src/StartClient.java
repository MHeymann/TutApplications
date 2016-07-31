import java.util.Scanner;
import client.*;

/**
 *
 * @author Murray Heymann
 */
public class StartClient {

	/* Some data structure for those currently online */

    public static void main(String[] args)  {
		String line = null;
		Thread thread = null;
		ChatClient client = new ChatClient("127.0.0.1", 8002);
		/*
		Scanner scanner = new Scanner(System.in);
		*/

		System.out.println("Starting up client");

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
