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

public class GameView extends View {

    Context context;
    private Paint wallPaint, spacePaint, playerPaint, startPaint, endPaint;
    private int COLS = 21, ROWS = 21;
    private int hMargin, vMargin;
    private int cellSize;
    public Player user = new Player();
    private char[] maze;
    private char[][] mazeArray;

    long start = System.currentTimeMillis();
    int speed = 100;
    long end = start + speed;

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

    public void ResetPlayerPoint() {
        user.x = 1;
        user.y = 0;
    }

    public void loadNextLevel(int level){
        readMaze(level);
        convertMaze();
    }

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
