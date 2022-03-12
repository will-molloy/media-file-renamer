package com.wilmol.media;

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
    log.info("run(directory={}, dryRun={})", directory, dryRun);

    List<Path> paths = Files.list(directory).toList();

    log.info("Detected {} season(s)", paths.size());

    int season = 1;
    for (Path path : paths) {
      log.info("Processing Season {}", season);
      nameEpisodes(path, season, dryRun);
      season++;
    }
  }

  private void nameEpisodes(Path seasonDirectory, int season, boolean dryRun) throws IOException {
    // may need to rename the files
    // E.g. "Episode 10" comes before "Episode 9"
    // So name it like "Episode 09"
    List<Path> paths = Files.list(seasonDirectory).sorted().toList();

    log.info("Detected {} episode(s)", paths.size());

    int episode = 1;
    for (Path path : paths) {
      String s = Strings.padStart(String.valueOf(season), 2, '0');
      String e = Strings.padStart(String.valueOf(episode), 2, '0');

      String name = path.getFileName().toString();
      String suffix = name.substring(name.lastIndexOf("."));

      String newName = "Episode S%sE%s%s".formatted(s, e, suffix);
      Path newPath = path.resolveSibling(newName);

      log.info("Renaming ({}/{}): {} -> {}", episode, paths.size(), path, newPath);
      if (!dryRun) {
        Files.move(path, newPath);
      }
      episode++;
    }
  }

  public static void main(String[] args) {
    try {
      FileRenamer app = new FileRenamer();
      app.run(Path.of(""), false);
    } catch (Throwable e) {
      log.fatal("Fatal error", e);
    }
  }
}
