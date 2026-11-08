import java.io.*;
import java.util.*;
import javazoom.jl.player.Player;

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
            
            
            // test song 1:
            Song song = songs.get(0);
            song.play();
            
            System.out.println("playing song :)");

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
    private Player player;
    
    private volatile boolean paused = false;
   private volatile boolean stopped = true;
   private final Object pauseLock = new Object();

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
    
    
    
   /**
    * Plays the song from the beginning.
    */
   public void play() {
       stop();
   
       try {
           player = new Player(new FileInputStream(fileName));
           stopped = false;
           paused = false;
   
           Thread thread = new Thread(() -> {
               try {
                   while (!stopped) {
   
                       synchronized (pauseLock) {
                           while (paused && !stopped) {
                               pauseLock.wait();
                           }
                       }
   
                       if (stopped || !player.play(1)) {
                           break;
                       }
                   }
               } catch (Exception e) {
                   if (!stopped) {
                       System.out.println("Playback error: " + e.getMessage());
                   }
               }
           });
   
           thread.start();
   
       } catch (Exception e) {
           System.out.println("Could not play song.");
       }
   }
   
   
   /**
    * Pauses the current song.
    */
   public void pause() {
       paused = true;
   }
   
   /**
    * Resumes the paused song.
    */
   public void resume() {
       synchronized (pauseLock) {
           paused = false;
           pauseLock.notifyAll();
       }
   }
   
   /**
    * Completely stops playback.
    */
   public void stop() {
       stopped = true;
       resume();
   
       if (player != null) {
           player.close();
           player = null;
       }
   }  
}
