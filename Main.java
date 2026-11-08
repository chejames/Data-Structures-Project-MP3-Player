import java.io.*;
import java.util.*;

public class Main {

    public static ArrayList<Song> songs = new ArrayList<Song>();

    public static void loadSongs(String fileName) throws FileNotFoundException {
        Scanner input = new Scanner(new File(fileName));

        input.nextLine(); 
        songs.clear();

        while (input.hasNextLine()) {
            String line = input.nextLine();
            System.out.println(line);

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] data = splitCSV(line);

            Song song = new Song(
                data[0],
                data[1],
                data[2],
                Integer.parseInt(data[3]),
                data[4],
                data[5]
            );

            songs.add(song);
        }

        input.close();
    }

    //formats each line properly

    public static String[] splitCSV(String line) {
        ArrayList<String> values = new ArrayList<String>();
        String current = "";
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.trim());
                current = "";
            } else {
                current += c;
            }
        }

        values.add(current.trim());

        return values.toArray(new String[0]);
    }


    public static void main(String[] args) {
        try {
            loadSongs("songs.csv");

            for (Song song : songs) {
                System.out.println(song.getSongName() + " - " +
                                   song.getAuthor() + " (" +
                                   song.getLength() + " seconds)");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Could not find songs.csv!");
        }
    }
}


class Song {

    private String songName;
    private String author;
    private String album;
    private int length;
    private String cover;
    private String fileName;

    public Song(String songName, String author, String album,
                int length, String cover, String fileName) {
        this.songName = songName;
        this.author = author;
        this.album = album;
        this.length = length;
        this.cover = cover;
        this.fileName = fileName;
    }

    public String getSongName() {
        return songName;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbum() {
        return album;
    }

    public int getLength() {
        return length;
    }

    public String getCover() {
        return cover;
    }

    public String getFileName() {
        return fileName;
    }
}
