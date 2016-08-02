#! /bin/bash
rm -rv server/*.class
rm -rv packet/*.class
echo javac server/ChatServer.java
javac server/ChatServer.java
