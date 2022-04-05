package net.jackinpoint;

import org.apache.log4j.BasicConfigurator;

/**
 * Class Main.
 */
public class Main {
    /**
     * Main run.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
// %TODO, add docker, setup and deploy scripts
        WsProxy wsProxy = new WsProxy();
        wsProxy.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook!");
            wsProxy.stop();
        }));
    }
}
