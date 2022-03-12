package com.wilmol.media.tvshows.repository;

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
   * @param firstAirDateYear show year
   * @param season season number
   * @param episode episode number
   * @return episode name
   */
  String getEpisodeName(String showName, int firstAirDateYear, int season, int episode);
}
