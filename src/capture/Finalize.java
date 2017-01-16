package capture;

import java.util.Set;
import java.util.Arrays;
import capture.client.*;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import capture.packet.FileMethods;
import capture.packet.Packet;
import capture.packet.Serializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;

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
public class Finalize extends JFrame implements ActionListener {

	private String clientName = null;
	private int code = -1;

	private File csvFile = null;

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
	private FocusListener /*fldLogin, */fldConnect;
	private FocusListener flSurname;
	private FocusListener flIdNo;
	private FocusListener flEmail;
	private FocusListener flCellphone;
	private FocusListener flMaths;
	private FocusListener flScience;
	private FocusListener flEng;

	private final JFileChooser fchoose;

	/* Buttons for actions to be performed */
	private JButton open = null, submit = null; 
	/* For displaying messages */
	private JTextArea taMessages = null, taUsers = null;

	
	public Finalize(String csvFileName) {
		super("Finalizer");

		this.csvFile = new File(csvFileName);
		try {
			if (csvFile.createNewFile()) {
				System.out.printf("created new csv file %s\n", csvFileName);
				if (!appendCSV("Surname,Name,Cell,Email,ID,Maths,Science,English,Comments\n")) { 
					System.err.printf("failed to put headers in csv\n");
				}
			} else {
				System.out.printf("using existing csv file\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.fchoose = new JFileChooser("serverdata/");

		/* NorthPanel */
		JPanel northPanel = new JPanel(new GridLayout(28,1));

		/* The label and text field for communication */
		label = new JLabel("Enter your details in the fields below:", SwingConstants.CENTER);
		northPanel.add(label);
		tfName = new JTextField("Name");
		tfName.setEditable(false);
		tfData = new JTextField("Comments");
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

		fldConnect = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (tfData.getText().equals("Comments")) {
					tfData.setText("");
				}
			}
			public void focusLost(FocusEvent e) {
				if (tfData.getText().equals("")) {
					tfData.setText("Comments");
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
		tfData.addFocusListener(fldConnect);
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
		open = new JButton("Open Details");
		open.addActionListener(this);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		submit.setEnabled(false);

		southPanel.add(open);
		southPanel.add(submit);

		this.add(southPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 595);
		this.setVisible(true);


		tfName.addActionListener(this);
		tfData.addActionListener(this);

		tfSurname.requestFocus();

		brokenConnection();
	}

	public void append(String s) 
	{
		taMessages.append(s);
		taMessages.setCaretPosition(taMessages.getText().length() - 1);
	}

	private void resetTextFields() {
		tfName.setText("Name");
		tfData.setText("Comments");
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
		open.setEnabled(true);
		submit.setEnabled(false);
		label.setText("Use the 'Open Details' button to load a student file\n");
		this.resetTextFields();	

		tfName.setEditable(false);
		tfSurname.setEditable(false);
		tfCell.setEditable(false);
		tfEmail.setEditable(false);
		tfIdNo.setEditable(false);
		tfMaths.setEditable(false);
		tfScience.setEditable(false);
		tfEng.setEditable(false);
		tfData.setEditable(false);
		taMessages.setText("");
		taUsers.setText("");
	}

	private boolean appendCSV(String s) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(this.csvFile.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(s);
		} catch (Exception ee) {
			ee.printStackTrace();
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (Exception ea) {
				ea.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		/* buttons */
		if (o == submit) {

			String sname = tfSurname.getText();
			String fname = tfName.getText();
			String sCell = tfCell.getText();
			String sEmail = tfEmail.getText();
			String sID = tfIdNo.getText();
			String sMaths = tfMaths.getText();
			String sScience = tfScience.getText();
			String sEng = tfEng.getText();
			String comments = tfData.getText();
				
			this.resetTextFields();	
			this.brokenConnection();
			moveDataFile(fchoose.getSelectedFile());

			Packet p = new Packet(this.code, sname, fname, sID, sCell, 
					sEmail, sMaths, sScience, sEng, comments, 
					this.clientName);	
			// TODO: add to csv
			if (!appendCSV(sname + "," + fname + "," + sCell + "," + sEmail + 
						"," + sID + "," + sMaths + "," + sScience + "," + 
						sEng + "," + comments + "\n")) 
			{
				System.err.printf("NB!!! failed to add packet to csv file. " + p + "\n");
				System.err.printf("Please review!!\n");
			}
			

			
			byte[] bytes = null;
			try {
				bytes = Serializer.serialize(p);
				String filename = p.surname + "_" + p.name + "_" + Packet.bytesToHex(Packet.getSha256(bytes));
				if (!FileMethods.writeDataToFile("csv_candidates/", filename, bytes)) {
					System.out.printf("Failed to write to directory of processed files\n");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}



			return;
		}

		if (o == open) {
			Packet p = null;

			int returnVal = fchoose.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.printf("file selected: %s\n", fchoose.getSelectedFile().getName());
				p = FileMethods.readPacketFromFile("serverdata/", fchoose.getSelectedFile().getName());	
				this.resetTextFields();	
				tfSurname.setText(p.surname);
				tfName.setText(p.name);
				tfCell.setText(p.cellphone);
				tfEmail.setText(p.email);
				tfIdNo.setText(p.id_number);
				tfMaths.setText(p.math_mark);
				tfScience.setText(p.science_mark);
				tfEng.setText(p.eng_mark);
				tfData.setText(p.data);
				this.clientName = p.to;
				this.code = p.code;
	
				label.setText("Review your details in the fields below:");
	
				open.setEnabled(false);
				submit.setEnabled(true);
	
				tfName.setEditable(true);
				tfData.setEditable(true);
				tfSurname.setEditable(true);
				tfCell.setEditable(true);
				tfEmail.setEditable(true);
				tfIdNo.setEditable(true);
				tfMaths.setEditable(true);
				tfScience.setEditable(true);
				tfEng.setEditable(true);
			}

			return;
		}
	}

	private static void moveDataFile(File file) {
		try {
			if (file.renameTo(new File("processed/" + file.getName()))) {
			} else {
				System.out.printf("Failed to move file to new directory\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args)  {
		Finalize client = null;
		
		File dir = null;
		
		dir = new File("data");
		if ((!dir.exists()) && (!dir.mkdir())) {
			System.out.printf("failed to make directory 'data' as required\n");
		}
		dir = new File("processed");
		if ((!dir.exists()) && (!dir.mkdir())) {
			System.out.printf("failed to make directory 'processed' as required\n");
		}
		dir = new File("csv_candidates");
		if ((!dir.exists()) && (!dir.mkdir())) {
			System.out.printf("failed to make directory 'csv_candidates' as required\n");
		}
		dir = null;

		
		
		client = new Finalize("candidates.csv");
	}
}
