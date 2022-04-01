package com.willmolloy.media.tvshows.parser;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

/**
 * Parsed TV show.
 *
 * @param showName show name
 * @param showYear show year (first air date)
 * @param seasons seasons
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public record TvShow(String showName, int showYear, List<Season> seasons) {
  public TvShow {
    checkArgument(Strings.isNotBlank(showName), "blank showName");
    checkArgument(showYear > 0, "showYear (%s) <= 0", showYear);
    checkArgument(!seasons.isEmpty(), "empty seasons list");
  }

  /**
   * TV show season.
   *
   * @param seasonNum season number
   * @param directory path to season directory
   * @param episodes episodes
   */
  public record Season(int seasonNum, Path directory, List<Episode> episodes) {
    public Season {
      checkArgument(seasonNum >= 0, "seasonNum (%s) < 0", seasonNum);
      checkArgument(Files.isDirectory(directory), "directory (%s) is not a directory", directory);
      checkArgument(!episodes.isEmpty(), "empty episodes list");
    }
  }

  /**
   * TV show episode.
   *
   * @param episodeNum episode number
   * @param file path to episode file (video)
   */
  public record Episode(int episodeNum, Path file) {
    public Episode {
      checkArgument(episodeNum > 0, "episodeNum (%s) <= 0", episodeNum);
      checkArgument(Files.isRegularFile(file), "file (%s) is not a regular file", file);
    }
  }
}
