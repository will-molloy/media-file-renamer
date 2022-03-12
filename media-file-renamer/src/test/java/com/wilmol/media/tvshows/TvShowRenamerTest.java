package com.wilmol.media.tvshows;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

/**
 * TvShowRenamerTest.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class TvShowRenamerTest {

  @Test
  void name() {
    assertThat(new TvShowRenamer()).isNotNull();
  }
}
