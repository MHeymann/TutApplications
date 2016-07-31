import java.util.Scanner;
import server.Users;
import server.ServerListener;

/**
 *
 * @author Murray Heymann
 */
public class StartServer {

	/* Some data structure for those currently online */

    public static void main(String[] args)  {
		Thread thread = null;
		Users users = null;
		ServerListener listener = null;
		Scanner scanner = null;
		int[] ports;

		scanner = new Scanner(System.in);
		String line;
		ports = new int[1];
		ports[0] = 8002;

		System.out.println("Starting up server");
		users = new Users();
		listener = new ServerListener(ports);

		listener.setUsers(users);

		thread = new Thread(listener);
		thread.start();

		System.out.println("Server running");

		while(true) {
			line = scanner.nextLine();
			if (line.equals("quit")) {
				break;
			} else if (line.equals("check")) {
				System.out.println("Server running");
			} else {
				System.out.println(line);
			}
		}

		scanner.close();
		listener.kill();
		System.out.println("I'm Here");
	
    }

}
