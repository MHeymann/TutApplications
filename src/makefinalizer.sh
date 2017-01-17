#! /bin/bash
rm -rv capture/Finalize*.class
rm -rv capture/packet/*.class
rm -rv capture/receipt/*.class

echo javac capture/Finalize.java
javac capture/Finalize.java
