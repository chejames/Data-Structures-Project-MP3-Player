import java.util.ArrayList;

public class MusicLibrary {

    private ArrayList<Song> songs;
    private int currentIndex;

    public MusicLibrary() {
        songs = new ArrayList<>();
        currentIndex = -1;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public ArrayList<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    public Song selectSong(int index) {

        if (index >= 0 && index < songs.size()) {
            currentIndex = index;
            return songs.get(currentIndex);
        }

        return null;
    }

    public Song nextSong() {

        if (songs.isEmpty()) {
            return null;
        }

        currentIndex = (currentIndex + 1) % songs.size();

        return songs.get(currentIndex);
    }

    public Song previousSong() {

        if (songs.isEmpty()) {
            return null;
        }

        currentIndex =
            (currentIndex - 1 + songs.size()) % songs.size();

        return songs.get(currentIndex);
    }

    public Song getCurrentSong() {

        if (currentIndex == -1) {
            return null;
        }

        return songs.get(currentIndex);
    }

    public void removeSong(int index) {

        if (index >= 0 && index < songs.size()) {
            songs.remove(index);

            if (songs.isEmpty()) {
                currentIndex = -1;
            } else if (index < currentIndex) {
                currentIndex--;
            } else if (index == currentIndex) {
                currentIndex = Math.min(currentIndex, songs.size() - 1);
            }
        }
    }
}
