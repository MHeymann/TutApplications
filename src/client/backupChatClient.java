/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/*
import com.sun.corba.se.pept.encoding.InputObject;
import java.io.BufferedReader;
*/
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import packet.Packet;
import java.util.Scanner;

/**
 *
 * @author 15988694
 */
public class ChatClient {
	private Socket socket = null;
	private Packet packet = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;

    public void connect() throws IOException {
        this.socket = new Socket("127.0.0.1", 8002);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
		packet = null;
    }

    public void ChatClient() {
        socket	= null;
        in		= null;
		out		= null;
		packet	= null;
    }

	public Packet receivePacket() throws ClassNotFoundException, IOException {
        packet = (Packet)in.readObject();
		return packet;
	}

	public static void main(String args[]) {
		Packet packet = null;
		ChatClient client;
		client = new ChatClient();
		String line = null;
		Scanner scanner = new Scanner(System.in);
		try {
			client.connect();

			/*
			line = scanner.nextLine();
			packet = new Packet(1, line);
			line = null;
			client.out.writeObject(packet);
			client.out.flush();
			packet = null;
			*/
			while (true) {
				line = scanner.nextLine();
				if (line.equals("quit")) {
					break;
				}
				packet = new Packet(1, line);
				line = null;
				client.out.writeObject(packet);
				client.out.flush();
				packet = null;

				packet = client.receivePacket();
				System.out.println(packet.data);
			}
		} catch (Exception e) {
			System.out.println("Some exception when Connecting or receiving the packet");
		}
		scanner.close();

	}
}
