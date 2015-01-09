

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.ISpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;


//IRichSpout??
public class NettySpout extends BaseRichSpout implements ISpout, IComponent  {

	static private int PORT;
	public static final Logger LOG = LoggerFactory.getLogger(NettySpout.class);
	private static final long serialVersionUID = 1L;
	final NettySpout self = this;


	public NettySpout(int port) {
		PORT = port;
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		setCollector(collector);
		//Thread to listen the Netty Clients.
		new Thread(new Runnable() {
			NettySpout spout = self;
			public void run() {

				while(true){
					EventLoopGroup bossGroup = new NioEventLoopGroup(1);
					EventLoopGroup workerGroup = new NioEventLoopGroup();
					try {
						//SSL Connection
						SelfSignedCertificate ssc = new SelfSignedCertificate();
						SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());

						ServerBootstrap b = new ServerBootstrap();
						b.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.INFO))
						.childHandler(new NettySpoutServerInitializer(spout,sslCtx));

						b.bind(PORT).sync().channel().closeFuture().sync();
					} catch (Exception e) {

						e.printStackTrace();
					}  finally {
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
					}
				}

			}
		}).start();
	}


	public void nextTuple() {

	}

	@Override
	public void ack(Object msgId) {
		send((Values)msgId);
	}

	@Override
	public void fail(Object msgId) {
		send((Values)msgId);
	}

	private SpoutOutputCollector collector;

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("JsonUnparsed"));

	}

	public void setCollector (SpoutOutputCollector collector){
		this.collector = collector;
	}

	public void send(Values value){
		collector.emit(value);
	}

	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		Channel incoming = ctx.channel();
		send(new Values (msg));


	}

}