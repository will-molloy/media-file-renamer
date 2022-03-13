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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
  public Map<Integer, String> getEpisodeNames(String showName, int showYear, int season) {
    log.info("getEpisodeNames(showName={}, showYear={}, season={})", showName, showYear, season);

    int showId = getIdCache.getUnchecked(new GetIdCacheKey(showName, showYear));

    String uri =
        "https://api.themoviedb.org/3/tv/%s/season/%s?api_key=%s".formatted(showId, season, apiKey);
    TvSeasonDetailsResponse response = httpHelper.get(uri, TvSeasonDetailsResponse.class);

    List<TvSeasonDetailsResponse.Episode> episodes = response.episodes();
    return IntStream.rangeClosed(1, episodes.size())
        .boxed()
        .collect(Collectors.toMap(Function.identity(), i -> episodes.get(i - 1).name()));
  }

  private final LoadingCache<GetIdCacheKey, Integer> getIdCache =
      CacheBuilder.newBuilder()
          .build(CacheLoader.from(key -> getShowId(key.showName(), key.showYear())));

  private int getShowId(String showName, int showYear) {
    log.info("getShowId(showName={}, showYear={})", showName, showYear);

    String uri =
        "https://api.themoviedb.org/3/search/tv?api_key=%s&query=%s&first_air_date_year=%s"
            .formatted(apiKey, URLEncoder.encode(showName, StandardCharsets.UTF_8), showYear);
    TvShowSearchResponse response = httpHelper.get(uri, TvShowSearchResponse.class);

    List<TvShowSearchResponse.Result> searchResults = response.results();
    verify(searchResults.size() >= 1, "No search results for: %s (%s)", showName, showYear);
    if (searchResults.size() > 1) {
      log.warn(
          "Multiple search results for: {} ({}): {}. Taking the first one",
          showName,
          showYear,
          searchResults);
    }
    return searchResults.get(0).id();
  }

  private record GetIdCacheKey(String showName, int showYear) {}

  // https://developers.themoviedb.org/3/search/search-tv-shows
  private record TvShowSearchResponse(List<Result> results) {
    private record Result(int id) {}
  }

  // https://developers.themoviedb.org/3/tv-seasons/get-tv-season-details
  private record TvSeasonDetailsResponse(List<Episode> episodes) {
    private record Episode(String name) {}
  }
}
