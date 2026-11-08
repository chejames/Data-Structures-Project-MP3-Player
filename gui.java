import acm.program.*; 
import acm.graphics.*; 
import java.awt.*; 
import java.awt.image.BufferedImage; 
import java.util.*; 
import javax.swing.*; 
import java.io.*; 
 
public class gui extends GraphicsProgram {
   private final String trackTitle;
   private final String artistNameText;

   public gui() {
      this("Title", "Artist Name");
   }

   public gui(String trackTitle, String artistName) {
      this.trackTitle = trackTitle;
      this.artistNameText = artistName;
   }

   public static void main(String[] args) { 
    System.out.println("Display scale: ");
        double scale = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration()
        .getDefaultTransform().getScaleX();
System.out.println("Display scale: " + scale);
       new gui().start(args); 
   } 
    //FLAG
   protected GCanvas createGCanvas() {
      return new GCanvas() {
         public Image createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB) {
               public Graphics2D createGraphics() {
                  Graphics2D g2 = super.createGraphics();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                  return g2;
               }
            };
         }
      };
   }

   public void run() { 
        getGCanvas().setOpaque(true);
        getGCanvas().setBackground(Color.BLACK);
        getRootPane().setOpaque(true);
        getRootPane().setBackground(Color.BLACK);
        // JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // if (frame != null) {
        //     frame.setBackground(Color.BLACK);
        //     ((JComponent) frame.getContentPane()).setOpaque(true);
        //     frame.getContentPane().setBackground(Color.BLACK);
        // }
        //FLAG
        final double AREA_HEIGHT = getHeight() - 60; 
        final double AREA_WIDTH = getWidth() - 60; 
        try { 
            Font geist = Font.createFont(Font.TRUETYPE_FONT, new File("GeistMono-Regular.ttf")); 
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
            env.registerFont(geist); 
        
        GRect bg = new GRoundRect(30, 30, AREA_WIDTH, AREA_HEIGHT, 50, 50); 
        bg.setFillColor(new Color(236, 234, 229)); 
        bg.setFilled(true); 
        bg.setColor(new Color(0, 0, 0, 0)); 
        
        add(bg); 
        
        GRect albumImage = new GRoundRect(50, 50, AREA_WIDTH - 40, AREA_HEIGHT/1.7, 20, 20); 
        albumImage.setFillColor(new Color(0, 0, 0)); 
        albumImage.setFilled(true); 
        albumImage.setColor(new Color(0, 0, 0, 0)); 
        
        add(albumImage); 

        double albumBottom = 50 + AREA_HEIGHT / 1.7;
        Font font = geist.deriveFont(36f); 
        
        GLabel title = new GLabel(trackTitle, 50, albumBottom + 40); 
        title.setFont(font); 
        title.setColor(new Color(0x404040)); 
        add(title); 

        font = geist.deriveFont(18f); 

        GLabel artistName = new GLabel(artistNameText, title.getX() + title.getWidth() + 15, albumBottom + 40); 
        artistName.setFont(font); 
        artistName.setColor(new Color(0x404040)); 
        add(artistName); 

        GRect timeTrack = new GRoundRect(50, albumBottom + 55, AREA_WIDTH - 40, 5, 5, 5);
        timeTrack.setFillColor(new Color(200, 196, 190));
        timeTrack.setFilled(true);
        timeTrack.setColor(new Color(200, 196, 190));
        add(timeTrack);

        double progress = (AREA_WIDTH - 40) * 0.4;
        GRect timeProgress = new GRoundRect(50, albumBottom + 55, progress, 5, 5, 5);
        timeProgress.setFillColor(new Color(0x404040));
        timeProgress.setFilled(true);
        timeProgress.setColor(new Color(0x404040));
        add(timeProgress);

        GOval timeKnob = new GOval(45 + progress, albumBottom + 50, 15, 15);
        timeKnob.setFillColor(new Color(0x404040));
        timeKnob.setFilled(true);
        timeKnob.setColor(new Color(0, 0, 0, 0)); 

        add(timeKnob);

        GLabel timeElapsed = new GLabel("00:01 of 03:00", 50, albumBottom + 85); 
        timeElapsed.setFont(font); 
        timeElapsed.setColor(new Color(0x404040)); 

        add(timeElapsed);  

        GRect skip = new GRoundRect(AREA_WIDTH/2 - 25, AREA_HEIGHT - 15, 100, 35, 35, 35); 
        skip.setFillColor(new Color(0x404040)); 
        skip.setFilled(true); 
        skip.setColor(new Color(0, 0, 0, 0)); 
        
        add(skip); 

        GOval pause = new GOval(AREA_WIDTH/2 - 75, AREA_HEIGHT - 15, 35, 35); 
        pause.setFillColor(new Color(0x404040)); 
        pause.setFilled(true); 
        pause.setColor(new Color(0, 0, 0, 0)); 
        add(pause); 

        GImage backIcon = new GImage("skip-back.png", AREA_WIDTH/2 - 15, AREA_HEIGHT - 10);
        backIcon.setSize(25, 25);
        add(backIcon);

        GImage forwardIcon = new GImage("skip-forward.png", AREA_WIDTH/2 + 40, AREA_HEIGHT - 10);
        forwardIcon.setSize(25, 25);
        add(forwardIcon);

        GImage pauseIcon = new GImage("pause.png", AREA_WIDTH/2 - 70, AREA_HEIGHT - 10);
        pauseIcon.setSize(25, 25);
        add(pauseIcon);
      } catch (Exception e) { 
         System.out.println(e); 
      } 
   } 
    
  } 