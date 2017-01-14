package chat.packet;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class Files {

	public static boolean writePacketToFile(Packet packet) {
		return true;
	}
	public static boolean writeDataToFile(String hash, byte[] data) {
		RandomAccessFile file = null; 
		FileChannel fc = null; 
		ByteBuffer buf = null;
		try {
			file = new RandomAccessFile("data/" + hash, "rw");
			fc = file.getChannel();
			buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				fc.write(buf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (Exception ee) {
					ee.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public static byte[] readDataFromFile(String hash) {
		RandomAccessFile file = null; 
		FileChannel fc = null; 
		ByteBuffer buf = null;
		try {
			file = new RandomAccessFile("data/" + hash, "r");
			fc = file.getChannel();
			buf = ByteBuffer.allocate((int)file.length());
			while (fc.read(buf) != 0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (Exception ee) {
					ee.printStackTrace();
					return null;
				}
			}
		}

		
		if (hash.compareTo(Packet.bytesToHex(Packet.getSha256(buf.array()))) != 0) {
			System.err.printf("corrupted data when reading packet %s\n", hash);
			return null;
		} else {
			return buf.array();
		}
	}

	public static Packet readPacketFromFile(String hash) {
		byte[] data = null;
		Packet packet = null;

		data = readDataFromFile(hash);
		if (data == null) {
			return null;
		}
		
		packet = (Packet)Serializer.deserialize(data);

		return packet;
	}
}
