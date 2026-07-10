import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;

public class CjGame extends JPanel implements ActionListener, KeyListener {

    static final int WIDTH = 800;
    static final int HEIGHT = 450;

    static final int TITLE = 0;
    static final int LEVEL_SELECT = 1;
    static final int GAME = 2;
    static final int CLEAR = 3;
    static final int END = 4;

    int gameState = TITLE;

    static final int MAX_LEVEL = 6;

final Color BG        = new Color(18, 10, 30);
final Color DARK      = new Color(40, 25, 60);
final Color DOOR      = new Color(255, 120, 220);
final Color DOOR_OPEN = new Color(12, 6, 20);
final Color KEY       = new Color(255, 240, 160);
final Color SPIKE     = new Color(255, 70, 140);
final Color EYES      = new Color(180, 255, 255);
final Color UI        = new Color(240, 220, 255);
final Color HIGHLIGHT = new Color(255, 120, 220);

    int px, py;
    final int pw = 30, ph = 30;
    double vx, vy;
    boolean left, right, jump;
    boolean onGround;

    final double GRAVITY = 0.6;
    final double JUMP = -12;
    final double SPEED = 4;

    int currentLevel = 1;
    int selectedLevel = 1;

    ArrayList<Rectangle> platforms = new ArrayList<>();
    ArrayList<Rectangle> spikes = new ArrayList<>();
    Rectangle keyRect, doorRect;

    Rectangle saw;
    int sawDir = 1;
    double sawAngle = 0;

    boolean hasKey = false;
    boolean enteringDoor = false;
    int doorTimer = 0;

    long startTime;
    long clearTime;

    boolean pendingReset = false;
 
// ===== BLINKING =====
boolean eyesOpen = true;
int blinkTimer = 0;
final int BLINK_INTERVAL = 120;   // frames between blinks
final int BLINK_DURATION = 8;     // frames eyes stay closed


    Timer timer;

    public CjGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BG);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(16, this);
        timer.start();
    }

    // ======================= LEVEL LOADING =======================

    void loadLevel(int level) {
        platforms.clear();
        spikes.clear();
        saw = null;
        hasKey = false;
        enteringDoor = false;
        doorTimer = 0;

        px = 40;
        py = 360;
        vx = vy = 0;

eyesOpen = true;
blinkTimer = 0;

        switch (level) {
            case 1 -> {
                platforms.add(new Rectangle(0, 400, WIDTH, 50));
                platforms.add(new Rectangle(420, 300, 140, 15));
                spikes.add(new Rectangle(160, 380, 60, 20));
                spikes.add(new Rectangle(260, 380, 60, 20));
                keyRect = new Rectangle(470, 270, 26, 14);
                doorRect = new Rectangle(20, 320, 30, 80);
            }
            case 2 -> {
                platforms.add(new Rectangle(0, 400, WIDTH, 50));
                platforms.add(new Rectangle(200, 330, 120, 15));
                platforms.add(new Rectangle(420, 260, 120, 15));
                spikes.add(new Rectangle(140, 380, 80, 20));
                spikes.add(new Rectangle(360, 380, 80, 20));
                keyRect = new Rectangle(450, 230, 26, 14);
                doorRect = new Rectangle(740, 320, 30, 80);
            }
            case 3 -> {
                platforms.add(new Rectangle(0, 400, 220, 50));
                platforms.add(new Rectangle(300, 400, 180, 50));
                platforms.add(new Rectangle(560, 400, 240, 50));
                platforms.add(new Rectangle(620, 320, 120, 15));
                keyRect = new Rectangle(660, 290, 26, 14);
                doorRect = new Rectangle(20, 320, 30, 80);
            }
            case 4 -> {
                platforms.add(new Rectangle(0, 400, WIDTH, 50));
                saw = new Rectangle(200, 360, 40, 40);
                keyRect = new Rectangle(600, 360, 26, 14);
                doorRect = new Rectangle(20, 320, 30, 80);
            }
            case 5 -> {
                platforms.add(new Rectangle(0, 400, 240, 50));
                platforms.add(new Rectangle(320, 400, 240, 50));
                platforms.add(new Rectangle(600, 400, 200, 50));
                spikes.add(new Rectangle(350, 380, 80, 20));
                keyRect = new Rectangle(640, 360, 26, 14);
                doorRect = new Rectangle(20, 320, 30, 80);
            }
            case 6 -> {
                platforms.add(new Rectangle(0, 400, 220, 50));
                platforms.add(new Rectangle(300, 400, 500, 50));
                saw = new Rectangle(320, 360, 40, 40);
                spikes.add(new Rectangle(500, 380, 80, 20));
                keyRect = new Rectangle(700, 360, 26, 14);
                doorRect = new Rectangle(20, 320, 30, 80);
            }
        }

        startTime = System.currentTimeMillis();
    }

    // ======================= UPDATE =======================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState != GAME) {
            repaint();
            return;
        }

        if (saw != null) {
            saw.x += sawDir * 3;
            sawAngle += 0.2;
            if (saw.x < 200 || saw.x > 500) sawDir *= -1;
        }

        if (enteringDoor) {
            px += (doorRect.x - px) * 0.2;
            py += (doorRect.y - py) * 0.2;
            doorTimer++;
            if (doorTimer > 30) {
                clearTime = System.currentTimeMillis() - startTime;
                gameState = CLEAR;
            }

            repaint();
            return;
        }

        vx = 0;
        if (left) vx = -SPEED;
        if (right) vx = SPEED;

        if (jump && onGround) {
            vy = JUMP;
            onGround = false;
        }

        vy += GRAVITY;
        int prevY = py;
        px += vx;
        py += vy;

        Rectangle player = new Rectangle(px, py, pw, ph);
        onGround = false;

        for (Rectangle p : platforms) {
            if (player.intersects(p)) {
                if (vy > 0 && prevY + ph <= p.y) {
                    py = p.y - ph;
                    vy = 0;
                    onGround = true;
                } else if (vy < 0 && prevY >= p.y + p.height) {
                    py = p.y + p.height;
                    vy = 0;
                }
            }
        }

        if (saw != null && player.intersects(saw)) pendingReset = true;
        for (Rectangle s : spikes) if (player.intersects(s)) pendingReset = true;
        if (py > HEIGHT) pendingReset = true;

        if (!hasKey && player.intersects(keyRect)) hasKey = true;
        if (hasKey && player.intersects(doorRect)) enteringDoor = true;

        if (pendingReset) {
            pendingReset = false;
            loadLevel(currentLevel);
        }

// ===== BLINK UPDATE =====
blinkTimer++;
if (blinkTimer > BLINK_INTERVAL && blinkTimer <= BLINK_INTERVAL + BLINK_DURATION) {
    eyesOpen = false;
} else if (blinkTimer > BLINK_INTERVAL + BLINK_DURATION) {
    eyesOpen = true;
    blinkTimer = 0;
}

        repaint();
    }

    // ======================= INPUT =======================

    @Override
    public void keyPressed(KeyEvent e) {

        switch (gameState) {
            case TITLE -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    gameState = LEVEL_SELECT;
            }

            case LEVEL_SELECT -> {
                int col = (selectedLevel - 1) % 3;
                int row = (selectedLevel - 1) / 3;

                if (e.getKeyCode() == KeyEvent.VK_LEFT && col > 0) col--;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && col < 2) col++;
                if (e.getKeyCode() == KeyEvent.VK_UP && row > 0) row--;
                if (e.getKeyCode() == KeyEvent.VK_DOWN && row < 1) row++;

                selectedLevel = row * 3 + col + 1;
                if (selectedLevel > MAX_LEVEL) selectedLevel = MAX_LEVEL;

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    currentLevel = selectedLevel;
                    gameState = GAME;
                    loadLevel(currentLevel);
                }
            }

            case CLEAR -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    currentLevel++;
                    if (currentLevel > MAX_LEVEL) gameState = END;
                    else {
                        gameState = GAME;
                        loadLevel(currentLevel);
                    }
                }
            }

            case END -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameState = TITLE;
                    selectedLevel = 1;
                }
            }
        }

        if (gameState == GAME) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) left = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = true;
            if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = false;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = false;
    }

    @Override public void keyTyped(KeyEvent e) {}

    // ======================= DRAW HELPERS =======================

    void drawCenteredString(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    void drawLevelGrid(Graphics2D g) {
        int size = 80;
        int startX = (WIDTH - (size * 3 + 40)) / 2;
        int startY = 180;

        g.setFont(new Font("Serif", Font.BOLD, 28));

        for (int i = 1; i <= MAX_LEVEL; i++) {
            int index = i - 1;
            int col = index % 3;
            int row = index / 3;

            int x = startX + col * (size + 20);
            int y = startY + row * (size + 20);

            g.setColor(i == selectedLevel ? HIGHLIGHT : DARK);
            g.fillRect(x, y, size, size);

            g.setColor(UI);
            g.drawRect(x, y, size, size);

            String txt = String.valueOf(i);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(txt,
                    x + (size - fm.stringWidth(txt)) / 2,
                    y + size / 2 + fm.getAscent() / 2 - 4);
        }
    }

    void drawKey(Graphics2D g, Rectangle r) {
        g.setColor(KEY);
        g.fillOval(r.x, r.y, 14, 14);
        g.setColor(BG);
        g.fillOval(r.x + 4, r.y + 4, 6, 6);
        g.setColor(KEY);
        g.fillRect(r.x + 14, r.y + 6, 22, 4);
        g.fillRect(r.x + 26, r.y + 10, 4, 6);
        g.fillRect(r.x + 32, r.y + 8, 4, 8);
    }

    void drawSpikes(Graphics2D g, Rectangle r) {
        g.setColor(SPIKE);
        for (int i = 0; i < r.width; i += 10) {
            Polygon spike = new Polygon();
            spike.addPoint(r.x + i, r.y + r.height);
            spike.addPoint(r.x + i + 5, r.y);
            spike.addPoint(r.x + i + 10, r.y + r.height);
            g.fillPolygon(spike);
        }
    }

void drawSaw(Graphics2D g) {
    AffineTransform old = g.getTransform(); // SAVE

    g.translate(saw.x + 20, saw.y + 20);
    g.rotate(sawAngle);
    g.setColor(SPIKE);
    g.fillOval(-20, -20, 40, 40);

    for (int i = 0; i < 8; i++) {
        g.rotate(Math.PI / 4);
        g.fillRect(15, -3, 10, 6);
    }

    g.setTransform(old); // RESTORE
}
    // ======================= SCREENS =======================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(UI);

        switch (gameState) {
            case TITLE -> {
                g2.setFont(new Font("Serif", Font.BOLD, 48));
                drawCenteredString(g2, "ESCAPE ROOM", 190);
                g2.setFont(new Font("Serif", Font.PLAIN, 20));
                drawCenteredString(g2, "Press ENTER to play", 260);
            }
            case LEVEL_SELECT -> {
                g2.setFont(new Font("Serif", Font.BOLD, 36));
                drawCenteredString(g2, "SELECT LEVEL", 130);
                drawLevelGrid(g2);
            }
            case CLEAR -> {
                g2.setFont(new Font("Serif", Font.BOLD, 42));
                drawCenteredString(g2, "LEVEL CLEARED", 180);
                g2.setFont(new Font("Serif", Font.PLAIN, 22));
                drawCenteredString(g2, "Time: " + clearTime / 1000.0 + "s", 240);
                drawCenteredString(g2, "Press ENTER", 290);
            }
            case END -> {
                g2.setFont(new Font("Serif", Font.BOLD, 42));
                drawCenteredString(g2, "YOU'VE ESCAPED", 180);
                g2.setFont(new Font("Serif", Font.PLAIN, 22));
                drawCenteredString(g2, "New levels arriving soon", 240);
                drawCenteredString(g2, "Press ENTER", 290);
            }
            case GAME -> drawGame(g2);
        }
    }

    void drawGame(Graphics2D g) {
        g.setColor(DARK);
        for (Rectangle p : platforms) g.fillRect(p.x, p.y, p.width, p.height);
        for (Rectangle s : spikes) drawSpikes(g, s);
        if (saw != null) drawSaw(g);

        g.setColor(enteringDoor ? DOOR_OPEN : DOOR);
        g.fillRect(doorRect.x, doorRect.y, doorRect.width, doorRect.height);

        if (!hasKey) drawKey(g, keyRect);

        g.setColor(DARK);
        g.fillRect(px, py, pw, ph);

g.setColor(EYES);
if (eyesOpen) {
    g.fillOval(px + 7, py + 10, 5, 5);
    g.fillOval(px + 18, py + 10, 5, 5);
} else {
    g.fillRect(px + 7, py + 12, 5, 2);
    g.fillRect(px + 18, py + 12, 5, 2);
}
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Escape Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new CjGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}