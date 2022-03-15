package com.wilmol.media.tvshows;

import static com.google.common.base.Preconditions.checkNotNull;

import com.wilmol.media.tvshows.enricher.TvShowEnricher;
import com.wilmol.media.tvshows.parser.TvShowParser;
import com.wilmol.media.tvshows.repository.themoviedb.TheMovieDatabase;
import com.wilmol.media.util.HttpHelper;
import com.wilmol.media.util.JsonHelper;

/**
 * Factory for constructing {@link TvShowRenamer}.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public final class TvShowRenamerFactory {

  /**
   * Constructs {@link TvShowRenamer}.
   *
   * @return {@link TvShowRenamer}
   */
  public static TvShowRenamer construct() {
    String movieDbApiKey = System.getenv("THE_MOVIE_DB_API_KEY");
    checkNotNull(movieDbApiKey, "THE_MOVIE_DB_API_KEY not set");
    TheMovieDatabase theMovieDatabase =
        new TheMovieDatabase(movieDbApiKey, new HttpHelper(new JsonHelper()));

    TvShowParser tvShowParser = new TvShowParser();
    TvShowEnricher tvShowEnricher = new TvShowEnricher(theMovieDatabase);
    return new TvShowRenamer(tvShowParser, tvShowEnricher);
  }

  private TvShowRenamerFactory() {}
}
