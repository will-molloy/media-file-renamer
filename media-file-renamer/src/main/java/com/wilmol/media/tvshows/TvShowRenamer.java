package com.wilmol.media.tvshows;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starting point for renaming TV shows.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class TvShowRenamer {

  private static final Logger log = LogManager.getLogger();

  void run(Path showDir, boolean dryRun) throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    log.info("run(showDir={}, dryRun={}) started", showDir, dryRun);

    TvShow tvShow = TvShow.parse(showDir);

    log.info("run finished - elapsed: {}", stopwatch.elapsed());
  }

  private void nameEpisodes(Path seasonDirectory, int season, boolean dryRun) throws IOException {
    // may need to rename the files manually first
    // E.g. "Episode 10" comes before "Episode 9"
    // So name it like "Episode 09"
    List<Path> episodes = Files.list(seasonDirectory).sorted().toList();

    log.info("Detected {} episode(s)", episodes.size());

    int i = 1;
    for (Path episode : episodes) {
      String s = Strings.padStart(String.valueOf(season), 2, '0');
      String e = Strings.padStart(String.valueOf(i), 2, '0');

      String name = episode.getFileName().toString();
      String suffix = name.substring(name.lastIndexOf("."));

      String newName = "Episode S%sE%s%s".formatted(s, e, suffix);
      Path newPath = episode.resolveSibling(newName);

      log.info("Renaming ({}/{}): {} -> {}", i, episodes.size(), episode, newPath);
      if (!dryRun) {
        Files.move(episode, newPath);
      }
      i++;
    }
  }

  public static void main(String[] args) {
    try {
      TvShowRenamer app = new TvShowRenamer();
      app.run(Path.of("J:\\Shows\\Breaking Bad (2008)"), true);
    } catch (Throwable e) {
      log.fatal("Fatal error", e);
    }
  }
}
