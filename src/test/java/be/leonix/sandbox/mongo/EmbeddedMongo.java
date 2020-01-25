package be.leonix.sandbox.mongo;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class EmbeddedMongo {
	
	private static final Logger logger = LoggerFactory.getLogger(EmbeddedMongo.class);
	
	// Only one starter for caching extracted executables and library files.
	private static MongodStarter mongodStarter;
	
	private static synchronized MongodStarter getMongodStarter() {
		if (mongodStarter == null) {
			IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
					.defaultsWithLogger(Command.MongoD, logger)
					.daemonProcess(false)
					.build();
			mongodStarter = MongodStarter.getInstance(runtimeConfig);
		}
		return mongodStarter;
	}
	
	private final String mongodHost;
	private final int    mongodPort;
	
	private final MongodProcess mongodProcess;
	
	public EmbeddedMongo(String mongodHost, int mongodPort) {
		this.mongodHost = mongodHost;
		this.mongodPort = mongodPort;
		
		try {
			IMongodConfig mongodConfig = new MongodConfigBuilder()
					.version(Version.Main.V4_0)
					.net(new Net(mongodHost, mongodPort, Network.localhostIsIPv6()))
					.build();
			
			MongodExecutable mongodExecutable = getMongodStarter().prepare(mongodConfig);
			try {
				mongodProcess = mongodExecutable.start();
			} catch (Throwable ex) {
				try {
					throw ex;
				} finally {
					mongodExecutable.stop();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public String getMongodHost() {
		return mongodHost;
	}
	
	public int getMongodPort() {
		return mongodPort;
	}
	
	public void stop() {
		mongodProcess.stop();
	}
}
