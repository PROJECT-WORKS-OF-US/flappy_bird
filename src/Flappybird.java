import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Flappybird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdx = boardWidth / 8;
    int birdy = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    int pipeWidth = 64;
    int pipeHeight = 512;
    int gapBetweenPipes = 120;
    int pipeSpacing = 250;  // Horizontal space between pipes

    int backgroundX = 0;
    int scrollSpeed = 2;

    class Bird {
        int x = birdx;
        int y = birdy;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    class Pipe {
        int x;
        int y;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    Bird bird;
    int velocityY = 0;
    int gravity = 1;
    int flapStrength = -8;
    int score = 0;
    boolean gameOver = false;

    javax.swing.Timer gameLoop;
    javax.swing.Timer placePipesTimer;
    ArrayList<Pipe> pipes;
    Random random = new Random();

    Flappybird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        placePipesTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop = new javax.swing.Timer(1000 / 60, this);  // 60 FPS
        gameLoop.start();
    }

    public void placePipes() {
        int randomY = -random.nextInt(pipeHeight / 3);  // Random height variation for the top pipe
        Pipe topPipe = new Pipe(boardWidth, randomY, topPipeImg);
        Pipe bottomPipe = new Pipe(boardWidth, randomY + pipeHeight + gapBetweenPipes, bottomPipeImg);
        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background scrolling
        g.drawImage(backgroundImg, backgroundX, 0, boardWidth, boardHeight, null);
        g.drawImage(backgroundImg, backgroundX + boardWidth, 0, boardWidth, boardHeight, null);

        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("Game Over", boardWidth / 4, boardHeight / 2);
     
        }
    }

    public void move() {
        if (gameOver) return;

        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        bird.y = Math.min(bird.y, boardHeight - bird.height);

        // Background scrolling
        backgroundX -= scrollSpeed;
        if (backgroundX <= -boardWidth) {
            backgroundX = 0;
        }

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x -= scrollSpeed + 2;  // Move pipes left

            if (pipe.x + pipe.width < 0) {
                pipes.remove(i);
                i--;
            }

            if (!pipe.passed && pipe.x + pipe.width < bird.x) {
                score++;
                pipe.passed = true;
            }

            if (bird.getBounds().intersects(pipe.getBounds())) {
                gameOver = true;
            }
        }

        if (bird.y == 0 || bird.y + bird.height >= boardHeight) {
            gameOver = true;
        }

        if (gameOver) {
            gameLoop.stop();
            placePipesTimer.stop();
        }
    }

    public void restartGame() {
        bird.y = boardHeight / 2;
        velocityY = 0;
        score = 0;
        pipes.clear();
        gameOver = false;
        gameLoop.start();
        placePipesTimer.start();
    }

    public void flap() {
        if (!gameOver) {
            velocityY = flapStrength;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            restartGame();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            flap();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        Flappybird game = new Flappybird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
