package com.wilmol.media;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

/**
 * FileRenamerTest.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class FileRenamerTest {

  @Test
  void name() {
    assertThat(new FileRenamer()).isNotNull();
  }
}
