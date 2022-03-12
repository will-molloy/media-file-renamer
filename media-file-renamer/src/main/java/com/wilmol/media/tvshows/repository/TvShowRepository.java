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
   * @param episode episode number
   * @param season season number
   * @return TV show episode name
   */
  String getEpisodeName(String showName, int firstAirDateYear, int episode, int season);
}
