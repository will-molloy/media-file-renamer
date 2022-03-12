package com.wilmol.media.tvshows.repository.themoviedb;

import java.util.List;

/**
 * GET TV show by search response.
 *
 * @param results search results
 * @see <a
 *     href=https://developers.themoviedb.org/3/search/search-tv-shows>https://developers.themoviedb.org/3/search/search-tv-shows</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
record TvShowSearchResponse(List<Result> results) {

  /**
   * Search result.
   *
   * @param id show id
   */
  record Result(int id) {}
}
