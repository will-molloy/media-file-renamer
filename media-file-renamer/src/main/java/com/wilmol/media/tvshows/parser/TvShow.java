package com.wilmol.media.tvshows.parser;

import java.nio.file.Path;
import java.util.List;

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
}
