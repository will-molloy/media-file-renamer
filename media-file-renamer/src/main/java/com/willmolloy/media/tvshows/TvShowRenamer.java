package com.willmolloy.media.tvshows;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.willmolloy.media.tvshows.enricher.EnrichedTvShow;
import com.willmolloy.media.tvshows.enricher.TvShowEnricher;
import com.willmolloy.media.tvshows.parser.TvShowParser;
import com.willmolloy.media.tvshows.repository.themoviedb.TheMovieDatabase;
import com.willmolloy.media.util.HttpHelper;
import com.willmolloy.media.util.JsonHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starting point for renaming TV shows.
 *
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
class TvShowRenamer {

  private static final Logger log = LogManager.getLogger();

  private static final Pattern ILLEGAL_PATH_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

  private final TvShowParser tvShowParser;
  private final TvShowEnricher tvShowEnricher;

  TvShowRenamer(TvShowParser tvShowParser, TvShowEnricher tvShowEnricher) {
    this.tvShowParser = checkNotNull(tvShowParser);
    this.tvShowEnricher = checkNotNull(tvShowEnricher);
  }

  private void run(Path showDir, boolean dryRun) throws IOException {
    EnrichedTvShow tvShow = tvShowEnricher.enrich(tvShowParser.parse(showDir));
    int renameCount = 0;

    for (EnrichedTvShow.EnrichedSeason season : tvShow.seasons()) {
      log.info("Processing season {} ({} episodes)", season.seasonNum(), season.episodes().size());

      for (EnrichedTvShow.EnrichedEpisode episode : season.episodes()) {
        String fileName = episode.file().getFileName().toString();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));

        String newFileName =
            "%s S%sE%s%s%s"
                .formatted(
                    tvShow.showName(),
                    padLength2(season.seasonNum()),
                    padLength2(episode.episodeNum()),
                    ILLEGAL_PATH_CHARS
                        .matcher(episode.episodeName().map(s -> " %s".formatted(s)).orElse(""))
                        .replaceAll(""),
                    fileSuffix);
        Path newPath = episode.file().resolveSibling(newFileName);

        if (!episode.file().equals(newPath)) {
          log.info("Renaming: {} -> {}", episode.file(), newPath);
          renameCount++;
          if (!dryRun) {
            Files.move(episode.file(), newPath);
          }
        }
      }
    }

    log.info("Renamed {} file(s)", renameCount);

    if (dryRun) {
      log.info("Dry run. Please check the above output");
    }
  }

  private String padLength2(int i) {
    return Strings.padStart(String.valueOf(i), 2, '0');
  }

  public static void main(String... args) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      checkArgument(args.length == 2, "Expected 2 args");
      // Process 1 show at a time rather than all shows. Some shows require manual intervention
      // (e.g. joint episodes) which can't really be automated. So reprocessing all shows would mess
      // up the data.
      Path showDir = Path.of(args[0]);
      boolean dryRun = Boolean.parseBoolean(args[1]);

      TvShowRenamer app = construct();

      log.info("Running - showDir={}, dryRun={}", showDir, dryRun);
      app.run(showDir, dryRun);
    } catch (Throwable e) {
      log.fatal("Fatal error", e);
    } finally {
      log.info("Elapsed: {}", stopwatch.elapsed());
    }
  }

  private static TvShowRenamer construct() {
    String movieDbApiKey = System.getenv("THE_MOVIE_DB_API_KEY");
    checkNotNull(movieDbApiKey, "THE_MOVIE_DB_API_KEY not set");
    TheMovieDatabase theMovieDatabase =
        new TheMovieDatabase(movieDbApiKey, new HttpHelper(new JsonHelper()));

    TvShowParser tvShowParser = new TvShowParser();
    TvShowEnricher tvShowEnricher = new TvShowEnricher(theMovieDatabase);
    return new TvShowRenamer(tvShowParser, tvShowEnricher);
  }
}
