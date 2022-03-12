package com.wilmol.media.tvshows;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

/**
 * TvShowRenamerIntegrationTest.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class TvShowRenamerIntegrationTest {

  @Test
  void name() {
    assertThat(new TvShowRenamer()).isNotNull();
  }
}
