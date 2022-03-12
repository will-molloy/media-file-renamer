package com.wilmol.media.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Range;
import java.io.IOException;
import java.net.URI;
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
public class HttpClient {

  private static final Logger log = LogManager.getLogger();

  private static final Duration TIMEOUT = Duration.ofSeconds(30);

  private final java.net.http.HttpClient httpClient =
      java.net.http.HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

  private final JsonDeserialiser jsonDeserialiser;

  public HttpClient(JsonDeserialiser jsonDeserialiser) {
    this.jsonDeserialiser = checkNotNull(jsonDeserialiser);
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
    try {
      HttpRequest request =
          HttpRequest.newBuilder().GET().uri(URI.create(uri)).timeout(TIMEOUT).build();
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (Range.closedOpen(200, 300).contains(response.statusCode())) {
        String body = response.body();
        return jsonDeserialiser.deserialise(body, type);
      }

      String msg =
          "Error sending GET: %s. Received unsuccessful status code: %s"
              .formatted(uri, response.statusCode());
      log.error(msg);
      throw new RuntimeException(msg);
    } catch (IOException | InterruptedException e) {
      String msg = "Error sending GET %s".formatted(uri);
      log.error(msg, e);
      throw new RuntimeException(msg);
    }
  }
}
