package ch.hearc.ig.orderresto.persistence.helper;

import java.util.HashMap;
import java.util.Map;

public class CacheProvider<K, V> {
    public final Map<K, V> cache = new HashMap<>();

    private boolean isCacheValid = false;

    public boolean isCacheValid() {
        return isCacheValid;
    }

    public void setCacheInvalid() {
        this.isCacheValid = false;
    }

    public void setCacheValid() {
        this.isCacheValid = true;
    }

    public void refreshCache(Map<K, V> freshCache) {
        this.clearCache();
        cache.putAll(freshCache);
        this.setCacheValid();
    }

    public void clearCache() {
        cache.clear();
    }

}
