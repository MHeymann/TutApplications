#! /bin/bash
rm -rv capture/ChatClient*.class
rm -rv capture/client/*.class
rm -rv capture/packet/*.class
echo javac capture/ChatClient.java
javac capture/ChatClient.java
#echo mkdir -pv data
#mkdir -pv data
