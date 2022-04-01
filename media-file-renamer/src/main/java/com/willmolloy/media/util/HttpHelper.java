package com.willmolloy.media.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HttpClient abstraction.
 *
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public class HttpHelper {

  private static final Logger log = LogManager.getLogger();

  private final OkHttpClient httpClient = new OkHttpClient.Builder().build();

  private final JsonHelper jsonHelper;

  public HttpHelper(JsonHelper jsonHelper) {
    this.jsonHelper = checkNotNull(jsonHelper);
  }

  /**
   * Sends a GET request and deserialises the JSON response.
   *
   * @param url url to send the GET request to
   * @param type deserialised object type
   * @param <T> deserialised object type
   * @return deserialised response
   */
  public <T> T get(String url, Class<T> type) {
    Request request =
        new Request.Builder().url(url).header("Accept", "application/json").get().build();

    String jsonResponseBody = sendRequest(request);

    return jsonHelper.deserialise(jsonResponseBody, type);
  }

  private String sendRequest(Request request) {
    log.debug("Sending request: {}", request);
    try {
      Response response = httpClient.newCall(request).execute();

      if (response.isSuccessful()) {
        log.debug("Received successful response: {}", response);
        String responseBody = response.body().string();
        log.debug("Received response body: {}", responseBody);
        return responseBody;
      }

      String msg = "Received unsuccessful response: %s".formatted(response);
      log.error(msg);
      throw new RuntimeException(msg);
    } catch (IOException e) {
      String msg = "Error sending request: %s".formatted(request);
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }
}
