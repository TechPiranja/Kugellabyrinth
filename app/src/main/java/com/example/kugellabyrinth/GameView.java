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
    private Paint wallPaint, spacePaint, playerPaint, startPaint, finishPaint;
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
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        startPaint = new Paint();
        startPaint.setColor(Color.BLUE);

        finishPaint = new Paint();
        finishPaint.setColor(Color.GREEN);

        spacePaint = new Paint();
        spacePaint.setColor(Color.WHITE);

        mazeArray = new char[COLS+2][ROWS+2];
        readMaze();
        convertMaze();
    }

    private void readMaze() {
        try {
            InputStream is = context.getAssets().open("mazeGen1.txt");
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

    private void convertMaze(){
        int y = 0;
        int x = 0;
        for (int i = 0; i < maze.length; i++){
            mazeArray[x][y] = maze[i];
            x++;

            if (maze[i] == '\n')
            {
                y++;
                x = 0;
            }
        }
    }

    public void ResetPlayerPoint(){
        user.x = 0;
        user.y = 0;
    }

    public void PlayerInput(float x, float y) {
        if (System.currentTimeMillis() < end) return;
        start = System.currentTimeMillis();
        end = start + speed;

        if (y > 1f && user.y <= ROWS-2 && mazeArray[(int) user.x][(int) user.y+1] == ' '){
            user.y = user.y + 1;
            invalidate();
        } else if (y < -1f && user.y > 1 && mazeArray[(int) user.x][(int) user.y-1] == ' ') {
            user.y = user.y - 1;
            invalidate();
        }

        if (x < 1f && user.x <= COLS-2 && mazeArray[(int) user.x+1][(int) user.y] == ' '){
            user.x = user.x + 1;
            invalidate();
        } else if (x > -1f && user.x > 1 && mazeArray[(int) user.x-1][(int) user.y] == ' ') {
            user.x = user.x - 1;
            invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

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
                }
            }
        }

        // draw player
        canvas.drawOval(user.x*cellSize,user.y*cellSize,(user.x+1)*cellSize,(user.y+1)*cellSize, playerPaint);
    }
}
