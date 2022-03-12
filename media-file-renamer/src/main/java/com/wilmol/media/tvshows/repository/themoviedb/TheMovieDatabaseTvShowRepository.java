package com.wilmol.media.tvshows.repository.themoviedb;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;

import com.wilmol.media.tvshows.repository.TvShowRepository;
import com.wilmol.media.util.HttpHelper;
import java.net.URLEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client for calling The Movie Database API (for TV Show info).
 *
 * @see <a href=https://developers.themoviedb.org>https://developers.themoviedb.org</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TheMovieDatabaseTvShowRepository implements TvShowRepository {

  private static final Logger log = LogManager.getLogger();

  private final String apiKey;
  private final HttpHelper httpHelper;

  public TheMovieDatabaseTvShowRepository(String apiKey, HttpHelper httpHelper) {
    this.apiKey = checkNotNull(apiKey);
    this.httpHelper = checkNotNull(httpHelper);
  }

  @Override
  public String getEpisodeName(String showName, int firstAirDateYear, int season, int episode) {
    log.info("getEpisodeName({}, {}, s{}, e{})", showName, firstAirDateYear, season, episode);
    int showId = getId(showName, firstAirDateYear);

    // https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details
    String uri =
        "https://api.themoviedb.org/3/tv/%s/season/%s/episode/%s?api_key=%s"
            .formatted(showId, season, episode, apiKey);
    TvEpisodeDetailsResponse response = httpHelper.get(uri, TvEpisodeDetailsResponse.class);
    return response.name();
  }

  private int getId(String showName, int firstAirDateYear) {
    // https://developers.themoviedb.org/3/search/search-tv-shows
    String uri =
        "https://api.themoviedb.org/3/search/tv?api_key=%s&query=%s&first_air_date_year=%s"
            .formatted(apiKey, URLEncoder.encode(showName), firstAirDateYear);
    TvShowSearchResponse response = httpHelper.get(uri, TvShowSearchResponse.class);
    verify(response.results().size() == 1, "Expected exactly one result in response: %s", response);
    return response.results().get(0).id();
  }
}
