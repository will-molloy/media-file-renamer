package com.wilmol.media.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JSON deserailiser abstraction.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class JsonDeserialiser {

  private static final Logger log = LogManager.getLogger();

  private final ObjectReader objectReader;

  public JsonDeserialiser() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectReader = objectMapper.reader();
  }

  <T> T deserialise(String json, Class<T> type) {
    try {
      return objectReader.readValue(json, type);
    } catch (IOException e) {
      log.error("Failed to deserialise: %s".formatted(json), e);
      throw new UncheckedIOException(e);
    }
  }
}
