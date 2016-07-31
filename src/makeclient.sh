#! /bin/bash
rm -rv StartClient.class
rm -rv client/*.class
rm -rv packet/*.class
echo javac StartClient.java
javac StartClient.java
