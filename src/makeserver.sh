#! /bin/bash
rm -rv chat/ChatServer.class
rm -rv chat/server/*.class
rm -rv chat/packet/*.class
echo javac chat/ChatServer.java
javac chat/ChatServer.java
echo mkdir -pv data
mkdir -pv data
