package com.wilmol.media.tvshows.enricher;

import java.nio.file.Path;
import java.util.List;

/**
 * Enriched TV show.
 *
 * @param showName show name
 * @param showYear show year (first air date)
 * @param seasons seasons
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public record EnrichedTvShow(String showName, int showYear, List<EnrichedSeason> seasons) {

  /**
   * Enriched TV show season.
   *
   * @param seasonNum season number
   * @param directory path to season directory
   * @param episodes episodes
   */
  public record EnrichedSeason(int seasonNum, Path directory, List<EnrichedEpisode> episodes) {}

  /**
   * Enriched TV show episode.
   *
   * @param episodeNum episode number
   * @param file path to episode file (video)
   * @param episodeName episode name
   */
  public record EnrichedEpisode(int episodeNum, Path file, String episodeName) {}
}
