#! /bin/bash
rm -rv chat/*.class
rm -rv chat/client/*.class
rm -rv chat/packet/*.class
echo javac chat/ChatClient.java
javac chat/ChatClient.java
