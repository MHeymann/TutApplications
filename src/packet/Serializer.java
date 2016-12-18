package packet;

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
	private final int lower_bytes =  1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256;
	private final int upper_bytes = lower_bytes << 8;

    public static byte[] serialize(Packet packet) throws IOException {
		int size = 0;
		int i;
		ByteBuffer bb = null;

		if(packet == null) {
			return new byte[0];
		}

		/* code */
		size += 4;

		/* name */
		size += 4;
		if (packet.name != null) {
			size += packet.name.length() * 2;
		}

		/* data */
		size += 4;
		if (packet.data != null) {
			size += packet.data.length() * 2;
		}

		/* to */
		size += 4;
		if (packet.to != null) {
			size += packet.to.length() * 2;
		}

		/* users */
		/* users count */
		size += 4;
		if (packet.users != null) {
			for (String s: packet.users) {
				/* name length */
				size += 4;
				/* name */
				size += s.length() * 2;
			}
		}

		/* allocate bytes */
		bb = ByteBuffer.allocate(size);

		/* code */
		bb.putInt(packet.code);

		/* name */
		if (packet.name == null) {
			bb.putInt(0);
		} else {
			bb.putInt(packet.name.length());
			for (i = 0; i < packet.name.length(); i++)
			{
				bb.putChar(packet.name.charAt(i));
			}
		}

		/* data */
		if (packet.data == null) {
			bb.putInt(0);
		} else {
			bb.putInt(packet.data.length());
			for (i = 0; i < packet.data.length(); i++)
			{
				bb.putChar(packet.data.charAt(i));
			}
		}

		/* to */
		if (packet.to == null) {
			bb.putInt(0);
		} else {
			bb.putInt(packet.to.length());
			for (i = 0; i < packet.to.length(); i++)
			{
				bb.putChar(packet.to.charAt(i));
			}
		}

		/* users */

		if (packet.users == null) {
			bb.putInt(0);
		} else {
			bb.putInt(packet.users.size());
			for (String s: packet.users) {
				bb.putInt(s.length());
				for (i = 0; i < s.length(); i++) {
					bb.putChar(s.charAt(i));
				}
			}
		}
		return bb.array();
	}

    public static byte[] serializeIntoStream(Packet packet) throws IOException {
		int i;

        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
				o.writeInt(packet.code);
				if (packet.name != null) {
					o.writeInt(packet.name.length());
					for (i = 0; i < packet.name.length(); i++) {
						o.writeChar(packet.name.charAt(i));
					}
				} else {
					o.writeInt(0);
				}
				if (packet.data != null) {
					o.writeInt(packet.data.length());
					for (i = 0; i < packet.data.length(); i++) {
						o.writeChar(packet.data.charAt(i));
					}
				} else {
					o.writeInt(0);
				}
				if (packet.to != null) {
					o.writeInt(packet.to.length());
					for (i = 0; i < packet.to.length(); i++) {
						o.writeChar(packet.to.charAt(i));
					}
				} else {
					o.writeInt(0);
				}
				if (packet.users != null) {
					o.writeInt(packet.users.size());
					for (String s: packet.users) {
						o.writeInt(s.length());
						for (i = 0; i < s.length(); i++) {
							o.writeChar(s.charAt(i));
						}
					}
				} else {
					o.writeInt(0);
				}
            }
            return b.toByteArray();
        }
    }

    public static byte[] serializeObj(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    public static Packet deserialize(byte[] bytes) {
		int code;
		int len;
		int i, j;
		int userCount;
		/*
		String name;
		String data;
		String to;
		*/
		HashSet<String> users;
		
		StringBuilder sb;
		Packet packet = new Packet();
		ByteBuffer bb = ByteBuffer.wrap(bytes);

		packet.code = bb.getInt();
		System.out.printf("code: %d\n", packet.code);

		len = bb.getInt();
		if (len == 0) {
			packet.name = null;
		} else {
			System.out.printf("%d\n", len);
			sb = new StringBuilder(len);
			for (i = 0; i < len; i++) {
				sb.append(bb.getChar());
			}
			packet.name = sb.toString();
			System.out.printf("packet name: %s\n", packet.name);
			sb = null;
		}

		len = bb.getInt();
		if (len == 0) {
			packet.data = null;
		} else {
			sb = new StringBuilder(len);
			for (i = 0; i < len; i++) {
				sb.append(bb.getChar());
			}
			packet.data = sb.toString();
			sb = null;
		}

		len = bb.getInt();
		if (len == 0) {
			packet.to = null;
		} else {
			sb = new StringBuilder(len);
			for (i = 0; i < len; i++) {
				sb.append(bb.getChar());
			}
			packet.to = sb.toString();
			sb = null;
		}

		userCount = bb.getInt();
		if (userCount > 0) {
			users = new HashSet<String>();
			for (i = 0; i < userCount; i++) {
				len = bb.getInt();
				sb = new StringBuilder(len);
				for (j = 0; j < len; j++) {
					sb.append(bb.getChar());
				}
				users.add(sb.toString());
			}
			packet.users = users;
		} else {
			packet.users = null;
		}

		return packet;
	}

    public static Packet deserializeFromStream(byte[] bytes) throws IOException, ClassNotFoundException {
		Packet packet = new Packet();
		int i, j;
		int length;
		StringBuilder sb;
		char c;
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
				packet.code = o.readInt();

				length = o.readInt();
				sb = new StringBuilder();
				for (i = 0; i < length; i++) {
					c = o.readChar();
					sb.append(c);
				}
				packet.name = sb.toString();

				length = o.readInt();
				if (length > 0) {
					sb = new StringBuilder();
					for (i = 0; i < length; i++) {
						c = o.readChar();
						sb.append(c);
					}
					packet.data = sb.toString();
				} else {
					packet.data = null;
				}

				length = o.readInt();
				if (length > 0) {
					sb = new StringBuilder();
					for (i = 0; i < length; i++) {
						c = o.readChar();
						sb.append(c);
					}
					packet.to = sb.toString();
				} else {
					packet.to = null;
				}

				length = o.readInt();
				if (length > 0) {
					HashSet<String> set = new HashSet<String>();
					for (i = 0; i < length; i++) {
						int nameLen = o.readInt();
						sb = new StringBuilder();
						for (j = 0; j < nameLen; j++) {
							c = o.readChar();
							sb.append(c);
						}
						set.add(sb.toString());
					}
					packet.users = set;
				} else {
					packet.users = null;
				}
				return packet;
            }
        }
    }

    public static Object deserializeToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }

}
