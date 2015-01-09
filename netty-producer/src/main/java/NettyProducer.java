

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;

import javax.net.ssl.SSLException;

import util.json.JSONObject;


public class NettyProducer{
	protected static  String host = "127.0.0.1";
	protected static  int port = 8992;

	public static void main(String[] args) {
		connect();
	}
	
static void connect(){
	EventLoopGroup group = new NioEventLoopGroup();
	try {
		// Configure SSL.
		//TODO Change the insecure Trust Manager Factory for a chain of trusted certificates...
		final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		
		Bootstrap bootstrap = new Bootstrap()
		.group(group)
		.channel(NioSocketChannel.class)
		.handler(new NettyConnectionInitializer(sslCtx, host, port));

		Channel channel = bootstrap.connect(host, port).sync().channel();
		NettyChannelSpecification nettyChannel = new NettyChannelSpecification(channel);
		
		
		//Simple JSONObject to ilustrate the example
		JSONObject objToSend = new JSONObject();
		objToSend.put("topic", "I Am Alive");
		while (true) {
			if(!channel.isActive()) {
        		throw new java.nio.channels.ClosedChannelException();
        		}
			//Keep sending the JSON Object until the channel drops...
			System.out.println("[Netty Producer] Sent to network: "+ objToSend.toString());
			nettyChannel.send(objToSend);
			Thread.sleep(1000);
		}

	}
	catch (InterruptedException e) {
		System.err.println("[Netty Producer] Producer Interrupted, restarting the producer.");
		restart(group);
	} catch (SSLException e) {
		System.err.println("[Netty Producer] Restarting because it wasn't possible to establish a safe connection with the server :(");
		restart(group);
	} catch (ClosedChannelException e) {
	 	System.err.println("[Netty Producer] The channel has dropped...");
	 	restart(group);
	}
	catch (Exception e) {
		System.err.println(e.getMessage());
		restart(group);
	}
}
	
static void restart(EventLoopGroup group){
		
		try {
			group.shutdownGracefully();
			System.err.println("[Netty Producer] Retrying in " + 1 + " seconds...");
			Thread.sleep(1000);
			connect();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}	
}

