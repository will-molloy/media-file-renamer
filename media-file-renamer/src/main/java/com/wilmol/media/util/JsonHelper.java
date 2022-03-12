package com.wilmol.media.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JSON abstraction.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class JsonHelper {

  private static final Logger log = LogManager.getLogger();

  private final ObjectReader objectReader;
  private final ObjectWriter objectWriter;

  public JsonHelper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    objectReader = objectMapper.reader();
    objectWriter = objectMapper.writer();
  }

  /**
   * Deserialise JSON.
   *
   * @param json JSON to deserialise
   * @param type deserialised object type
   * @param <T> deserialised object type
   * @return deserialised object
   */
  public <T> T deserialise(String json, Class<T> type) {
    try {
      return objectReader.readValue(json, type);
    } catch (IOException e) {
      String msg = "Failed to deserialise: %s".formatted(json);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }

  /**
   * Serialise JSON.
   *
   * @param obj object to serialise
   * @return JSON
   */
  public String serialise(Object obj) {
    try {
      return objectWriter.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      String msg = "Failed to serialise: %s".formatted(obj);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }
}
