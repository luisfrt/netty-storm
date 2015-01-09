Integration between a Netty client and Apache Storm using SSL (TLS). The exchanged messages are in JSON format.

Implementation of a sample Netty Spout and Netty producer that uses JSON to comunicate through an encrypted channel.


Two maven projects inside this repository: 

[netty-spout] Implementation of a Storm Spout capable of handling direct connections from Netty clients. The communication uses TLS. In the test folder of this project you can find a sample Netty Topology for testing purposes.
    
How to run: Load the project in eclipse as a maven project and run the test/NettyTopogy.java as a java application. Or build the files manually through maven. 

[netty-producer] Direct communication. Needs the IP and Port of the Storm server. Also, the Storm must be running a compatible topology (NettySpout). Sends a JSON message through an encrypted channel (TLS) to Storm.

How to run: Load the project in eclipse as a maven project and run the NettyProducer.java as a java application. Or build the files manually through maven. 
ps.: Be sure that you run Storm before the producer, otherwise you are going to get a "Connection Refused" message.