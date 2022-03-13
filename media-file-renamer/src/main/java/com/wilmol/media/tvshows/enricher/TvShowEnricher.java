package com.wilmol.media.tvshows.enricher;

import static com.google.common.base.Preconditions.checkNotNull;

import com.wilmol.media.tvshows.parser.TvShow;
import com.wilmol.media.tvshows.repository.TvShowRepository;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for enriching TV show data.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
public class TvShowEnricher {

  private static final Logger log = LogManager.getLogger();

  private final TvShowRepository tvShowRepository;

  public TvShowEnricher(TvShowRepository tvShowRepository) {
    this.tvShowRepository = checkNotNull(tvShowRepository);
  }

  /**
   * Enrich {@link TvShow} data.
   *
   * @param tvShow {@link TvShow}
   * @return {@link EnrichedTvShow}
   */
  public EnrichedTvShow enrich(TvShow tvShow) {
    log.info("enrich(tvShow={})", tvShow);

    List<EnrichedTvShow.EnrichedSeason> seasons =
        tvShow.seasons().stream()
            .map(
                season -> {
                  Map<Integer, String> episodeNames =
                      tvShowRepository.getEpisodeNames(
                          tvShow.showName(), tvShow.showYear(), season.seasonNum());
                  if (episodeNames.size() != season.episodes().size()) {
                    log.warn(
                        "Found {} episodes for {} ({}) Season {} but only parsed {} video files",
                        episodeNames.size(),
                        tvShow.showName(),
                        tvShow.showYear(),
                        season.seasonNum(),
                        season.episodes().size());
                  }

                  List<EnrichedTvShow.EnrichedEpisode> episodes =
                      season.episodes().stream()
                          .map(
                              episode -> {
                                String episodeName = episodeNames.get(episode.episodeNum());
                                return new EnrichedTvShow.EnrichedEpisode(
                                    episode.episodeNum(), episode.file(), episodeName);
                              })
                          .toList();
                  return new EnrichedTvShow.EnrichedSeason(
                      season.seasonNum(), season.directory(), episodes);
                })
            .toList();

    EnrichedTvShow enrichedTvShow =
        new EnrichedTvShow(tvShow.showName(), tvShow.showYear(), seasons);
    log.info("Enriched TV show: {}", enrichedTvShow);
    return enrichedTvShow;
  }
}
