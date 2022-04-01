# media-file-renamer

[![build](https://github.com/will-molloy/media-file-renamer/workflows/build/badge.svg?branch=main&event=push)](https://github.com/will-molloy/media-file-renamer/actions?query=workflow%3Abuild)
[![integration-test](https://github.com/will-molloy/media-file-renamer/workflows/integration-test/badge.svg?branch=main&event=push)](https://github.com/will-molloy/media-file-renamer/actions?query=workflow%3Aintegration-test)
[![codecov](https://codecov.io/gh/will-molloy/media-file-renamer/branch/main/graph/badge.svg)](https://codecov.io/gh/will-molloy/media-file-renamer)

Quickly renaming Movies, Tv Shows, etc.

## Requirements

- Java 17
- API Key for The Movie Database API
    - See https://developers.themoviedb.org/
    - Store your API key in `THE_MOVIE_DB_API_KEY` env variable

## Usage

### Build and test

```
./gradlew spotlessApply build integrationTest
```

_`integrationTest` hits The Movie Database API_

### TV Show Renaming

1. TV show must be stored in a particular way:
    - Root directory must be named like: `<Show Name> (<Show Year>)`
    - Then subdirectories of seasons:
        - Must be named like: `Season xx`
    - Then episode files:
        - Can be named anyway you want (they're going to be renamed!)
        - However, it's assumed they're contiguous and in order, starting with the first episode
            - i.e. name it like `Ep 09` otherwise `Ep 10` comes before `Ep 9`
    - For example:
      ```
      Breaking Bad (2008)
         ├── Season 01
         │   ├── Ep 101 (Blu-ray 1080p).mkv
         │   ├── Ep 102 (Blu-ray 1080p).mkv
         │   └── Ep 103.mp4
         |   ...
         └── Season 02
         |   ├── Episode 01 (Bluray rip 1080p).mkv
         |   └── Episode 02.mp4
         |   ...
         └── Season 03
             ├── S03E01.mkv
             └── S03E02.mkv
             ...
      ```


2. Run [`TvShowRenamer.main`](media-file-renamer/src/main/java/com/willmolloy/media/tvshows/TvShowRenamer.java)
    - Point `showDir` at root directory of your TV show
    - Set `dryRun`, recommend `true` at first and check the output


3. Results will look like this:
    - `<Show Name> SxxEyy <Episode Name>`
    - Video file extension is retained
    - For example:
      ```
      Breaking Bad (2008)
         ├── Season 01
         │   ├── Breaking Bad S01E01 Pilot.mkv
         │   ├── Breaking Bad S01E02 Cat's in the Bag....mkv
         │   └── Breaking Bad S01E03 ...And the Bag's in the River.mp4
         |   ...
         └── Season 02
         |   ├── Breaking Bad S02E01 Seven Thirty-Seven.mkv
         |   └── Breaking Bad S02E02 Grilled.mp4
         |   ...
         └── Season 03
             ├── Breaking Bad S03E01 No Más.mkv
             └── Breaking Bad S03E02 Caballo sin Nombre.mkv
             ...
      ```
      - see [integration test](media-file-renamer/src/integrationTest/java/com/willmolloy/media/tvshows/TvShowRenamerIntegrationTest.java) for more complete examples
