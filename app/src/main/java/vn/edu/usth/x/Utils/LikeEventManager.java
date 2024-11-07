// LikeEventManager.java
package vn.edu.usth.x.Utils;

import java.util.ArrayList;
import java.util.List;

public class LikeEventManager {
    private static LikeEventManager instance;
    private List<LikeUpdateListener> listeners = new ArrayList<>();

    public interface LikeUpdateListener {
        void onLikeUpdated(String tweetId, boolean isLiked, int newLikeCount);
    }

    private LikeEventManager() {}

    public static synchronized LikeEventManager getInstance() {
        if (instance == null) {
            instance = new LikeEventManager();
        }
        return instance;
    }

    public void addListener(LikeUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(LikeUpdateListener listener) {
        listeners.remove(listener);
    }

    public void notifyLikeUpdate(String tweetId, boolean isLiked, int newLikeCount) {
        for (LikeUpdateListener listener : listeners) {
            listener.onLikeUpdated(tweetId, isLiked, newLikeCount);
        }
    }
}
