package capture;

import java.util.Set;
import java.util.Arrays;
import capture.client.*;
import java.io.Console;
import java.io.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * Author Murray Heymann
 *
 * This file is starts the client.  If it is started in gui mode, the swing
 * components are created and managed from here.  Threads for incoming and
 * outgoing data are created from here.  
 *
 * "LAFAYETTE!
 * I'm taking this horse
 * by the reins making
 * Redcoats redder with bloodstains"
 * Guns And Ships, Hamilton
 */
public class ChatClient extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	/* For giving instructions on what to enter in the txt field below */
	private JLabel label = null;
	/* text field for entering username and messages */
	private JTextField tfSurname = null;
	private JTextField tfCell = null;
	private JTextField tfEmail = null;
	private JTextField tfIdNo = null;
	private JTextField tfMaths = null;
	private JTextField tfScience = null;
	private JTextField tfEng = null;
	private JTextField tfName = null;
	private JTextField tfData = null;

	private FocusListener flnLogin;
	private FocusListener fldLogin, fldConnect;
	private FocusListener flSurname;
	private FocusListener flIdNo;
	private FocusListener flEmail;
	private FocusListener flCellphone;
	private FocusListener flMaths;
	private FocusListener flScience;
	private FocusListener flEng;

	/* For entering the ip and port number */
	private JTextField tfServerIP = null, tfPortNo = null;
	/* Buttons for actions to be performed */
	private JButton login = null, /*logout = null,*/  
			echo = null, submit = null;; 
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
		JPanel northPanel = new JPanel(new GridLayout(29,1));
		/* Space to enter the server's name and port number */
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
		label = new JLabel("Enter your details in the fields below:", SwingConstants.CENTER);
		northPanel.add(label);
		tfName = new JTextField("Name");
		tfName.setEditable(false);
		tfData = new JTextField("Password");
		tfData.setEditable(false);
		tfSurname = new JTextField("Surname");
		tfSurname.setEditable(false);
		tfCell = new JTextField("Cellphone");
		tfCell.setEditable(false);
		tfEmail = new JTextField("Email");
		tfEmail.setEditable(false);
		tfIdNo = new JTextField("IdNo");
		tfIdNo.setEditable(false);
		tfMaths = new JTextField("Maths");
		tfMaths.setEditable(false);
		tfScience = new JTextField("Science");
		tfScience.setEditable(false);
		tfEng = new JTextField("Eng");
		tfEng.setEditable(false);

		flnLogin = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfName.getText().equals("Name")) {
					tfName.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfName.getText().equals("")) {
					tfName.setText("Name");
				}
			}
		};
		fldLogin = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfData.getText().equals("Password")) {
					tfData.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfData.getText().equals("")) {
					tfData.setText("Password");
				}
			}
		};

		fldConnect = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfData.getText().equals("Message")) {
					tfData.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfData.getText().equals("")) {
					tfData.setText("Message");
				}
			}
		};

		flSurname = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfSurname.getText().equals("Surname")) {
					tfSurname.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfSurname.getText().equals("")) {
					tfSurname.setText("Surname");
				}
			}
		};

		flIdNo = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfIdNo.getText().equals("IdNo")) {
					tfIdNo.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfIdNo.getText().equals("")) {
					tfIdNo.setText("IdNo");
				}
			}
		};

		flEmail = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfEmail.getText().equals("Email")) {
					tfEmail.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfEmail.getText().equals("")) {
					tfEmail.setText("Email");
				}
			}
		};

		flCellphone = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfCell.getText().equals("Cellphone")) {
					tfCell.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfCell.getText().equals("")) {
					tfCell.setText("Cellphone");
				}
			}
		};

		flMaths = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfMaths.getText().equals("Maths")) {
					tfMaths.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfMaths.getText().equals("")) {
					tfMaths.setText("Maths");
				}
			}
		};

		flScience = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfScience.getText().equals("Science")) {
					tfScience.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfScience.getText().equals("")) {
					tfScience.setText("Science");
				}
			}
		};

		flEng = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfEng.getText().equals("Eng")) {
					tfEng.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfEng.getText().equals("")) {
					tfEng.setText("Eng");
				}
			}
		};


		tfName.addFocusListener(flnLogin);
		tfData.addFocusListener(fldLogin);
		tfSurname.addFocusListener(flSurname);
		tfCell.addFocusListener(flCellphone);
		tfIdNo.addFocusListener(flIdNo);
		tfEmail.addFocusListener(flEmail);
		tfMaths.addFocusListener(flMaths);
		tfScience.addFocusListener(flScience);
		tfEng.addFocusListener(flEng);

		northPanel.add(new JLabel("Surname"));
		northPanel.add(tfSurname);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Name"));
		northPanel.add(tfName);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("South African ID number"));
		northPanel.add(tfIdNo);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Cellphone number"));
		northPanel.add(tfCell);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Email"));
		northPanel.add(tfEmail);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Matric Maths Mark"));
		northPanel.add(tfMaths);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Matric Science Mark"));
		northPanel.add(tfScience);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Matric English Mark"));
		northPanel.add(tfEng);
		northPanel.add(new JLabel(""));
		northPanel.add(new JLabel("Additional Comments"));
		northPanel.add(tfData);
		northPanel.add(new JLabel(""));
		this.add(northPanel, BorderLayout.NORTH);

		/* 
		 * The CenterPanel where chat's are displayed and online users
		 * shown
		 */
		taMessages = new JTextArea("Message area:\n", 80, 80);
		taUsers = new JTextArea("\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		centerPanel.add(new JScrollPane(taMessages));
		centerPanel.add(new JScrollPane(taUsers));
		taMessages.setEditable(false);
		taUsers.setEditable(false);
		this.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();

		/* the buttons */
		login = new JButton("Login");
		login.addActionListener(this);
		/*
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		*/
		submit = new JButton("Submit");
		submit.addActionListener(this);
		submit.setEnabled(false);
		echo = new JButton("Echo");
		echo.addActionListener(this);
		echo.setEnabled(false);

		southPanel.add(login);
		/*
		southPanel.add(logout);
		*/
		southPanel.add(submit);
		southPanel.add(echo);

		this.add(southPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setVisible(true);


		tfName.addActionListener(this);
		tfData.addActionListener(this);

		tfSurname.requestFocus();
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

	private void resetTextFields() {
		tfName.setText("Name");
		tfData.setText("Message");
		tfSurname.setText("Surname");
		tfCell.setText("Cellphone");
		tfEmail.setText("Email");
		tfIdNo.setText("IdNo");
		tfMaths.setText("Maths");
		tfScience.setText("Science");
		tfEng.setText("Eng");
		tfSurname.requestFocus();
	}

	public void brokenConnection() {
		login.setEnabled(true);
		/*
		logout.setEnabled(false);
		*/
		echo.setEnabled(false);
		submit.setEnabled(false);
		label.setText("Enter your Username and password below");
		this.resetTextFields();	
		tfData.setText("Message");

		tfData.removeFocusListener(fldConnect);
		tfData.addFocusListener(fldLogin);
		tfPortNo.setText("" + this.portNo);
		tfServerIP.setText(this.hostAddress);
		tfServerIP.setEditable(true);
		tfPortNo.setEditable(true);

		tfName.setEditable(false);
		tfSurname.setEditable(false);
		tfCell.setEditable(false);
		tfEmail.setEditable(false);
		tfIdNo.setEditable(false);
		tfMaths.setEditable(false);
		tfScience.setEditable(false);
		tfEng.setEditable(false);
		tfData.setEditable(false);
		connected = false;
		taMessages.setText("");
		taUsers.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		/* buttons */
		if (o == echo) {
			String mtext = tfData.getText();
			if (speaker.echoString(mtext)) {
			} else {
				this.append("Some error echoing message\n");
			}
			
			this.resetTextFields();	

			return;
		}

		if (o == tfName) {
			tfData.requestFocus();
			return;
		}
		if ((connected) && (o == submit)) {
			/* sending message */

			String sname = tfSurname.getText();
			String fname = tfName.getText();
			String sCell = tfCell.getText();
			String sEmail = tfEmail.getText();
			String sID = tfIdNo.getText();
			String sMaths = tfMaths.getText();
			String sScience = tfScience.getText();
			String sEng = tfEng.getText();
			String comments = tfData.getText();
			if (speaker.sendStrings(sname, fname, sID, sCell, sEmail, sMaths, 
						sScience, sEng, comments)) {
				//this.append(this.myName + " to " + rname + ": " + mtext + "\n");
				//TODO: some message to indicate success
			} else {
				this.append("Some error sending data\n");
			}
				
			this.resetTextFields();	
			return;
		}

		if ((o == login)) {
			/* basically log in the user */

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
			
			this.speaker = new ClientSpeaker("defaultName", server, port, true);
			/* open connection if possible */

			if (!this.speaker.login()) {
				return;
			}
			this.myName = speaker.getName();
			System.out.printf("logged in\n");

			this.listener = new ClientListener(this.speaker.getSocketChannel(), this, this.myName);
			Thread thread = new Thread(listener);
			thread.start();

			tfData.setText("Message");
			tfData.removeFocusListener(fldLogin);
			tfData.addFocusListener(fldConnect);
			tfName.setText("Name");
			label.setText("Enter your details in the fields below:");
			connected = true;

			login.setEnabled(false);
			/*
			logout.setEnabled(true);
			*/
			echo.setEnabled(true);
			submit.setEnabled(true);

			tfServerIP.setEditable(false);
			tfPortNo.setEditable(false);
			tfName.setEditable(true);
			tfData.setEditable(true);
			tfSurname.setEditable(true);
			tfCell.setEditable(true);
			tfEmail.setEditable(true);
			tfIdNo.setEditable(true);
			tfMaths.setEditable(true);
			tfScience.setEditable(true);
			tfEng.setEditable(true);
			this.resetTextFields();	

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
		@SuppressWarnings("unused")
		ChatClient client = null;
		String line = null;
		String name = null;
		Thread threadSpeaker = null;
		Thread threadListen = null;
		ClientSpeaker speaker = null;
		ClientListener listener = null;
		File dir = null;
		
		dir = new File("data");
		if ((!dir.exists()) && (!dir.mkdir())) {
			System.out.printf("failed to make directory 'data' as required\n");
		}

		
		client = new ChatClient("localhost", 8002);
	}
}
