package chat.packet;

/*
 * Author: Murray Heymann
 *
 * This is a class containing some magic numbers, to be used for 
 * identifying the function of a data packet in the chat system.  An enum
 * was considered, but this is a significantly larger datastructure to be
 * sent over the network.
 *
 * "Laughin' at my sister as she's dazzling the room
 * Then you Walked in and my heart went 'Boom!'"
 * --> Helpless, Hamilton
 */

public class Code {
	public static final int QUIT		= 0;
	public static final int SEND		= 1;
	public static final int ECHO		= 2;
	public static final int BROADCAST	= 3;
	public static final int LOGIN		= 4;
	public static final int GET_ULIST	= 5;
	public static final int ACCEPT		= 6;
	public static final int DENIAL		= 7;
}
