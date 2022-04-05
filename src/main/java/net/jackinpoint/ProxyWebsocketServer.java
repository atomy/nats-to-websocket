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

        // invalid auth, drop connection
        // %TODO, make pass a variable
        if (!handshake.getFieldValue("X-AUTH").equals("super-password")) {
            conn.close(1000, "Unauthorized!");
        }

        sendBacklog(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();

        if (remoteSocketAddress != null) {
            LOGGER.info(String.format("[%s] Client disconnected. Code: %d - Reason: %s - Closed-by-remote: %s", remoteSocketAddress.getAddress().getHostAddress(), code, reason, remote ? "yes" : "no"));
        } else {
            LOGGER.info(String.format("Client disconnected. Code: '%d' - Reason: '%s' - Closed-by-remote: '%s'", code, reason, remote ? "yes" : "no"));
        }
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
