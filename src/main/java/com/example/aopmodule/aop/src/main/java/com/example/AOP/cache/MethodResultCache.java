package com.example.aopmodule.aop.src.main.java.com.example.AOP.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MethodResultCache {

    private static final Logger logger = LoggerFactory.getLogger(MethodResultCache.class);

    private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();

    public CacheEntry get(String key) {
        return store.get(key);
    }

    public void put(String key, Object value, long timestampMillis) {
        store.put(key, new CacheEntry(value, timestampMillis));
    }

    public void removeExact(String key) {
        if (key != null && !key.isEmpty() && store.remove(key) != null) {
            logger.debug("Removed cache entry: {}", key);
        }
    }

    /**
     * Removes exact keys, then removes any entry whose key starts with one of the prefixes.
     */
    public void invalidate(String[] keys, String[] keyPrefixes) {
        if (keys != null) {
            for (String k : keys) {
                removeExact(k);
            }
        }
        if (keyPrefixes == null || keyPrefixes.length == 0) {
            return;
        }
        Iterator<Map.Entry<String, CacheEntry>> it = store.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CacheEntry> e = it.next();
            String k = e.getKey();
            for (String p : keyPrefixes) {
                if (p != null && !p.isEmpty() && k.startsWith(p)) {
                    it.remove();
                    logger.debug("Removed cache entry by prefix {}: {}", p, k);
                    break;
                }
            }
        }
    }

    public static final class CacheEntry {
        public final Object value;
        public final long timestamp;

        public CacheEntry(Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
