package client;

import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;
import client.*;
import packet.*;
import java.io.Console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Murray Heymann
 */
public class ChatClient extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	/* For giving instructions on what to enter in the txt field below */
	private JLabel label = null;
	/* text field for entering username and messages */
	private JTextField tfName = null;
	private JTextField tfData = null;
	/* For entering the ip and port number */
	private JTextField tfServerIP = null, tfPortNo = null;
	/* Buttons for actions to be performed */
	private JButton login = null, logout = null, whosThere = null; 
	/* For displaying messages */
	private JTextArea taMessages = null, taUsers = null;
	/* current connection status */
	private boolean connected = false;
	/* The Client Listener */
	private ClientListener listener = null;
	/* The Client Speaker for sending messages to the server */
	private ClientSpeaker speaker = null;
	private String myName = null;

	/* The port to connect to */
	private int portNo = -1;
	/* The host ip address */
	private String hostAddress = null;
	
	public ChatClient(String host, int port) {
		super("Chatterbox");
		this.portNo = port;
		this.hostAddress = host;

		/* NorthPanel */
		JPanel northPanel = new JPanel(new GridLayout(4,1));
		/* Spacte to enter the server's name and port number */
		JPanel serverPortPanel = new JPanel(new GridLayout(1,5, 1,3));
		/* start up the text fields for server name and port number */
		tfServerIP = new JTextField(this.hostAddress);
		tfPortNo = new JTextField("" + this.portNo);
		tfPortNo.setHorizontalAlignment(SwingConstants.RIGHT);

		serverPortPanel.add(new JLabel("Server Address:  "));
		serverPortPanel.add(tfServerIP);
		serverPortPanel.add(new JLabel("Port Number:  "));
		serverPortPanel.add(tfPortNo);
		serverPortPanel.add(new JLabel(""));
		/* put all of this in the north pannel */
		northPanel.add(serverPortPanel);

		/* The label and text field for communication */
		label = new JLabel("Enter your Username:", SwingConstants.CENTER);
		northPanel.add(label);
		tfName = new JTextField("name");
		tfData = new JTextField("Password");
		tfName.setBackground(Color.WHITE);
		tfData.setBackground(Color.WHITE);
		northPanel.add(tfName);
		northPanel.add(tfData);
		this.add(northPanel, BorderLayout.NORTH);

		/* 
		 * The CenterPanel where chat's are displayed and online users
		 * shown
		 */
		taMessages = new JTextArea("Message area:\n", 80, 80);
		taUsers = new JTextArea("\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(taMessages));
		centerPanel.add(new JScrollPane(taUsers));
		taMessages.setEditable(false);
		taUsers.setEditable(false);
		this.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();

		/* the 3 buttons */
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		whosThere = new JButton("Who is in?");
		whosThere.addActionListener(this);
		whosThere.setEnabled(false);

		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whosThere);

		this.add(southPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setVisible(true);
		tfName.requestFocus();
	}

	public void append(String s) 
	{
		taMessages.append(s);
		taMessages.setCaretPosition(taMessages.getText().length() - 1);
	}

	public void showOnlineUsers(Set<String> users) {
		int i;
		String[] userArray = new String[users.size()];

		i = 0;
		for (String s: users){
			userArray[i] = s;
			i++;
		}

		Arrays.sort(userArray);

		taUsers.setText("");
		taUsers.append("Online Users:\n");
		for (String s: userArray) {
			taUsers.append(s + "\n");
		}
	}

	public void brokenConnection() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whosThere.setEnabled(false);
		label.setText("Enter your Username and password below");
		tfName.setText("name");
		tfData.setText("password");
		tfPortNo.setText("" + this.portNo);
		tfServerIP.setText(this.hostAddress);
		tfServerIP.setEditable(true);
		tfPortNo.setEditable(true);
		tfName.removeActionListener(this);
		tfData.removeActionListener(this);
		connected = false;
		taMessages.setText("");
		taUsers.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		/* logout being the button */
		if (o == logout) {
			speaker.logoff();
			this.brokenConnection();
			return;
		} else if (o == whosThere) {
			speaker.getOnlineNames();
			return;
		}

		if (connected) {
			/* sending message */
			if (o == tfName) {
				tfData.requestFocus();
			} else {
				String mtext = tfData.getText();
				String rname = tfName.getText();
				if (speaker.sendString(mtext, rname)) {
					this.append(this.myName + " to " + rname + ": " + mtext + "\n");
				} else {
					this.append("Some error sending message\n");
				}
				
				tfName.setText("");
				tfData.setText("");
			}
			return;
		}

		if (o == login) {
			String username = tfName.getText().trim();
			if (username.length() == 0) {
				return;
			}
			
			String password = tfData.getText().trim();
			if (password.length() == 0) {
				return;
			}

			String server = tfServerIP.getText().trim();
			if (server.length() == 0) {
				return;
			}

			String portNumber = tfPortNo.getText().trim();
			if (portNumber.length() == 0) {
				return;
			}
			int port = -1;
			try {
				port = Integer.parseInt(portNumber);
			} catch (Exception except) {
				System.err.printf("Please provide a valid port number\n");
				return;
			}
			
			System.out.printf("lets create a speaker\n");
			this.speaker = new ClientSpeaker(username, server, port, true);
			this.myName = username;
			/* open connection if possible */

			if (!this.speaker.login(password)) {
				return;
			}

			this.listener = new ClientListener(this.speaker.getSocketChannel(), this);
			Thread thread = new Thread(listener);
			thread.start();

			tfData.setText("");
			tfName.setText("");
			label.setText("Enter recepient and message");
			connected = true;

			login.setEnabled(false);
			logout.setEnabled(true);
			whosThere.setEnabled(true);
			tfServerIP.setEditable(false);
			tfPortNo.setEditable(false);
			tfName.addActionListener(this);
			tfData.addActionListener(this);

			this.setTitle(this.getTitle() + " - " + this.myName);

		}
	}

	public static String getPassword() {
		String line = null;
		Console cons = null;
		char[] passwd = null;

		if ((cons = System.console()) != null &&
				(passwd = cons.readPassword()) != null) {
			line = new String(passwd);
			java.util.Arrays.fill(passwd, ' ');
		}
		return line;
	}

    public static void main(String[] args)  {
		ChatClient client = null;
		String line = null;
		String name = null;
		Thread threadSpeaker = null;
		Thread threadListen = null;
		ClientSpeaker speaker = null;
		ClientListener listener = null;
		Scanner scanner = new Scanner(System.in);
		
		if ((args.length == 1) && args[0].equals("terminal")) {

			System.out.printf("Please enter your username: ");
			name = scanner.nextLine();
			speaker = new ClientSpeaker(name, "127.0.0.1", 8002, false);
	
			System.out.printf("Please provide the password for %s: ", name);
			line = getPassword();

			System.out.printf("%s with password %s \n", name, line);
			if (!speaker.login(line)) {
				return;
			}
			listener = new ClientListener(speaker.getSocketChannel());
	
			threadSpeaker = new Thread(speaker);
			threadListen = new Thread(listener);
			threadSpeaker.start();
			threadListen.start();
		} else {
			client = new ChatClient("localhost", 8002);
		}
	}
}
