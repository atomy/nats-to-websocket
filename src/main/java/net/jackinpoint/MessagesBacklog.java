package net.jackinpoint;

import java.util.LinkedList;

/**
 * Class MessagesBacklog.
 */
public class MessagesBacklog {
    private static final int BACKLOG_LIST = 500;

    /**
     * Property to hold messages.
     */
    private final LinkedList<String> messageBacklog;

    /**
     * Constructor.
     */
    public MessagesBacklog() {
        messageBacklog = new LinkedList<String>();
    }

    /**
     * Remove first element from list to emulate a FIFO list.
     *
     * @return String
     */
    public String removeFirst() {
        return messageBacklog.removeFirst();
    }

    /**
     * Add given element, if above limit, remove first element (oldest).
     *
     * @param element String
     */
    public void addElement(String element) {
        messageBacklog.add(element);

        if (messageBacklog.size() > BACKLOG_LIST) {
            messageBacklog.removeFirst();
        }
    }

    /**
     * Return backlog-list.
     *
     * @return LinkedList<String>
     */
    public LinkedList<String> get() {
        return messageBacklog;
    }
}
