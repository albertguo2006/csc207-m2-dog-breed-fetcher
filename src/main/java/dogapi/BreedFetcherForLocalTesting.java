package dogapi;

import dogapi.BreedFetcher.BreedNotFoundException;
import java.util.List;

/**
 * A minimal implementation of the BreedFetcher interface for testing purposes.
 * To avoid excessive calls to the real API, we can primarily test with a local
 * implementation that demonstrates the basic functionality of the interface.
 */
public class BreedFetcherForLocalTesting implements BreedFetcher {
    private int callCount = 0;

    @Override
    public List<String> getSubBreeds(String breed) {
        callCount++;
        if ("hound".equalsIgnoreCase(breed)) {
            return List.of("afghan", "basset");
        }
        return sneakyThrow(new BreedNotFoundException(breed));
    }

    public int getCallCount() {
        return callCount;
    }

    @SuppressWarnings("unchecked")
    private static <T> T sneakyThrow(Throwable t) {
        BreedFetcherForLocalTesting.<RuntimeException>throwUnchecked(t);
        return null; // Unreachable
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwUnchecked(Throwable t) throws E {
        throw (E) t;
    }
}
