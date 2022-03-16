package com.wilmol.media.tvshows.repository.themoviedb;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;
import static java.util.stream.Collectors.toUnmodifiableMap;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wilmol.media.tvshows.repository.TvShowRepository;
import com.wilmol.media.util.HttpHelper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 * Client for calling The Movie Database API (for TV Show info).
 *
 * @see <a href=https://developers.themoviedb.org>https://developers.themoviedb.org</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TheMovieDatabase implements TvShowRepository {

  private static final Logger log = LogManager.getLogger();

  private static final String BASE_URL = "https://api.themoviedb.org/3";

  private final String apiKey;
  private final HttpHelper httpHelper;

  public TheMovieDatabase(String apiKey, HttpHelper httpHelper) {
    this.apiKey = checkNotNull(apiKey);
    this.httpHelper = checkNotNull(httpHelper);
  }

  @Override
  public Map<Integer, String> getEpisodeNames(String showName, int showYear, int season) {
    log.debug("getEpisodeNames(showName={}, showYear={}, season={})", showName, showYear, season);

    int showId = getIdCache.getUnchecked(new GetIdCacheKey(showName, showYear));

    String url = "%s/tv/%s/season/%s?api_key=%s".formatted(BASE_URL, showId, season, apiKey);
    TvSeasonDetailsResponse response = httpHelper.get(url, TvSeasonDetailsResponse.class);

    List<TvSeasonDetailsResponse.Episode> episodes = response.episodes();
    return episodes.stream()
        .collect(
            toUnmodifiableMap(
                TvSeasonDetailsResponse.Episode::episode_number,
                TvSeasonDetailsResponse.Episode::name));
  }

  private final LoadingCache<GetIdCacheKey, Integer> getIdCache =
      CacheBuilder.newBuilder()
          .build(CacheLoader.from(key -> getShowId(key.showName(), key.showYear())));

  private int getShowId(String showName, int showYear) {
    log.debug("getShowId(showName={}, showYear={})", showName, showYear);

    String url =
        "%s/search/tv?api_key=%s&query=%s&first_air_date_year=%s"
            .formatted(
                BASE_URL, apiKey, URLEncoder.encode(showName, StandardCharsets.UTF_8), showYear);
    TvShowSearchResponse response = httpHelper.get(url, TvShowSearchResponse.class);

    List<TvShowSearchResponse.Result> searchResults = response.results();
    verify(!searchResults.isEmpty(), "No search results for: %s (%s)", showName, showYear);
    if (searchResults.size() > 1) {
      log.warn("{} search results, taking the first one: {}", searchResults.size(), searchResults);
    }

    TvShowSearchResponse.Result searchResult = searchResults.get(0);
    log.info(
        "Using data for show: {}. First aired: {}. Overview: {}",
        searchResult.name(),
        searchResult.first_air_date(),
        searchResult.overview());
    return searchResult.id();
  }

  private record GetIdCacheKey(String showName, int showYear) {}

  // https://developers.themoviedb.org/3/search/search-tv-shows
  private record TvShowSearchResponse(List<Result> results) {
    TvShowSearchResponse {
      checkNotNull(results, "null results list");
    }

    record Result(int id, String name, LocalDate first_air_date, String overview) {
      Result {
        checkArgument(id > 0, "id (%s) <= 0", id);
        checkArgument(Strings.isNotBlank(name), "blank name");
        checkNotNull(first_air_date, "null first_air_date");
        checkArgument(Strings.isNotBlank(overview), "blank overview");
      }
    }
  }

  // https://developers.themoviedb.org/3/tv-seasons/get-tv-season-details
  private record TvSeasonDetailsResponse(List<Episode> episodes) {
    TvSeasonDetailsResponse {
      checkNotNull(episodes, "null episodes list");
    }

    record Episode(int id, int season_number, int episode_number, String name, String overview) {
      Episode {
        checkArgument(id > 0, "id (%s) <= 0", id);
        checkArgument(season_number >= 0, "season_number (%s) < 0", season_number);
        checkArgument(episode_number > 0, "episode_number (%s) <= 0", episode_number);
        checkArgument(Strings.isNotBlank(name), "blank name");
        checkArgument(Strings.isNotBlank(overview), "blank overview");
      }
    }
  }
}
