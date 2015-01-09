
/*
 * Netty Topology
 * Queueless (No Kafka like queue) Topology to monitor and rank the load of network assets
 */



import backtype.storm.Config;
import backtype.storm.topology.TopologyBuilder;

public class NettyTopology {
	// Port in the server machine dedicated to netty communication.
		final public static Integer nettyServerPort = 8992;

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = createTopology();

		Config config = new Config();
		config.setDebug(true);
		//config.setMaxSpoutPending(1);
		try {
			StormRunner.runTopologyLocally(builder.createTopology(),
					"NettySpoutTest", config, 0);
		} catch (InterruptedException e) {
			System.out.println("\n\n Execution interrupted. \n\n");
		}
	}

	static private TopologyBuilder createTopology() {

		TopologyBuilder topology = new TopologyBuilder();
		// One single Netty spout listening for clients connections
		topology.setSpout("NettySpout", new NettySpout(nettyServerPort),1);

		// Various (2) fetcher bolts -> shuffle grouping from feed spout
		//topology.setBolt("PrettyPrinterBolt", new PrettyPrinterBolt(), 1).shuffleGrouping("NettySpout");
		
		
		//topology.setBolt("TestBolt", new PrettyPrinterBolt(), 2).fieldsGrouping("NettyBoltParser", new Fields(CONFIG_FILE.topic));
		return topology;
	}





}
