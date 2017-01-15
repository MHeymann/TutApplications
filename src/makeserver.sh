#! /bin/bash
rm -rv capture/ChatServer.class
rm -rv capture/server/*.class
rm -rv capture/packet/*.class
echo javac capture/ChatServer.java
javac capture/ChatServer.java
echo mkdir -pv data
mkdir -pv data
