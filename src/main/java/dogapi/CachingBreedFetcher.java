package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher);
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        String key = (breed == null) ? null : breed.toLowerCase(Locale.ROOT);

        // Return cached value if present
        if (key != null && cache.containsKey(key)) {
            return cache.get(key);
        }

        // Record the call to the underlying fetcher
        callsMade++;

        // Delegate to underlying fetcher; may throw BreedNotFoundException via sneaky-throw.
        List<String> result = fetcher.getSubBreeds(breed);

        // Cache successful results only (not exceptions)
        List<String> copy = List.copyOf(result);
        if (key != null) {
            cache.put(key, copy);
        }
        return copy;
    }

    public int getCallsMade() {
        return callsMade;
    }
}