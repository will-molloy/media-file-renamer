package com.wilmol.media.tvshows;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.wilmol.media.tvshows.enricher.EnrichedTvShow;
import com.wilmol.media.tvshows.enricher.TvShowEnricher;
import com.wilmol.media.tvshows.parser.TvShowParser;
import com.wilmol.media.tvshows.repository.themoviedb.TheMovieDatabase;
import com.wilmol.media.util.HttpHelper;
import com.wilmol.media.util.JsonHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starting point for renaming TV shows.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class TvShowRenamer {

  private static final Logger log = LogManager.getLogger();

  private final TvShowParser tvShowParser;
  private final TvShowEnricher tvShowEnricher;

  TvShowRenamer(TvShowParser tvShowParser, TvShowEnricher tvShowEnricher) {
    this.tvShowParser = checkNotNull(tvShowParser);
    this.tvShowEnricher = checkNotNull(tvShowEnricher);
  }

  void run(Path showDir, boolean dryRun) throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    log.info("run(showDir={}, dryRun={}) started", showDir, dryRun);

    EnrichedTvShow tvShow = tvShowEnricher.enrich(tvShowParser.parse(showDir));

    for (EnrichedTvShow.EnrichedSeason season : tvShow.seasons()) {
      log.info("Processing season {} ({} episodes)", season.seasonNum(), season.episodes().size());
      for (EnrichedTvShow.EnrichedEpisode episode : season.episodes()) {
        String fileName = episode.file().getFileName().toString();
        String videoFileSuffix = fileName.substring(fileName.lastIndexOf("."));

        String newFileName =
            "%s S%sE%s %s%s"
                .formatted(
                    tvShow.showName(),
                    padLength2(season.seasonNum()),
                    padLength2(episode.episodeNum()),
                    episode.episodeName().replaceAll("[.]", ""),
                    videoFileSuffix);
        Path newPath = episode.file().resolveSibling(newFileName);

        log.info("Renaming: {} -> {}", episode.file(), newPath);
        if (!dryRun) {
          Files.move(episode.file(), newPath);
        }
      }
    }

    log.info("run finished - elapsed: {}", stopwatch.elapsed());

    if (dryRun) {
      log.info("Dry run. Please check the above output");
    }
  }

  private String padLength2(int i) {
    return Strings.padStart(String.valueOf(i), 2, '0');
  }

  public static void main(String[] args) {
    try {
      String movieDbApiKey = System.getenv("THE_MOVIE_DB_API_KEY");
      checkNotNull(movieDbApiKey, "THE_MOVIE_DB_API_KEY not set");
      TheMovieDatabase theMovieDatabase =
          new TheMovieDatabase(movieDbApiKey, new HttpHelper(new JsonHelper()));

      TvShowParser tvShowParser = new TvShowParser();
      TvShowEnricher tvShowEnricher = new TvShowEnricher(theMovieDatabase);
      TvShowRenamer app = new TvShowRenamer(tvShowParser, tvShowEnricher);

      app.run(Path.of("J:\\Shows\\Breaking Bad (2008)"), true);
    } catch (Throwable e) {
      log.fatal("Fatal error", e);
    }
  }
}
