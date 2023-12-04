package it.unibo.oop.lab.streams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.Map.Entry;
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
        return songs.stream()
                .map(s -> s.getSongName())
                .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet().stream()
            .filter(a -> a.getValue() == year)
            .map(a -> a.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) songs.stream()
            .filter(s -> s.getAlbumName().isPresent())
            .filter(s -> s.getAlbumName().get() == albumName)
            .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream()
            .filter(s -> s.getAlbumName().isEmpty())
            .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream()
            .filter(s -> s.getAlbumName().isPresent())
            .filter(s -> s.getAlbumName().get() == albumName)
            .mapToDouble(Song::getDuration)
            .average();
    }   

    @Override
    public Optional<String> longestSong() {
        return songs.stream()
            .sorted(
                new Comparator<Song>() {

                    @Override
                    public int compare(Song o1, Song o2) {
                        return Double.compare(o2.getDuration(),o1.getDuration());
                    }
                    
                }
            )
            .map(Song::getSongName)
            .findFirst();
    }

    @Override
    public Optional<String> longestAlbum() {
        Map<String, Double> albumDuration = new HashMap<>();
        songs.stream().filter(s -> s.getAlbumName().isPresent()).forEach(
            s -> {
                if(!albumDuration.containsKey(s.getAlbumName().get())){
                    albumDuration.put(s.getAlbumName().get(),s.getDuration());
                } else{
                    albumDuration.put(s.getAlbumName().get(),albumDuration.get(s.getAlbumName().get()) + s.getDuration());
                }
            }

        );
        return albumDuration.entrySet().stream()
            .sorted(
                new Comparator<Entry<String,Double>>() {
                    @Override
                    public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                        return Double.compare(o2.getValue(), o1.getValue());
                    }
                }
            )
            .map(Map.Entry::getKey)
            .findFirst();
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
