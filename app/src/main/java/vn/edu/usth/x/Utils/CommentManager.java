// CommentEventManager.java
package vn.edu.usth.x.Utils;

import java.util.ArrayList;
import java.util.List;

public class CommentManager {
    private static CommentManager instance;
    private List<CommentUpdateListener> listeners = new ArrayList<>();

    public interface CommentUpdateListener {
        void onCommentUpdated(String tweetId);
    }

    private CommentManager() {}

    public static synchronized CommentManager getInstance() {
        if (instance == null) {
            instance = new CommentManager();
        }
        return instance;
    }

    public void addListener(CommentUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CommentUpdateListener listener) {
        listeners.remove(listener);
    }

    public void notifyCommentUpdate(String tweetId) {
        for (CommentUpdateListener listener : listeners) {
            listener.onCommentUpdated(tweetId);
        }
    }
}