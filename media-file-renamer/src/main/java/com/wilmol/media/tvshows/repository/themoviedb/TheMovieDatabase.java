package com.wilmol.media.tvshows.repository.themoviedb;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wilmol.media.tvshows.repository.TvShowRepository;
import com.wilmol.media.util.HttpHelper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client for calling The Movie Database API (for TV Show info).
 *
 * @see <a href=https://developers.themoviedb.org>https://developers.themoviedb.org</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TheMovieDatabase implements TvShowRepository {

  private static final Logger log = LogManager.getLogger();

  private final String apiKey;
  private final HttpHelper httpHelper;

  public TheMovieDatabase(String apiKey, HttpHelper httpHelper) {
    this.apiKey = checkNotNull(apiKey);
    this.httpHelper = checkNotNull(httpHelper);
  }

  @Override
  public String getEpisodeName(String showName, int firstAirDateYear, int season, int episode) {
    log.info(
        "getEpisodeName(showName={}, firstAirDateYear={}, season={}, episode={})",
        showName,
        firstAirDateYear,
        season,
        episode);

    int showId = getIdCache.getUnchecked(new GetIdCacheKey(showName, firstAirDateYear));

    // https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details
    String uri =
        "https://api.themoviedb.org/3/tv/%s/season/%s/episode/%s?api_key=%s"
            .formatted(showId, season, episode, apiKey);
    TvEpisodeDetailsResponse response = httpHelper.get(uri, TvEpisodeDetailsResponse.class);

    return response.name();
  }

  private final LoadingCache<GetIdCacheKey, Integer> getIdCache =
      CacheBuilder.newBuilder()
          .build(CacheLoader.from(key -> getShowId(key.showName(), key.firstAirDateYear())));

  private int getShowId(String showName, int firstAirDateYear) {
    log.info("getShowId(showName={}, firstAirDateYear={})", showName, firstAirDateYear);

    // https://developers.themoviedb.org/3/search/search-tv-shows
    String uri =
        "https://api.themoviedb.org/3/search/tv?api_key=%s&query=%s&first_air_date_year=%s"
            .formatted(
                apiKey, URLEncoder.encode(showName, StandardCharsets.UTF_8), firstAirDateYear);
    List<TvShowSearchResponse.Result> searchResults =
        httpHelper.get(uri, TvShowSearchResponse.class).results();

    verify(searchResults.size() >= 1, "No search results for: %s (%s)", showName, firstAirDateYear);
    if (searchResults.size() > 1) {
      log.warn(
          "Multiple search results for: {} ({}): {}", showName, firstAirDateYear, searchResults);
    }
    return searchResults.get(0).id();
  }

  private record GetIdCacheKey(String showName, int firstAirDateYear) {}
}
