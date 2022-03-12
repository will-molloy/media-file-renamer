package com.wilmol.media.tvshows.repository;

/**
 * Client for calling The Movie Database API (for TV Show info).
 *
 * @see <a href=https://developers.themoviedb.org>https://developers.themoviedb.org</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TheMovieDatabaseTvShowRepository implements TvShowRepository {

  @Override
  public String getEpisodeName(String showName, int firstAirDateYear, int episode, int season) {
    // https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details
    int id = getId(showName, firstAirDateYear);
    return null;
  }

  private int getId(String showName, int firstAirDateYear) {
    // https://developers.themoviedb.org/3/search/search-tv-shows
    return 0;
  }
}
