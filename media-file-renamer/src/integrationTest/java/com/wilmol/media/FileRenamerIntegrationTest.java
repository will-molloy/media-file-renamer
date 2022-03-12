package com.wilmol.media;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

/**
 * FileRenamerIntegrationTest.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class FileRenamerIntegrationTest {

  @Test
  void name() {
    assertThat(new FileRenamer()).isNotNull();
  }
}
