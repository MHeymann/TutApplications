package capture.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;

/*
 * This is some brief code, found on a stackexchange page.  It switches data
 * between object formand byte array form.  Objects need to be made into 
 * byte arrays if they are to be transmitted over a network.  
 */

public class Serializer {
	//private final int lower_bytes =  1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256;
	//private final int upper_bytes = lower_bytes << 8;
	
	private static int get_string_buffer_size(String s) {
		int ret_size = 4;
		if (s != null) {
			ret_size += s.length() /* * 2*/;
		}

		return ret_size;
	}

    public static byte[] serialize(Packet packet) throws IOException {
		int size = 0;
		int i;
		ByteBuffer bb = null;

		if(packet == null) {
			return new byte[0];
		}

		/* code */
		size += 4;

		/* surname */
		size += get_string_buffer_size(packet.surname);
		/* name */
		size += get_string_buffer_size(packet.name);
		/* id_number */
		size += get_string_buffer_size(packet.id_number);
		/* cellphone */
		size += get_string_buffer_size(packet.cellphone);
		/* email */
		size += get_string_buffer_size(packet.email);
		/* math_mark */
		size += get_string_buffer_size(packet.math_mark);
		/* science_mark */
		size += get_string_buffer_size(packet.science_mark);
		/* eng_mark */
		size += get_string_buffer_size(packet.eng_mark);
		/* data */
		size += get_string_buffer_size(packet.data);
		/* to */
		size += get_string_buffer_size(packet.to);

		/* users */
		/* users count */
		size += 4;
		if (packet.users != null) {
			for (String s: packet.users) {
				/* name length */
				size += get_string_buffer_size(s);
			}
		}


		/* allocate bytes */
		bb = ByteBuffer.allocate(size);


		/* code */
		bb.putInt(packet.code);
		
		/* surname */
		write_string(bb, packet.surname);
		/* name */
		write_string(bb, packet.name);
		/* id_number */
		write_string(bb, packet.id_number);
		/* cellphone */
		write_string(bb, packet.cellphone);
		/* email */
		write_string(bb, packet.email);
		/* math_mark */
		write_string(bb, packet.math_mark);
		/* science_mark */
		write_string(bb, packet.science_mark);
		/* eng_mark */
		write_string(bb, packet.eng_mark);
		/* data */
		write_string(bb, packet.data);
		/* to */
		write_string(bb, packet.to);

		/* users */
		if (packet.users == null) {
			bb.putInt(0);
		} else {
			bb.putInt(packet.users.size());
			for (String s: packet.users) {
				write_string(bb, s);
			}
		}
		return bb.array();
	}

	private static void write_string(ByteBuffer bb, String s) {
		int i;
		
		if (s == null) {
			bb.putInt(0);
		} else {
			bb.putInt(s.length());
			bb.put(s.getBytes());
			/*
			for (i = 0; i < s.length(); i++)
			{
				bb.putChar(s.charAt(i));
			}
			*/
		}
	}


	private static String readStringFromBuffer(ByteBuffer bb) {
		int len = 0;
		byte[] bytes = null;
		/*
		StringBuilder sb = null;
		*/
		int i;
		
		len = bb.getInt();
		if (len == 0) {
			return null;
		} else {
			bytes = new byte[len];
			bb.get(bytes);
			return new String(bytes);
			/*
			sb = new StringBuilder(len);
			for (i = 0; i < len; i++) {
				sb.append(bb.getChar());
			}
			return sb.toString();
			*/
		}
	}
    public static Packet deserialize(byte[] bytes) {
		int len;
		int i, j;
		int userCount;
		HashSet<String> users;
		
		StringBuilder sb;
		Packet packet = new Packet();
		ByteBuffer bb = ByteBuffer.wrap(bytes);

		packet.code = bb.getInt();

		packet.surname = readStringFromBuffer(bb);
		packet.name = readStringFromBuffer(bb);
		packet.id_number = readStringFromBuffer(bb);
		packet.cellphone = readStringFromBuffer(bb);
		packet.email = readStringFromBuffer(bb);
		packet.math_mark = readStringFromBuffer(bb);
		packet.science_mark = readStringFromBuffer(bb);
		packet.eng_mark = readStringFromBuffer(bb);

		packet.data = readStringFromBuffer(bb);
		packet.to = readStringFromBuffer(bb);

		userCount = bb.getInt();
		if (userCount > 0) {
			users = new HashSet<String>();
			for (i = 0; i < userCount; i++) {
				users.add(readStringFromBuffer(bb));
			}
			packet.users = users;
		} else {
			packet.users = null;
		}

		return packet;
	}

}
