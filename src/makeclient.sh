#! /bin/bash
rm -rv client/*.class
rm -rv packet/*.class
echo javac client/ChatClient.java
javac client/ChatClient.java
