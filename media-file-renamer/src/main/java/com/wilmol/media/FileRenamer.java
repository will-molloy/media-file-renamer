package com.wilmol.media;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * File renamer.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class FileRenamer {

  private static final Logger log = LogManager.getLogger();

  void run(Path directory, boolean dryRun) throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    log.info("run(directory={}, dryRun={}) started", directory, dryRun);

    List<Path> seasons = Files.list(directory).toList();

    log.info("Detected {} season(s)", seasons.size());

    int i = 1;
    for (Path seasonDir : seasons) {
      log.info("Processing Season {}", i);
      nameEpisodes(seasonDir, i, dryRun);
      i++;
    }

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
      FileRenamer app = new FileRenamer();
      app.run(Path.of(""), true);
    } catch (Throwable e) {
      log.fatal("Fatal error", e);
    }
  }
}
