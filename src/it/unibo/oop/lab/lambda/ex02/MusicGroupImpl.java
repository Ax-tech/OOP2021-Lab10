package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(x -> x.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream().filter(x -> x.getValue() == year).map(x -> x.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        if (!this.albums.containsKey(albumName)) {
            throw new IllegalArgumentException("invalid album name");
        }
        return (int) this.songs.stream()
                .filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().equals(Optional.of(albumName)))
                .count();
        /**
         * Standard implementation (without stream and lambda's)
         */
//        int count = 0;
//        for (final Song song : songs) {
//            if (song.getAlbumName().equals(Optional.of(albumName))) {
//              count++;
//            }
//        }
//        return count;
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                .filter(s -> s.getAlbumName().isEmpty())
                .count();
        /**
         * Standard implementation (without stream and lambda's)
         */
//        int count = 0;
//        for (final Song song : songs) {
//            if (song.getAlbumName().equals(Optional.empty())) {
//              count++;
//            }
//        }
//        return count;
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        if (!this.albums.containsKey(albumName)) {
            throw new IllegalArgumentException("invalid album name");
        }
        return this.songs.stream()
                .filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().equals(Optional.of(albumName)))
                .mapToDouble(s -> s.getDuration())
                .average();
        /**
         * Standard implementation (without stream and lambda's)
         */
//        double average = 0;
//        double count = 0;
//        for (final Song song : songs) {
//            if (song.getAlbumName().equals(Optional.of(albumName))) {
//                average += song.getDuration();
//                count++;
//              }
//            }
//        average = average / count;
//        return OptionalDouble.of(average);
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                .collect(Collectors.maxBy((x, y) -> Double.compare(x.getDuration(), y.getDuration())))
                .map(s -> s.getSongName());
        /**
         * Standard implementation (without stream and lambda's)
         */
//        Song longest = new Song(null, null, 0);
//        for (final Song song : songs) {
//            if (song.getDuration() > longest.getDuration()) {
//              longest = song;
//            }
//        }
//        return Optional.of(longest.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream().filter(a -> a.getAlbumName().isPresent())
                .collect(Collectors.groupingBy(Song::getAlbumName, Collectors.summingDouble(Song::getDuration)))
                .entrySet().stream()
                .collect(Collectors.maxBy((x, y) -> Double.compare(x.getValue(), y.getValue())))
                .flatMap(e -> e.getKey());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
