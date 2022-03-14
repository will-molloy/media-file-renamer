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
public record EnrichedTvShow(String showName, int showYear, List<EnrichedSeason> seasons) {
  public EnrichedTvShow {
    checkArgument(Strings.isNotBlank(showName));
    checkArgument(showYear >= 1900);
    checkArgument(!seasons.isEmpty());
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
      checkArgument(seasonNum > 0);
      checkArgument(Files.isDirectory(directory));
      checkArgument(!episodes.isEmpty());
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
      checkArgument(episodeNum > 0);
      checkArgument(Files.isRegularFile(file));
      checkArgument(Strings.isNotBlank(episodeName));
    }
  }
}
