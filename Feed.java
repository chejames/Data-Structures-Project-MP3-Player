import acm.program.*; 
import acm.graphics.*; 
import java.awt.*; 
import java.awt.image.BufferedImage; 
import java.util.*; 
import javax.swing.*; 
import java.io.*;
import javazoom.jl.player.Player;
 
public class Feed extends GraphicsProgram { 
   public static void main(String[] args) { 
       new Feed().start(args); 
   } 

   public void run() { 
        final double AREA_HEIGHT = getHeight() - 60; 
        final double AREA_WIDTH = getWidth() - 60; 
        try {

            loadSongs("songs.csv");

            Font geist = Font.createFont(Font.TRUETYPE_FONT, new File("GeistMono-Regular.ttf")); 
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
            env.registerFont(geist); 
            
            GLabel title = new GLabel("MP3 Player", 30, 60);
            title.setFont(geist.deriveFont(40f));
            title.setColor(new Color(0x404040)); 
            add(title);

            GLabel description = new GLabel("Legally-acquired mp3 tracks according to American law", 30, title.getY() + 30);
            description.setFont(geist.deriveFont(15f));
            description.setColor(new Color(0x404040)); 
            add(description);

            feedItems = new ArrayList<>();
            

            // for (int i = 0; i < 4; i++) {
            //     if (i == 0) {
            //         FeedItem item1 = new FeedItem("Song Title 1", "Artist 1", 30, description.getY(), geist.deriveFont(25f), AREA_WIDTH, AREA_HEIGHT);
            //         feedItems.add(item1);
            //         continue;
            //     }
            //     double nextY = feedItems.get(i - 1).contentBottom();
            //     FeedItem item = new FeedItem("Song Title " + (i + 1), "Artist " + (i + 1), 30, nextY - feedItems.get(i - 1).getHeight()/2, geist.deriveFont(25f), AREA_WIDTH, AREA_HEIGHT);
            //     feedItems.add(item);
            // }

            int i = 0;

            for (Song song : songs) {
                System.out.println(song.getSongName() + " - " +
                song.getAuthor() + " (" +
                song.getLength() + " seconds)");

                if (i == 0) {
                    FeedItem item1 = new FeedItem(song.getSongName(), song.getAuthor(), 30, description.getY(), geist.deriveFont(25f), AREA_WIDTH, AREA_HEIGHT, song);
                    feedItems.add(item1);
                    i++;
                    continue;
                }
                double nextY = feedItems.get(i - 1).contentBottom();
                FeedItem item = new FeedItem(song.getSongName(), song.getAuthor(), 30, nextY - feedItems.get(i - 1).getHeight()/2, geist.deriveFont(25f), AREA_WIDTH, AREA_HEIGHT, song);
                feedItems.add(item);
                i++;
            }

            for (FeedItem item : feedItems) {
                add(item);
            }
            addMouseListeners();

            double contentHeight = 0;
            for (FeedItem item : feedItems) {
                GRectangle bounds = item.getBounds();
                contentHeight = Math.max(contentHeight, bounds.getY() + bounds.getHeight());
            }

            GCanvas canvas = getGCanvas();
            canvas.setPreferredSize(new Dimension(getWidth(), (int) contentHeight + 60));

            getContentPane().removeAll();
            getContentPane().add(new JScrollPane(canvas,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
            getContentPane().revalidate();

        } catch (Exception e) { 
            System.out.println(e); 
        } 
   }

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

   public void mouseClicked(java.awt.event.MouseEvent event) {
      for (FeedItem item : feedItems) {
         if (item.isPlayClicked(event.getX(), event.getY())) {
            new gui(item.getTitle(), item.getArtist(), item.getSong(), item.getLength()).start();
            return;
         }
      }
   }

   private ArrayList<FeedItem> feedItems;

   public static class gui extends GraphicsProgram {
      private final String trackTitle;
      private final String artistName;
      private double exitX;
      private double exitY;
      private double exitSize;
      private double pauseX;
      private double pauseY;
      private double pauseSize;
      private GImage pauseIcon;
      private boolean paused;
      private Song song;
      private int length;
      private int timeElapsed;

      public gui(String trackTitle, String artistName, Song song, int length) {
         this.trackTitle = trackTitle;
         this.artistName = artistName;
         this.song = song;
         this.length = length;
      }

      public void run() {
         double areaHeight = getHeight() - 60;
         double areaWidth = getWidth() - 60;
         try {
            Font geist = Font.createFont(Font.TRUETYPE_FONT, new File("GeistMono-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(geist);

            GRect background = new GRoundRect(30, 30, areaWidth, areaHeight, 50, 50);
            background.setFillColor(new Color(236, 234, 229));
            background.setFilled(true);
            background.setColor(new Color(0, 0, 0, 0));
            add(background);

            GRect albumImage = new GRoundRect(50, 50, areaWidth - 40, areaHeight / 1.7, 20, 20);
            albumImage.setFillColor(Color.BLACK);
            albumImage.setFilled(true);
            albumImage.setColor(new Color(0, 0, 0, 0));
            add(albumImage);

            double albumBottom = 50 + areaHeight / 1.7;
            Font titleFont = geist.deriveFont(36f);
            GLabel title = new GLabel(trackTitle, 50, albumBottom + 40);
            title.setFont(titleFont);
            title.setColor(new Color(0x404040));
            add(title);

            Font controlsFont = geist.deriveFont(18f);
            GLabel artist = new GLabel(artistName, title.getX() + title.getWidth() + 15, albumBottom + 40);
            artist.setFont(controlsFont);
            artist.setColor(new Color(0x404040));
            add(artist);

            song.play();

            // GRect timeTrack = new GRoundRect(50, albumBottom + 55, areaWidth - 40, 5, 5, 5);
            // timeTrack.setFillColor(new Color(200, 196, 190));
            // timeTrack.setFilled(true);
            // timeTrack.setColor(new Color(200, 196, 190));
            // add(timeTrack);

            // double progress = (areaWidth - 40) * 0.4;
            // GRect timeProgress = new GRoundRect(50, albumBottom + 55, progress, 5, 5, 5);
            // timeProgress.setFillColor(new Color(0x404040));
            // timeProgress.setFilled(true);
            // timeProgress.setColor(new Color(0x404040));
            // add(timeProgress);

            // GOval timeKnob = new GOval(45 + progress, albumBottom + 50, 15, 15);
            // timeKnob.setFillColor(new Color(0x404040));
            // timeKnob.setFilled(true);
            // timeKnob.setColor(new Color(0, 0, 0, 0));
            // add(timeKnob);
            String secs = length % 60 < 10 ? "0" + length % 60 : String.valueOf(length % 60);
            String secsElapsed = timeElapsed % 60 < 10 ? "0" + timeElapsed % 60 : String.valueOf(timeElapsed % 60);

            GLabel elapsed = new GLabel(timeElapsed / 60 + ":" + secsElapsed + " of " + length / 60 + ":" + secs, 50, albumBottom + 85);
            elapsed.setFont(controlsFont);
            elapsed.setColor(new Color(0x404040));
            add(elapsed);

            double controlsY = areaHeight - 15;
            pauseSize = 35;
            pauseX = areaWidth / 2 - 40;
            pauseY = controlsY;
            GOval pause = new GOval(pauseX, pauseY, pauseSize, pauseSize);
            pause.setFillColor(new Color(0x404040));
            pause.setFilled(true);
            pause.setColor(new Color(0, 0, 0, 0));
            add(pause);

            exitSize = 35;
            exitX = areaWidth / 2 + 5;
            exitY = controlsY;
            GOval exit = new GOval(exitX, exitY, exitSize, exitSize);
            exit.setFillColor(new Color(0x404040));
            exit.setFilled(true);
            exit.setColor(new Color(0, 0, 0, 0));
            add(exit);

            GImage logout = new GImage("log-out.png", exitX + 5, exitY + 5);
            logout.setSize(25, 25);
            add(logout);

            pauseIcon = new GImage("pause.png", pauseX + 5, pauseY + 5);
            pauseIcon.setSize(25, 25);
            add(pauseIcon);

            addMouseListeners();

            new Thread(() -> {
                while (true) {
                    if (!paused) {
                        timeElapsed++;
                        elapsed.setLabel(timeElapsed / 60 + ":" + (timeElapsed % 60 < 10 ? "0" + timeElapsed % 60 : timeElapsed % 60) + " of " + length / 60 + ":" + secs);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();
            
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      public void mouseClicked(java.awt.event.MouseEvent event) {
         if (event.getX() >= pauseX && event.getX() <= pauseX + pauseSize
               && event.getY() >= pauseY && event.getY() <= pauseY + pauseSize) {
            onPauseClicked();
            return;
         }

         if (event.getX() >= exitX && event.getX() <= exitX + exitSize && event.getY() >= exitY && event.getY() <= exitY + exitSize) {
            new Feed().start(new String[0]);
            
            song.stop();
            //FLAG
            Window playerWindow = SwingUtilities.getWindowAncestor(this);
            if (playerWindow != null) {
            playerWindow.dispose();
            }
         }
      }

      private void onPauseClicked() {
        paused = !paused;
        remove(pauseIcon);

        pauseIcon = new GImage(paused ? "play.png" : "pause.png", pauseX + 5, pauseY + 5);
        pauseIcon.setSize(25, 25);

        add(pauseIcon);

        if (paused) {
            song.pause();
        } else {
            song.resume();
        }
      }
   }
  }

  class FeedItem extends GCompound {

    private double y;
    private double x;
    private String title;
    private String artist;
    private Font font;
    private double AREA_WIDTH;
    private double AREA_HEIGHT;
    private double playX;
    private double playY;
    private double playWidth;
    private double playHeight;
    private Song song;
    private int length;

    double contentBottom() {
        double bottom = 0;
        for (int i = 0; i < getElementCount(); i++) {
            GRectangle b = getElement(i).getBounds();
            bottom = Math.max(bottom, b.getY() + b.getHeight());
        }
        return bottom;
    }

    public FeedItem(String title, String artist, double x, double y, Font font, double AREA_WIDTH, double AREA_HEIGHT, Song song) {
        this.title = title;
        this.artist = artist;
        this.x = x;
        this.y = y;
        this.font = font;
        this.AREA_WIDTH = AREA_WIDTH;
        this.AREA_HEIGHT = AREA_HEIGHT;
        this.song = song;
        this.length = song.getLength();

        GRect bg = new GRoundRect(x, y, AREA_WIDTH, AREA_HEIGHT/1.7, 50, 50);
        bg.setFillColor(new Color(236, 234, 229)); 
        bg.setFilled(true); 
        bg.setColor(new Color(0, 0, 0, 0)); 

        double albumBottom = y + AREA_HEIGHT / 1.7;

        GLabel titleLabel = new GLabel(title, x, albumBottom + 40);
        titleLabel.setFont(font);
        titleLabel.setColor(new Color(0x404040)); 

        GLabel artistLabel = new GLabel(artist, titleLabel.getWidth() + 45, albumBottom + 40);
        artistLabel.setFont(font.deriveFont(15f));
        artistLabel.setColor(new Color(0x404040));

        playX = AREA_WIDTH - 45;
        playY = albumBottom + 15;
        playWidth = 75;
        playHeight = 25;
        GRect playContainer = new GRoundRect(playX, playY, playWidth, playHeight, 25, 25);
        playContainer.setFillColor(new Color(0x404040));
        playContainer.setFilled(true); 
        playContainer.setColor(new Color(0, 0, 0, 0));

        GLabel playLabel = new GLabel("Play");
        playLabel.setFont(font.deriveFont(15f));
        playLabel.setColor(new Color(236, 234, 229)); 

        GRect padding = new GRoundRect(x, albumBottom + 40, 10, 15, 1, 1);
        padding.setFillColor(new Color(0, 0, 0, 0)); 
        padding.setFilled(true); 
        padding.setColor(new Color(0, 0, 0, 0)); 

        add(bg);
        add(titleLabel);
        add(artistLabel);
        add(playContainer);
        add(playLabel, AREA_WIDTH - 45 + (((playContainer.getWidth() - playLabel.getWidth())/2)), albumBottom + 15 + (((playContainer.getHeight() + playLabel.getAscent())/2) - 3));
        add(padding);
    }

    public boolean isPlayClicked(double clickX, double clickY) {
        double localX = clickX - getX();
        double localY = clickY - getY();
        return localX >= playX && localX <= playX + playWidth
                && localY >= playY && localY <= playY + playHeight;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public double getY() {
        return y;
    }

    public Song getSong() {
        return song;
    }

    public int getLength() {
        return length;
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
           System.out.println("Could not play song.\n" + e.getMessage());
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