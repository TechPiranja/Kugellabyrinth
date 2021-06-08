package com.example.kugellabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class GameView extends View{

    Context context;
    private Paint wallPaint, spacePaint, playerPaint, startPaint, finishPaint;
    private int COLS = 21, ROWS = 21;
    private int hMargin, vMargin;
    private int cellSize;
    private Player user = new Player();
    private Rect r;
    private String maze;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);

        try {
            InputStream is = context.getAssets().open("mazeGen1.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            maze = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = getWidth();
        int height = getHeight();

        if (width/height < COLS/ROWS)
            cellSize = width/(COLS+1);
        else
            cellSize = height/(ROWS+1);

        hMargin = (width-COLS*cellSize)/2;
        vMargin = (height-ROWS*cellSize)/2;
        canvas.translate(hMargin, vMargin);
        char[] chars = maze.toCharArray();
        int y = 0;
        int x = 0;
        for (int i = 0; i < chars.length; i++){
            if(chars[i] == '#') {
                canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, wallPaint);
                //Log.d("Hashtag", x + "," + y);
            } else if(chars[i] == ' ') {
                if (user.x == -1 && user.y == -1) {
                    user.x = x;
                    user.y = y;
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, playerPaint);
                } else {
                    canvas.drawRect(x*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize, spacePaint);
                }

                //Log.d("Hashtag", x + "," + y);
            }
            x++;

            if (chars[i] == '\n')
            {
                System.out.print("Newline");
                y++;
                x = 0;
            }
        }
    }
}
