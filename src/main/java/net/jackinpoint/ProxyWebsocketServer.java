package net.jackinpoint;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Class ProxyWebsocketServer.
 */
public class ProxyWebsocketServer extends WebSocketServer {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private final MessagesBacklog messagesBacklog;

    public ProxyWebsocketServer(int port, MessagesBacklog messagesBacklog) {
        super(new InetSocketAddress(port));

        this.messagesBacklog = messagesBacklog;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected!");

        sendBacklog(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " disconnected!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOGGER.info(String.format("[%s] RECV_MESSAGE: '%s'", conn.getRemoteSocketAddress().getAddress().getHostAddress(), message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        throw new RuntimeException(ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("Server started on port " + getPort());

        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    /**
     * Send backlog to newly connected client.
     *
     * @param conn WebSocket
     */
    private void sendBacklog(final WebSocket conn) {
        for (String message : messagesBacklog.get()) {
            conn.send(message);
        }
    }
}
