package com.wilmol.media.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Range;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HttpClient abstraction.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class HttpHelper {

  private static final Logger log = LogManager.getLogger();

  private static final Duration TIMEOUT = Duration.ofSeconds(30);
  private static final Range<Integer> SUCCESSFUL_CODES = Range.closedOpen(200, 300);

  private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
  private final JsonHelper jsonHelper;

  public HttpHelper(JsonHelper jsonHelper) {
    this.jsonHelper = checkNotNull(jsonHelper);
  }

  /**
   * Sends a GET request and deserialises the JSON response.
   *
   * @param uri uri to send the GET request to
   * @param type deserialised object type
   * @param <T> deserialised object type
   * @return deserialised response
   */
  public <T> T get(String uri, Class<T> type) {
    HttpRequest request =
        HttpRequest.newBuilder().GET().uri(URI.create(uri)).timeout(TIMEOUT).build();

    HttpResponse<String> response = sendRequest(request);

    String body = response.body();
    return jsonHelper.deserialise(body, type);
  }

  private HttpResponse<String> sendRequest(HttpRequest request) {
    log.debug("Sending request: {}", request);
    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (SUCCESSFUL_CODES.contains(response.statusCode())) {
        return response;
      }

      String msg =
          "Error sending request: %s. Received unsuccessful status code: %s"
              .formatted(request, response.statusCode());
      log.error(msg);
      throw new RuntimeException(msg);
    } catch (IOException | InterruptedException e) {
      String msg = "Error sending request %s".formatted(request);
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }
}
