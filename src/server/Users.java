package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.HashSet;


import packet.Packet;


/**
 *
 * @author Murray Heymann
 */
public class Users {

	/* Some data structure for those currently online */
	private HashSet<Connection> connections = null;


	public Users() {
		connections = new HashSet<Connection>();
	}

	public void addConnection(Connection connect) {
		if (connect == null){
			return;
		}
		/*
		 * TODO:  add Lock 
		 */
		connections.add(connect);
	}
   
}
