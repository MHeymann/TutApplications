package capture.packet;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class FileMethods {

	/*
	public static boolean writePacketToFile(String filename, Packet packet) {
		return writePackettoFile("data/", filename, packet);
	}
	public static boolean writePacketToFile(String dir, String filename, Packet packet) {
		return true;
	}
	*/
	public static boolean writeDataToFile(String filename, byte[] data) {
		return writeDataToFile("data/", filename, data);
	}
	public static boolean writeDataToFile(String dir, String filename, byte[] data) {
		RandomAccessFile file = null; 
		FileChannel fc = null; 
		ByteBuffer buf = null;
		try {
			file = new RandomAccessFile(dir + filename, "rw");
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
			if (file != null) {
				try {
					file.close();
				} catch (Exception ee) {
					ee.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public static byte[] readDataFromFile(String filename) {
		return readDataFromFile("data/", filename);
	}

	public static byte[] readDataFromFile(String dir, String filename, String hash) {
		ByteBuffer buf = null;

		buf = ByteBuffer.wrap(readDataFromFile(dir, filename));

		System.out.printf("reading data from file\n");
		if (hash.compareTo(Packet.bytesToHex(Packet.getSha256(buf.array()))) != 0) {
			System.err.printf("corrupted data when reading packet %s\n", hash);
			return null;
		} else {
			return buf.array();
		}
	}

	public static byte[] readDataFromFile(String dir, String filename) {
		RandomAccessFile file = null; 
		FileChannel fc = null; 
		ByteBuffer buf = null;
		try {
			file = new RandomAccessFile(dir + filename, "r");
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
		if (file != null) {
			try {
				file.close();
			} catch (Exception ee) {
				ee.printStackTrace();
				return null;
			}
		}
		
		return buf.array();
	}

	public static Packet readPacketFromFile(String filename) {
		return readPacketFromFile("data/", filename);
	}

	public static Packet readPacketFromFile(String dir, String filename) {
		return readPacketFromFile(dir, filename, null);
	}

	public static Packet readPacketFromFile(String dir, String filename, String hash) {
		byte[] data = null;
		Packet packet = null;

		if (hash != null) {
			data = readDataFromFile(dir, filename, hash);
		} else {
			data = readDataFromFile(dir, filename);
		}

		if (data == null) {
			packet = null;
		} else {
			packet = (Packet)Serializer.deserialize(data);
		}

		return packet;
	}
}
