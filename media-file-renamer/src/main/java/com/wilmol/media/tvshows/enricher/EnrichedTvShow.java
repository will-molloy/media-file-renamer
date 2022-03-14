package com.wilmol.media.tvshows.enricher;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

/**
 * TV show with data enriched.
 *
 * @param showName show name
 * @param showYear show year (first air date)
 * @param seasons seasons
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
// TODO duplicate with TvShow class. Anyway to inherit code from another record?
public record EnrichedTvShow(String showName, int showYear, List<EnrichedSeason> seasons) {
  public EnrichedTvShow {
    checkArgument(Strings.isNotBlank(showName), "blank showName");
    checkArgument(showYear > 0, "showYear (%s) <= 0", showYear);
    checkArgument(!seasons.isEmpty(), "empty seasons list");
  }

  /**
   * Enriched TV show season.
   *
   * @param seasonNum season number
   * @param directory path to season directory
   * @param episodes episodes
   */
  public record EnrichedSeason(int seasonNum, Path directory, List<EnrichedEpisode> episodes) {
    public EnrichedSeason {
      checkArgument(seasonNum > 0, "seasonNum (%s) <= 0", seasonNum);
      checkArgument(Files.isDirectory(directory), "directory (%s) is not a directory", directory);
      checkArgument(!episodes.isEmpty(), "empty episodes list");
    }
  }

  /**
   * Enriched TV show episode.
   *
   * @param episodeNum episode number
   * @param file path to episode file (video)
   * @param episodeName episode name
   */
  public record EnrichedEpisode(int episodeNum, Path file, String episodeName) {
    public EnrichedEpisode {
      checkArgument(episodeNum > 0, "episodeNum (%s) <= 0", episodeNum);
      checkArgument(Files.isRegularFile(file), "file (%s) is not a regular file", file);
      checkArgument(Strings.isNotBlank(episodeName), "blank episodeName");
    }
  }
}
