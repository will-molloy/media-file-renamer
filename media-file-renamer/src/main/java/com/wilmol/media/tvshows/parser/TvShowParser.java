package com.wilmol.media.tvshows.parser;

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
 * Responsible for parsing TV show directory into Java object.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TvShowParser {

  private static final Logger log = LogManager.getLogger();

  private static final Pattern SHOW_DIR_PATTERN = Pattern.compile("(.+?) [(](\\d{4})[)]");
  private static final Pattern SEASON_DIR_PATTERN = Pattern.compile("Season \\d{2}");

  /**
   * Parse a {@link TvShow}.
   *
   * @param showDir path to show directory
   * @return {@link TvShow}
   */
  public TvShow parse(Path showDir) {
    checkArgument(Files.isDirectory(showDir), "%s is not a directory", showDir);
    String showDirName = showDir.getFileName().toString();
    Matcher showDirMatcher = SHOW_DIR_PATTERN.matcher(showDirName);
    checkArgument(
        showDirMatcher.matches(), "Directory %s doesnt match %s", showDirName, SHOW_DIR_PATTERN);

    String showName = showDirMatcher.group(1);
    int showYear = Integer.parseInt(showDirMatcher.group(2));
    List<TvShow.Season> seasons = parseSeasons(showDir);

    TvShow tvShow = new TvShow(showName, showYear, seasons);
    log.info("Parsed TV show: {}", tvShow);
    return tvShow;
  }

  private List<TvShow.Season> parseSeasons(Path showDir) {
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

      // assumed 'seasonDirs' are in sorted order starting with 'Season 01'
      // They'll need to name it like 'Season 09' otherwise 'Season 10' comes before 'Season 9'
      // TODO more complex logic to handle that??
      return IntStream.rangeClosed(1, seasonDirs.size())
          .mapToObj(
              seasonNum -> {
                List<TvShow.Episode> episodes = parseEpisodes(seasonDirs.get(seasonNum - 1));
                return new TvShow.Season(seasonNum, seasonDirs.get(seasonNum - 1), episodes);
              })
          .toList();
    } catch (IOException e) {
      String msg = "Error parsing seasons for show: %s".formatted(showDir);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }

  private List<TvShow.Episode> parseEpisodes(Path seasonDir) {
    try {
      List<Path> episodeFiles = Files.list(seasonDir).toList();
      for (Path episodeFile : episodeFiles) {
        checkArgument(Files.isRegularFile(episodeFile), "%s is not a regular file", episodeFile);
      }

      // similarly, assumed 'episodeFiles' are in sorted order starting with the first episode
      // They'll need to name it like 'Episode 09' otherwise 'Episode 10' comes before 'Episode 9'
      // TODO more complex logic to handle that??
      //  For episodes it can be named many ways, like 101, 102 or Ep 1, Ep 2. Too much conditions.
      return IntStream.rangeClosed(1, episodeFiles.size())
          .mapToObj(episodeNum -> new TvShow.Episode(episodeNum, episodeFiles.get(episodeNum - 1)))
          .toList();
    } catch (IOException e) {
      String msg = "Error parsing episodes for season: %s".formatted(seasonDir);
      log.error(msg, e);
      throw new UncheckedIOException(msg, e);
    }
  }
}
