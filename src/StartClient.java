import java.util.Scanner;
import client.*;
import packet.*;
import java.io.Console;

/**
 *
 * @author Murray Heymann
 */
public class StartClient {

	/* Some data structure for those currently online */

    public static void main(String[] args)  {
		String line = null;
		String name = null;
		Thread threadSpeaker = null;
		Thread threadListen = null;
		ClientSpeaker speaker = null;
		ClientListener listener = null;
		Scanner scanner = new Scanner(System.in);

		System.out.println("Starting up client");
		System.out.printf("Please enter your username: ");
		name = scanner.nextLine();
		speaker = new ClientSpeaker(name, "127.0.0.1", 8002);
		speaker.setGui(false);

		
		System.out.printf("Please provide the password for %s: ", name);

		Console cons;
		char[] passwd;
		if ((cons = System.console()) != null &&
				(passwd = cons.readPassword()) != null) {
			line = new String(passwd);
			java.util.Arrays.fill(passwd, ' ');
		}

		System.out.printf("%s with password %s \n", name, line);
		if (!speaker.login(line)) {
			return;
		}
		line = null;

		listener = new ClientListener(speaker.getSocketChannel());

		threadSpeaker = new Thread(speaker);
		threadListen = new Thread(listener);
		threadSpeaker.start();
		threadListen.start();
    }

}
