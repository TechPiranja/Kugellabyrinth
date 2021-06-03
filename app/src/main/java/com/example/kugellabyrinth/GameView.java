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
    private Paint wallPaint;
    private int COLS = 20, ROWS = 20;
    private int hMargin, vMargin;
    private int cellSize;
    private Rect r;
    private String maze;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        r = new Rect(100, 500, 100, 100);
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
        for (int i = 0; i < chars.length; i++, x++){
            if(chars[i] == '#') {
                canvas.drawRect(x*cellSize,y*cellSize,10,10, wallPaint);
                Log.d("Hashtag", x + "," + y);
            }

            System.out.print(chars[i]);

            if (chars[i] == '\n')
            {
                System.out.print("Newline");
                y++;
                x = 0;
            }
        }
    }
}
