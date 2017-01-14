#! /bin/bash
rm -rv chat/ChatClient.class
rm -rv chat/client/*.class
rm -rv chat/packet/*.class
echo javac chat/ChatClient.java
javac chat/ChatClient.java
echo mkdir -pv data
mkdir -pv data
