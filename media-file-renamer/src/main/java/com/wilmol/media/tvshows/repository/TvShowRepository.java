package com.wilmol.media.tvshows.repository;

import java.util.Map;

/**
 * Abstraction to get TV show data.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public interface TvShowRepository {

  /**
   * Get TV show episode name.
   *
   * @param showName show name
   * @param showYear show year
   * @param season season number
   * @return map of [episode number -> episode name]
   */
  Map<Integer, String> getEpisodeNames(String showName, int showYear, int season);
}
