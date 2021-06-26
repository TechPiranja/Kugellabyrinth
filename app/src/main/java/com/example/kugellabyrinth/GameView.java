package com.example.kugellabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;

/**
 * The GameView which draws the Maze and Player on a Canvas
 */
public class GameView extends View {

    private Context context;
    private Paint wallPaint, spacePaint, playerPaint, startPaint, endPaint;
    private int COLS = 21, ROWS = 21;
    private int hMargin, vMargin;
    private int cellSize;
    /**
     * The Player Position
     */
    public Player user = new Player();
    private char[] maze;
    private char[][] mazeArray;
    private long start = System.currentTimeMillis();
    private int speed = 100;
    private long end = start + speed;

    /**
     * Instantiates a new Game view.
     *
     * @param context the context
     */
    public GameView(Context context) {
        super(context);
        this.context = context;
        user.x = 1;
        user.y = 0;
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        startPaint = new Paint();
        startPaint.setColor(Color.BLUE);

        endPaint = new Paint();
        endPaint.setColor(Color.GREEN);

        spacePaint = new Paint();
        spacePaint.setColor(Color.WHITE);

        mazeArray = new char[COLS + 2][ROWS + 2];
        readMaze(0);
        convertMaze();
    }

    /**
     * reads the Maze out of a textfile with the correct level
     */
    private void readMaze(int level) {
        try {
            InputStream is = context.getAssets().open("mazeGen" + level + ".txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String mazeBuffer = new String(buffer);
            maze = mazeBuffer.toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts the maze from the textfile into a 2D array
     */
    private void convertMaze() {
        int y = 0;
        int x = 0;
        for (int i = 0; i < maze.length; i++) {
            mazeArray[x][y] = maze[i];
            x++;

            if (maze[i] == '\n') {
                y++;
                x = 0;
            }
        }
    }

    /**
     * Reset player point.
     */
    public void ResetPlayerPoint() {
        user.x = 1;
        user.y = 0;
    }

    /**
     * Load next level from textfile and draw it onto the canvas
     *
     * @param level the level
     */
    public void loadNextLevel(int level){
        readMaze(level);
        convertMaze();
    }

    /**
     * Sets the new Player Position
     *
     * @param x the x
     * @param y the y
     */
    public void PlayerInput(float x, float y) {
        if (System.currentTimeMillis() < end) return;
        start = System.currentTimeMillis();
        end = start + speed;

        if (y > 0.5f && user.y <= ROWS-2 && mazeArray[(int) user.x][(int) user.y+1] != '#'){
            user.y = user.y + 1;
            invalidate();
        } else if (y < -0.5f && user.y > 1 && mazeArray[(int) user.x][(int) user.y-1] != '#') {
            user.y = user.y - 1;
            invalidate();
        }

        if (x < 0.5f && user.x <= COLS-2 && mazeArray[(int) user.x+1][(int) user.y] != '#'){
            user.x = user.x + 1;
            invalidate();
        } else if (x > -0.5f && user.x > 1 && mazeArray[(int) user.x-1][(int) user.y] != '#') {
            user.x = user.x - 1;
            invalidate();
        }
    }

    /**
     * Draws the maze and the player onto the canvas
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width/height < COLS/ROWS)
            cellSize = width/(COLS+1);
        else
            cellSize = height/(ROWS+1);

        hMargin = (width-COLS*cellSize)/2;
        vMargin = (height-ROWS*cellSize)/2;
        canvas.translate(hMargin, vMargin);

        // draw maze
        for (int x = 0; x < mazeArray.length; x++){
            for (int y = 0; y < mazeArray[x].length; y++) {
                if(mazeArray[x][y] == '#') {
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, wallPaint);
                } else if(mazeArray[x][y] == ' ') {
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, spacePaint);
                } else if(mazeArray[x][y] == 's') {
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, startPaint);
                } else if(mazeArray[x][y] == 'e') {
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, endPaint);
                }
            }
        }

        // draw player
        canvas.drawOval(user.x*cellSize,user.y*cellSize,(user.x+1)*cellSize,(user.y+1)*cellSize, playerPaint);
    }
}
