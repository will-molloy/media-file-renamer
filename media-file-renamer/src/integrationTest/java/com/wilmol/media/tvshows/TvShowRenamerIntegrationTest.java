package com.wilmol.media.tvshows;

import static com.google.common.truth.Truth8.assertThat;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.common.truth.StreamSubject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TvShowRenamerIntegrationTest.
 *
 * @author <a href=https://wilmol.com>Will Molloy</a>
 */
class TvShowRenamerIntegrationTest {

  private Path testData;
  private Path fakeMkvFile;
  private TvShowRenamer tvShowRenamer;

  @BeforeEach
  void setUp() throws URISyntaxException, IOException {
    testData = Path.of(this.getClass().getSimpleName());
    fakeMkvFile = Path.of(Resources.getResource("fake.mkv").toURI());
    tvShowRenamer = TvShowRenamerFactory.construct();
    tearDown();
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(testData.toFile());
  }

  @Test
  void breakingBad() throws IOException {
    // Given
    Path showRoot = testData.resolve("Breaking Bad (2008)");
    Files.createDirectories(showRoot);

    Path season1 = fakeSeason(showRoot, 1, 7);
    Path season2 = fakeSeason(showRoot, 2, 13);
    Path season3 = fakeSeason(showRoot, 3, 13);
    Path season4 = fakeSeason(showRoot, 4, 13);
    Path season5 = fakeSeason(showRoot, 5, 16);

    // When
    tvShowRenamer.run(showRoot, false);

    // Then
    assertThatTestData()
        .containsExactly(
            season1.resolve("Breaking Bad S01E01 Pilot.mkv"),
            season1.resolve("Breaking Bad S01E02 Cat's in the Bag....mkv"),
            season1.resolve("Breaking Bad S01E03 ...And the Bag's in the River.mkv"),
            season1.resolve("Breaking Bad S01E04 Cancer Man.mkv"),
            season1.resolve("Breaking Bad S01E05 Gray Matter.mkv"),
            season1.resolve("Breaking Bad S01E06 Crazy Handful of Nothin'.mkv"),
            season1.resolve("Breaking Bad S01E07 A No-Rough-Stuff-Type Deal.mkv"),
            season2.resolve("Breaking Bad S02E01 Seven Thirty-Seven.mkv"),
            season2.resolve("Breaking Bad S02E02 Grilled.mkv"),
            season2.resolve("Breaking Bad S02E03 Bit by a Dead Bee.mkv"),
            season2.resolve("Breaking Bad S02E04 Down.mkv"),
            season2.resolve("Breaking Bad S02E05 Breakage.mkv"),
            season2.resolve("Breaking Bad S02E06 Peekaboo.mkv"),
            season2.resolve("Breaking Bad S02E07 Negro y Azul.mkv"),
            season2.resolve("Breaking Bad S02E08 Better Call Saul.mkv"),
            season2.resolve("Breaking Bad S02E09 4 Days Out.mkv"),
            season2.resolve("Breaking Bad S02E10 Over.mkv"),
            season2.resolve("Breaking Bad S02E11 Mandala.mkv"),
            season2.resolve("Breaking Bad S02E12 Phoenix.mkv"),
            season2.resolve("Breaking Bad S02E13 ABQ.mkv"),
            season3.resolve("Breaking Bad S03E01 No Más.mkv"),
            season3.resolve("Breaking Bad S03E02 Caballo sin Nombre.mkv"),
            season3.resolve("Breaking Bad S03E03 I.F.T..mkv"),
            season3.resolve("Breaking Bad S03E04 Green Light.mkv"),
            season3.resolve("Breaking Bad S03E05 Más.mkv"),
            season3.resolve("Breaking Bad S03E06 Sunset.mkv"),
            season3.resolve("Breaking Bad S03E07 One Minute.mkv"),
            season3.resolve("Breaking Bad S03E08 I See You.mkv"),
            season3.resolve("Breaking Bad S03E09 Kafkaesque.mkv"),
            season3.resolve("Breaking Bad S03E10 Fly.mkv"),
            season3.resolve("Breaking Bad S03E11 Abiquiu.mkv"),
            season3.resolve("Breaking Bad S03E12 Half Measures.mkv"),
            season3.resolve("Breaking Bad S03E13 Full Measure.mkv"),
            season4.resolve("Breaking Bad S04E01 Box Cutter.mkv"),
            season4.resolve("Breaking Bad S04E02 Thirty-Eight Snub.mkv"),
            season4.resolve("Breaking Bad S04E03 Open House.mkv"),
            season4.resolve("Breaking Bad S04E04 Bullet Points.mkv"),
            season4.resolve("Breaking Bad S04E05 Shotgun.mkv"),
            season4.resolve("Breaking Bad S04E06 Cornered.mkv"),
            season4.resolve("Breaking Bad S04E07 Problem Dog.mkv"),
            season4.resolve("Breaking Bad S04E08 Hermanos.mkv"),
            season4.resolve("Breaking Bad S04E09 Bug.mkv"),
            season4.resolve("Breaking Bad S04E10 Salud.mkv"),
            season4.resolve("Breaking Bad S04E11 Crawl Space.mkv"),
            season4.resolve("Breaking Bad S04E12 End Times.mkv"),
            season4.resolve("Breaking Bad S04E13 Face Off.mkv"),
            season5.resolve("Breaking Bad S05E01 Live Free or Die.mkv"),
            season5.resolve("Breaking Bad S05E02 Madrigal.mkv"),
            season5.resolve("Breaking Bad S05E03 Hazard Pay.mkv"),
            season5.resolve("Breaking Bad S05E04 Fifty-One.mkv"),
            season5.resolve("Breaking Bad S05E05 Dead Freight.mkv"),
            season5.resolve("Breaking Bad S05E06 Buyout.mkv"),
            season5.resolve("Breaking Bad S05E07 Say My Name.mkv"),
            season5.resolve("Breaking Bad S05E08 Gliding Over All.mkv"),
            season5.resolve("Breaking Bad S05E09 Blood Money.mkv"),
            season5.resolve("Breaking Bad S05E10 Buried.mkv"),
            season5.resolve("Breaking Bad S05E11 Confessions.mkv"),
            season5.resolve("Breaking Bad S05E12 Rabid Dog.mkv"),
            season5.resolve("Breaking Bad S05E13 To'hajiilee.mkv"),
            season5.resolve("Breaking Bad S05E14 Ozymandias.mkv"),
            season5.resolve("Breaking Bad S05E15 Granite State.mkv"),
            season5.resolve("Breaking Bad S05E16 Felina.mkv"))
        .inOrder();
  }

  private Path fakeSeason(Path showRoot, int seasonNum, int numEpisodes) throws IOException {
    Path season = showRoot.resolve("Season %s".formatted(padLength2(seasonNum)));
    Files.createDirectories(season);

    for (int episodeNum = 1; episodeNum <= numEpisodes; episodeNum++) {
      Files.copy(fakeMkvFile, season.resolve("Ep %s.mkv".formatted(padLength2(episodeNum))));
    }

    return season;
  }

  private String padLength2(int i) {
    return Strings.padStart(String.valueOf(i), 2, '0');
  }

  private StreamSubject assertThatTestData() throws IOException {
    return assertThat(Files.walk(testData).filter(Files::isRegularFile));
  }
}
