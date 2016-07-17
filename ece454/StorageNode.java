package ece454;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

import org.apache.thrift.*;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.*;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.*;
import org.apache.curator.*;
import org.apache.curator.retry.*;
import org.apache.curator.framework.*;

import org.apache.log4j.*;

public class StorageNode {
    static Logger log;

    public static void main(String [] args) throws Exception {
	BasicConfigurator.configure();
	log = Logger.getLogger(StorageNode.class.getName());

	if (args.length != 4) {
	    System.err.println("Usage: java ece454.StorageNode host port zkconnectstring zknode");
	    System.exit(-1);
	}
    String zknode = args[3];


	CuratorFramework curClient =
	    CuratorFrameworkFactory.builder()
	    .connectString(args[2])
	    .retryPolicy(new RetryNTimes(10, 1000))
	    .connectionTimeoutMs(1000)
	    .sessionTimeoutMs(10000)
	    .build();

	curClient.start();
	Runtime.getRuntime().addShutdownHook(new Thread() {
		public void run() {
		    curClient.close();
		}
	    });

	KeyValueService.Processor processor = new KeyValueService.Processor(new KeyValueHandler(args[0], Integer.parseInt(args[1]), curClient, args[3]));
	TServerSocket socket = new TServerSocket(Integer.parseInt(args[1]));
	TThreadPoolServer.Args sargs = new TThreadPoolServer.Args(socket);
	sargs.protocolFactory(new TBinaryProtocol.Factory());
	sargs.transportFactory(new TFramedTransport.Factory());
	sargs.processorFactory(new TProcessorFactory(processor));
	sargs.maxWorkerThreads(64);
	TServer server = new TThreadPoolServer(sargs);
	log.info("Launching server");

	new Thread(new Runnable() {
		public void run() {
		    server.serve();
		}
	    }).start();

	// TODO: create an ephemeral node in ZooKeeper
		String host = args[0];
		host= (host.equalsIgnoreCase("localhost"))? InetAddress.getLocalHost().getHostName() : host;
	 String nodeName = curClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(zknode,
			 String.format("%s-%s",host,args[1]).getBytes());
        log.info("YYYYYYYYYYYYYYYYYYOOOOOO"+nodeName);

        curClient.getChildren().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                log.info("****************" + watchedEvent.toString());
            }
        }).forPath(zknode);
    }
}
