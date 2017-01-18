package capture;

import capture.datachecks.DataChecks;
import capture.packet.Packet;
import capture.packet.FileMethods;

public class CheckId {
	
	public static void main(String[] args) {
		Packet p = null;
		String id = null;
		boolean valid = true;
		if (args.length < 1) {
			System.out.printf("Please provide a valid filename as argument\n");
			System.exit(1);
		}

		if (args[0].equals("-i")) {
			if (args.length == 2) {
				id = args[1];
			} else {
				System.out.printf("no id provided\n");
				System.exit(2);
			}
		} else {
			p = FileMethods.readPacketFromFile("", args[0]);
			if (p == null) {
				System.out.printf("Please provide a valid filename as argument\n");
				System.exit(3);
			}
			if (p.id_number == null) {
				System.out.printf("No id in packet\n");
				System.exit(4);
			}
			id = p.id_number;
		}
		
		if (DataChecks.isValidId(id, null)) {
			System.out.printf("Valid ID. Pass :)\n");
		} else {
			System.out.printf("%s is an invalid ID\n", id);
		}
		
	}
}
