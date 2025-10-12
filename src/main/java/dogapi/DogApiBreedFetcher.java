package dogapi;

import dogapi.BreedFetcher.BreedNotFoundException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        if (breed == null || breed.trim().isEmpty()) {
            return sneakyThrow(new BreedNotFoundException(breed));
        }

        String normalized = breed.trim().toLowerCase(Locale.ROOT);
        String url = "https://dog.ceo/api/breed/" + normalized + "/list";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";

            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "");
            if (!"success".equalsIgnoreCase(status)) {
                return sneakyThrow(new BreedNotFoundException(breed));
            }

            JSONArray arr = json.optJSONArray("message");
            List<String> result = new ArrayList<>();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    result.add(arr.getString(i));
                }
            }
            return result;
        } catch (IOException | org.json.JSONException e) {
            return sneakyThrow(new BreedNotFoundException(breed));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T sneakyThrow(Throwable t) {
        DogApiBreedFetcher.<RuntimeException>throwUnchecked(t);
        return null; // Unreachable
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwUnchecked(Throwable t) throws E {
        throw (E) t;
    }
}