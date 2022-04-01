package com.willmolloy.media.tvshows.enricher;

import static com.google.common.base.Preconditions.checkNotNull;

import com.willmolloy.media.tvshows.parser.TvShow;
import com.willmolloy.media.tvshows.repository.TvShowRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for enriching TV show data.
 *
 * @author <a href=https://willmolloy.com>Will Molloy</a>
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
    log.info("Enriching data for show: {} ({})", tvShow.showName(), tvShow.showYear());

    List<EnrichedTvShow.EnrichedSeason> seasons = enrichSeasons(tvShow);
    EnrichedTvShow enrichedTvShow =
        new EnrichedTvShow(tvShow.showName(), tvShow.showYear(), seasons);

    log.debug("Enriched TV show: {}", enrichedTvShow);
    return enrichedTvShow;
  }

  private List<EnrichedTvShow.EnrichedSeason> enrichSeasons(TvShow tvShow) {
    return tvShow.seasons().stream()
        .map(
            season -> {
              List<EnrichedTvShow.EnrichedEpisode> episodes = enrichEpisodes(tvShow, season);
              return new EnrichedTvShow.EnrichedSeason(
                  season.seasonNum(), season.directory(), episodes);
            })
        .toList();
  }

  private List<EnrichedTvShow.EnrichedEpisode> enrichEpisodes(TvShow tvShow, TvShow.Season season) {
    Map<Integer, String> episodeNames =
        tvShowRepository.getEpisodeNames(tvShow.showName(), tvShow.showYear(), season.seasonNum());
    if (episodeNames.size() != season.episodes().size()) {
      log.warn(
          "{} found {} episodes for {} ({}) Season {} but parser parsed {} episodes",
          tvShowRepository.getClass().getSimpleName(),
          episodeNames.size(),
          tvShow.showName(),
          tvShow.showYear(),
          season.seasonNum(),
          season.episodes().size());
    }

    return season.episodes().stream()
        .map(
            episode -> {
              Optional<String> episodeName =
                  Optional.ofNullable(episodeNames.get(episode.episodeNum()));
              if (episodeName.isEmpty()) {
                log.warn(
                    "{} did not find episode name for {} ({}) Season {} Episode {}",
                    tvShowRepository.getClass().getSimpleName(),
                    tvShow.showName(),
                    tvShow.showYear(),
                    season.seasonNum(),
                    episode.episodeNum());
              }
              return new EnrichedTvShow.EnrichedEpisode(
                  episode.episodeNum(), episode.file(), episodeName);
            })
        .toList();
  }
}
