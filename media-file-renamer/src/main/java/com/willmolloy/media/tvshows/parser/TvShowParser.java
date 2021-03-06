package com.willmolloy.media.tvshows.parser;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for parsing TV show directory into Java object.
 *
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public class TvShowParser {

  private static final Logger log = LogManager.getLogger();

  private static final Pattern SHOW_DIR_PATTERN = Pattern.compile("(.+?) [(](\\d{4})[)]");
  private static final Pattern SEASON_DIR_PATTERN = Pattern.compile("Season (\\d{2})");
  private static final Set<String> VIDEO_FILE_EXTENSIONS = Set.of(".mp4", ".mkv", ".avi");

  /**
   * Parse a {@link TvShow}.
   *
   * @param showDir path to show directory
   * @return {@link TvShow}
   */
  public TvShow parse(Path showDir) {
    log.info("Parsing directory: {}", showDir);
    checkArgument(Files.isDirectory(showDir), "%s is not a directory", showDir);
    String showDirName = showDir.getFileName().toString();
    Matcher showDirMatcher = SHOW_DIR_PATTERN.matcher(showDirName);
    checkArgument(
        showDirMatcher.matches(), "Directory %s doesn't match: %s", showDirName, SHOW_DIR_PATTERN);

    String showName = showDirMatcher.group(1);
    int showYear = Integer.parseInt(showDirMatcher.group(2));
    List<TvShow.Season> seasons = parseSeasons(showDir);

    TvShow tvShow = new TvShow(showName, showYear, seasons);
    log.debug("Parsed TV show: {}", tvShow);
    return tvShow;
  }

  private List<TvShow.Season> parseSeasons(Path showDir) {
    try {
      List<Path> seasonDirs = Files.list(showDir).filter(Files::isDirectory).toList();
      log.info("Detected {} season(s)", seasonDirs.size());

      return seasonDirs.stream()
          .map(
              seasonDir -> {
                String seasonDirName = seasonDir.getFileName().toString();
                Matcher seasonDirMatcher = SEASON_DIR_PATTERN.matcher(seasonDirName);
                checkArgument(
                    seasonDirMatcher.matches(),
                    "Directory %s doesnt match: %s",
                    seasonDirName,
                    SEASON_DIR_PATTERN);

                int seasonNum = Integer.parseInt(seasonDirMatcher.group(1));

                List<TvShow.Episode> episodes = parseEpisodes(seasonDir);
                log.info("Detected season {} with {} episode(s)", seasonNum, episodes.size());

                return new TvShow.Season(seasonNum, seasonDir, episodes);
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
      List<Path> episodeFiles =
          Files.list(seasonDir)
              .filter(Files::isRegularFile)
              .filter(
                  file ->
                      VIDEO_FILE_EXTENSIONS.stream()
                          .anyMatch(extension -> file.getFileName().toString().endsWith(extension)))
              .sorted()
              .toList();

      // assumed 'episodeFiles' are contiguous in sorted order starting with the first episode
      // They'll need to name it like 'Episode 09' otherwise 'Episode 10' comes before 'Episode 9'
      // TODO more complex logic to handle that?? I.e. extract episodeNum from file name.
      //  For episodes it can be named many ways, like 101, 102 or Ep 1, Ep 2. Too much conditions.
      //  The whole point is to rename them automatically. Not make the user name it certain way.
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
