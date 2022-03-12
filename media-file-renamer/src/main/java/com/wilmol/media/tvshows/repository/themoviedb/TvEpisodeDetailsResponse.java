package com.wilmol.media.tvshows.repository.themoviedb;

/**
 * GET TV episode details response.
 *
 * @param name episode name
 * @see <a
 *     href=https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details>https://developers.themoviedb.org/3/tv-episodes/get-tv-episode-details</a>
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
record TvEpisodeDetailsResponse(String name) {}
