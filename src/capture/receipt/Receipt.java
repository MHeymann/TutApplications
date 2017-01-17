package capture.receipt;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.lang.Process;

import capture.packet.Packet;

public class Receipt {
	private final String[] template1 = {
		"\\documentclass{letter}\n",
		"\\usepackage{invoice}\n",
		"\\address{Tut Maths and Science Upgrading \\\\ Tut North Campus \\\\ Soshanguve}\n",
		"\\signature{Signed} \n",
		"\\date{\\today}\n",
		"\\begin{document}\n",
		"	\\begin{letter}{Payment received from "
	};
	private final String[] template2 = {
		"}\n",
		"		\\opening{Invoice no. "
	};
	private final String[] template3 = {
		"}\n",
		"		Thank you for your payment!\n",
		"		\\begin{invoice}{Rand}{0}\n",
		"			\\ProjectTitle{Selection Fee}%\n",
		"				\\Fee{Maths \\& Science Upgrading Application} {100.00} {1}\n",
		"		\\end{invoice}\n",
		"		\\closing{Thank you for you payment!}\n",
		"	\\end{letter}\n",
		"\\end{document}\n"
	};
	private String dir = null;

	public Receipt(String dir) {
		File file = new File(dir);
		file.mkdir();
		file = new File(dir + "/counter");
		if (!file.exists()) {
			FileWriter fw = null;
			BufferedWriter bw = null;
			String zero = " 0";
			try {
				file.createNewFile();
				fw = new FileWriter(file, true);	
				bw = new BufferedWriter(fw);
				bw.write(zero);
		//		bw.close();
	//			bw = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
					if (fw != null) {
						fw.close();
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			System.out.printf("Created a new file for counting receipts\n");
		} else {
			System.out.printf("Using exisiting file for counting receipts\n");
		}

		this.dir = dir;
	}
	

	public void generateReceipt(String surname, String name, String id_no) throws Exception {
		long index = -1;
		int i;
		FileWriter fw = null;
		String filename = null;
		File file = new File(this.dir + "/counter");
		Scanner scanner = null;
		scanner = new Scanner(file);
		index = scanner.nextInt() + 1;
		scanner.close();
		fw = new FileWriter(file);
		fw.write(" " + index);
		fw.close();
		String sn, fn;

		sn = new String(surname);
		fn = new String(name);

		for (i = 0; i < sn.length(); i++) {
			if (sn.charAt(i) == ' ') {
				sn = sn.substring(0, i) + "_" + sn.substring(i+1);;
			}
		}
		for (i = 0; i < fn.length(); i++) {
			if (fn.charAt(i) == ' ') {
				fn = fn.substring(0, i) + "_" + fn.substring(i+1);;
			}
		}
		filename = sn + "_" + fn + "_" + id_no;
		System.out.printf(filename + "\n");

		file = new File(this.dir + "/" + filename + ".tex");
		file.createNewFile();
		fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		for (i = 0; i < template1.length; i++) {
			bw.write(template1[i]);
		}
		bw.write(surname + ", " + name);
		for (i = 0; i < template2.length; i++) {
			bw.write(template2[i]);
		}
		bw.write("" + index);
		for (i = 0; i < template3.length; i++) {
			bw.write(template3[i]);
		}
		bw.close();
		fw.close();


		System.out.printf("generating pdf receipt\n");
		Process p = Runtime.getRuntime().exec("pdflatex -output-directory " + this.dir + " " + this.dir + "/" + filename + ".tex");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			System.out.printf(line + "\n");
		}
		p.waitFor();
		System.out.printf("pdf generated.  instructing default printer to print\n");
		p = Runtime.getRuntime().exec("lpr " + this.dir + "/" + filename + ".pdf");
		p.waitFor();
		System.out.printf("print instructions sent\n");

	}
}
