package com.wilmol.media.tvshows.parser;

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
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public record TvShow(String showName, int showYear, List<Season> seasons) {
  public TvShow {
    checkArgument(Strings.isNotBlank(showName));
    checkArgument(showYear >= 1900);
    checkArgument(!seasons.isEmpty());
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
      checkArgument(seasonNum > 0);
      checkArgument(Files.isDirectory(directory));
      checkArgument(!episodes.isEmpty());
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
      checkArgument(episodeNum > 0);
      checkArgument(Files.isRegularFile(file));
    }
  }
}
