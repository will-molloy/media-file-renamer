package com.wilmol.media.tvshows.client;

/**
 * Client for calling TheMovieDb API (for TV Show info).
 *
 * @see <a href=https://developers.themoviedb.org>https://developers.themoviedb.org</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public interface TheMovieDatabaseTvShowClient {

  /**
   * Get TV show id.
   *
   * @param showName show name
   * @param firstAirDateYear show year
   * @return show id
   * @see <a
   *     href=https://developers.themoviedb.org/3/search/search-tv-shows>https://developers.themoviedb.org/3/search/search-tv-shows</a>
   */
  int getId(String showName, int firstAirDateYear);

  /**
   * Get TV show episode name.
   *
   * @param showId show id
   * @param season season
   * @param episode episode
   * @return show name
   * @see #getId
   * @see <a
   *     href=https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details>https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details</a>
   */
  String getEpisodeName(int showId, int season, int episode);
}
