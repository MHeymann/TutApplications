package capture.datachecks;

import javax.swing.*;

public class DataChecks {

	private static boolean is_good_id_param(String p) {
		boolean result = true;
		if (p == null) {
			System.out.printf("id provided was null\n");
			result = false;
		} else if (p.length() < 13) {
			System.out.printf("id %s provided was too short\n", p);
			result = false;
		}

		return result;
	}

	public static boolean isNumeric(String s) {  
		    return s.matches("[-+]?\\d*\\.?\\d*");  
	}  


	private static boolean validCheckDigit(String id) {
		boolean result = true;
		int checkdigit = -1;
		int sum;
		int Z;
		int i = 0;
		int temp;
		
		checkdigit = (int)(id.charAt(12) - 48);
		sum = 0;
		for (i = 0; i < 6; i++) {
			sum += (int)(id.charAt(i*2) - 48);
		}
		for (i = 0; i < 6; i++) {
			temp = 2 * (int)(id.charAt(i*2+1) - 48);
			sum += (temp / 10) + (temp % 10);
		}
		if (((checkdigit + sum) % 10) == 0) {
			result = true;
		} else {
			System.out.printf("sum %d ", sum);
			System.out.printf("check %d ", checkdigit);
			result = false;
		}

		return result;
	}

	public static boolean isCitizen(String id) {
		if (!is_good_id_param(id)) {
			return false;
		}
		return ((int)(id.charAt(10) - 48) == 0);
	}
	public static boolean isNonCitizen(String id) {
		if (!is_good_id_param(id)) {
			return false;
		}
		return ((int)(id.charAt(10) - 48) == 1);
	}

	private static boolean validCitizenDigit(String id) {
		return (isCitizen(id) || isNonCitizen(id));
	}

	private static boolean isValidDay(int year, int month, int day) {
		int high;
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				high = 31;
				break;
			case 2:
				if (year == 0) {
					high = 29;
					// grrrrr need more info
				} else if ((year % 4) == 0) {
					high = 29;
				} else {
					high = 28;
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				high = 30;
				break;
			default:
				high = 31;
				System.out.printf("Some logic error checking day of month\n");
				break;
		}
		if (day > high) {
			return false;
		} else {
			return true;
		}
	}
			//JOptionPane.showMessageDialog(this, "Invalid Id input.");
	private static void showUserMessage(String message, JFrame frame)
	{
		if (frame != null) {
			JOptionPane.showMessageDialog(frame, message);
		} else {
			System.out.printf(message + "\n");
		}
	}

	public static boolean isValidId(String id, JFrame frame) {
		boolean valid = true;
		if (is_good_id_param(id)) {
			if (!isNumeric(id)) {
				showUserMessage("Id " + id + " is not a numeric string\n", frame);
				valid = false;
			} else {
				String year = id.substring(0,2);
				String month = id.substring(2,4);
				String day = id.substring(4,6);
				int y = Integer.parseInt(year);
				int m = Integer.parseInt(month);
				int d = Integer.parseInt(day);
				if (m > 12) {
					showUserMessage("invalid id: month indicated as " + m + "\n", frame);
					valid = false;
				} else if (!isValidDay(y, m, d)) {
					showUserMessage("invalid id: day indicated as " + d + ", impossible in month " + m + " of year " + y + "\n", frame);
					valid = false;
				}

			}
			if (!validCheckDigit(id)) {
				showUserMessage("Failed check digit: " + id, frame);
				valid = false;
			}   
			if (!validCitizenDigit(id)) {
				showUserMessage("Invalid eleventh digit: " + id.charAt(10), frame);
				valid = false;
			}
			if ((int)(id.charAt(11) - 48) != 8) {
				showUserMessage("Invalid twelfth digit: " + id.charAt(11), frame);
				valid = false;
			}
		} else {
			valid = false;
		}
		return valid;
	}
	
}
