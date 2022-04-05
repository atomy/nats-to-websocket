package net.jackinpoint;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Class WsProxy.
 */
public class WsProxy {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private ProxyWebsocketServer proxyWebsocketServer;

    private Connection natsConnection;

    private MessagesBacklog messagesBacklog = new MessagesBacklog();

    /**
     * Start proxy and all that's needed.
     */
    public void start() {
        LOGGER.info("Starting WssServer...");
        startWssServer();
        LOGGER.info("Starting WssServer... DONE");

        LOGGER.info("Starting NATS Client...");
        startNatsClient();
        LOGGER.info("Starting NATS Client... DONE");
    }

    /**
     * Create nats-client and connect.
     */
    private void startNatsClient() {
        try {
            // %TODO, make this env var
            natsConnection = Nats.connect("nats://10.8.0.3:4222");
            Dispatcher d = natsConnection.createDispatcher((msg) -> {
                String response = new String(msg.getData(), StandardCharsets.UTF_8);
                LOGGER.info(String.format("[%s] %s", msg.getSubject(), response));
                String jsonString = new JSONObject()
                        .put("timestamp", System.currentTimeMillis())
                        .put("queue", msg.getSubject())
                        .put("message", response)
                        .toString();
                proxyWebsocketServer.broadcast(jsonString);
                messagesBacklog.addElement(jsonString);
            });

            // subscribe to all queues
            d.subscribe("*");
        } catch (Exception exception) {
            LOGGER.severe("ERROR: " + exception);
        }
    }

    /**
     * Create websocket-server.
     */
    private void startWssServer() {
        try {
            proxyWebsocketServer = new ProxyWebsocketServer(8887, messagesBacklog);
            proxyWebsocketServer.start();
        } catch (Exception exception) {
            LOGGER.severe("ERROR: " + exception);
        }
    }

    /**
     * Stop all things.
     */
    public void stop() {
        try {
            natsConnection.close();
            proxyWebsocketServer.stop();
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
