package com.wilmol.media.tvshows;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TV show.
 *
 * @param showName show name
 * @param showYear show year (first air date)
 * @param seasons seasons
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public record TvShow(String showName, int showYear, List<Season> seasons) {

  /**
   * TV show season.
   *
   * @param seasonNum season number
   * @param directory path to season directory
   * @param episodes episodes
   */
  public record Season(int seasonNum, Path directory, List<Episode> episodes) {}

  /**
   * TV show episode.
   *
   * @param episodeNum episode number
   * @param file path to episode file (video)
   */
  public record Episode(int episodeNum, Path file) {}

  private static final Logger log = LogManager.getLogger();

  private static final Pattern SHOW_DIR_PATTERN = Pattern.compile("(.+?) [(](\\d{4})[)]");
  private static final Pattern SEASON_DIR_PATTERN = Pattern.compile("Season \\d{2}");

  /**
   * Parse a {@link TvShow}.
   *
   * @param showDir path to show directory
   * @return {@link TvShow}
   */
  public static TvShow parseTvShow(Path showDir) {
    checkArgument(Files.isDirectory(showDir), "%s is not a directory", showDir);
    String showDirName = showDir.getFileName().toString();
    Matcher showDirMatcher = SHOW_DIR_PATTERN.matcher(showDirName);
    checkArgument(
        showDirMatcher.matches(), "Directory %s doesnt match %s", showDirName, SHOW_DIR_PATTERN);

    String showName = showDirMatcher.group(1);
    int showYear = Integer.parseInt(showDirMatcher.group(2));
    List<Season> seasons = parseSeasons(showDir);

    TvShow tvShow = new TvShow(showName, showYear, seasons);
    log.info("Parsed TV show: {}", tvShow);
    return tvShow;
  }

  private static List<Season> parseSeasons(Path showDir) {
    try {
      List<Path> seasonDirs = Files.list(showDir).toList();
      for (Path seasonDir : seasonDirs) {
        checkArgument(Files.isDirectory(seasonDir), "%s is not a directory", seasonDir);
        String seasonDirName = seasonDir.getFileName().toString();
        Matcher seasonDirMatcher = SEASON_DIR_PATTERN.matcher(seasonDirName);
        checkArgument(
            seasonDirMatcher.matches(),
            "Directory %s doesnt match %s",
            seasonDirName,
            seasonDirMatcher);
      }

      // assumed 'seasonDirs' are in sorted order
      // They'll need to same it like 'Season 09' otherwise 'Season 10' comes before 'Season 9'
      // TODO more complex logic to handle that??
      return IntStream.rangeClosed(1, seasonDirs.size())
          .mapToObj(
              seasonNum -> {
                List<Episode> episodes = parseEpisodes(seasonDirs.get(seasonNum - 1));
                return new Season(seasonNum, seasonDirs.get(seasonNum - 1), episodes);
              })
          .toList();
    } catch (IOException e) {
      String msg = "Error parsing seasons for show: %s".formatted(showDir);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }

  private static List<Episode> parseEpisodes(Path seasonDir) {
    try {
      List<Path> episodeFiles = Files.list(seasonDir).toList();
      for (Path episodeFile : episodeFiles) {
        checkArgument(Files.isRegularFile(episodeFile), "%s is not a regular file", episodeFile);
      }

      // similarly, assumed 'episodeFiles' are in sorted order
      // They'll need to same it like 'Episode 09' otherwise 'Episode 10' comes before 'Episode 9'
      // TODO more complex logic to handle that??
      //  For episodes it can be named many ways, like 101, 102 or Ep 1, Ep 2. Too much conditions.
      return IntStream.rangeClosed(1, episodeFiles.size())
          .mapToObj(episodeNum -> new Episode(episodeNum, episodeFiles.get(episodeNum - 1)))
          .toList();
    } catch (IOException e) {
      String msg = "Error parsing episodes for season: %s".formatted(seasonDir);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }
}
