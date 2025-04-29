module rbmk_reactor_simulator {
	exports app;
	exports core;
	exports ui;
	requires java.desktop;
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.bson;
	requires org.mongodb.driver.core;
	requires java.dotenv;
}