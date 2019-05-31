package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

class Vec2 {
    static final Vec2 DIRECTION_LEFT  = new Vec2(-1, 0);
    static final Vec2 DIRECTION_RIGHT = new Vec2(1, 0);
    static final Vec2 DIRECTION_UP    = new Vec2(0, -1);
    static final Vec2 DIRECTION_DOWN  = new Vec2(0, 1);

    int x;
    int y;

    Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean equals(Vec2 vec) {
        return x == vec.x && y == vec.y;
    }

    void translate(Vec2 vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    void scale(int scale) {
        this.x *= scale;
        this.y *= scale;
    }

    Vec2 getCopy() {
        return new Vec2(x, y);
    }
}

class SnakeGame extends JPanel implements ActionListener {
    private static final int scale = 40;
    private static final int fieldWidth = 800;
    private static final int fieldHeight = 600;
    private static final Color snakeColour = new Color(200, 29, 255);
    private static final Color foodColor = new Color(253, 41, 255);
    private static final Color backgroundColor = new Color(255, 167, 103);

    private ArrayList<Vec2> snakeBody = new ArrayList<>();
    private Vec2 foodLocation;
    private Vec2 direction = Vec2.DIRECTION_RIGHT;
    private boolean directionChanged = false;

    SnakeGame() {
        snakeBody.add(new Vec2(0, 0));

        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left pressed");
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right pressed");
        getInputMap().put(KeyStroke.getKeyStroke("UP"), "up pressed");
        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down pressed");

        getActionMap().put("left pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!direction.equals(Vec2.DIRECTION_RIGHT) && !directionChanged)
                    direction = Vec2.DIRECTION_LEFT;

                directionChanged = true;
            }
        });

        getActionMap().put("right pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!direction.equals(Vec2.DIRECTION_LEFT) && !directionChanged)
                    direction = Vec2.DIRECTION_RIGHT;

                directionChanged = true;
            }
        });

        getActionMap().put("up pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!direction.equals(Vec2.DIRECTION_DOWN) && !directionChanged)
                    direction = Vec2.DIRECTION_UP;

                directionChanged = true;
            }
        });

        getActionMap().put("down pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!direction.equals(Vec2.DIRECTION_UP) && !directionChanged)
                    direction = Vec2.DIRECTION_DOWN;

                directionChanged = true;
            }
        });

        setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        generateFoodLocation();
        Timer timer = new Timer(60, this);
        timer.start();
    }

    private void generateFoodLocation() {
        Random generator = new Random();
        boolean foundLocation = false;

        while (!foundLocation) {
            int x = generator.nextInt(fieldWidth / scale) * scale;
            int y = generator.nextInt(fieldHeight / scale) * scale;

            foodLocation = new Vec2(x, y);
            foundLocation = true;

            for (Vec2 snakeRect : snakeBody) {
                if (snakeRect.equals(foodLocation))
                    foundLocation = false;
            }
        }
    }

    private boolean foodEaten() {
        return snakeBody.get(snakeBody.size() - 1).equals(foodLocation);
    }

    private void update() {
        ArrayList<Vec2> newSnakeBody = new ArrayList<>();

        Vec2 dirVec = new Vec2(direction.x, direction.y);
        dirVec.scale(scale);

        if (foodEaten()) {
            generateFoodLocation();
            Vec2 vec = snakeBody.get(snakeBody.size() - 1).getCopy();
            vec.translate(dirVec);
            snakeBody.add(vec);
        }
        else {
            for (int i = 0; i < snakeBody.size() - 1; i++)
                newSnakeBody.add(snakeBody.get(i + 1).getCopy());

            Vec2 vec = snakeBody.get(snakeBody.size() - 1).getCopy();
            vec.translate(dirVec);
            newSnakeBody.add(vec);

            snakeBody = newSnakeBody;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRect(0, 0, fieldWidth, fieldHeight);

        for (Vec2 snakeRect : snakeBody) {
            graphics2D.setColor(snakeColour);
            graphics2D.fillRect(snakeRect.x + 1, snakeRect.y + 1, scale - 1,  scale - 1);
        }

        graphics2D.setColor(foodColor);
        graphics2D.fillRect(foodLocation.x + 1, foodLocation.y + 1, scale - 1, scale - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        directionChanged = false;

        update();
        repaint();
    }
}

public class Application extends JFrame {
    private SnakeGame game = new SnakeGame();

    private void init() {
        setTitle("Snake");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(100, 100);
        setResizable(false);
        add(game, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public void run() {
        init();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Application app = new Application();
            app.run();
        });
    }
}
