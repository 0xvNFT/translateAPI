package com.dichthuatjun88binh.jun88.orc_reader_main;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.dichthuatjun88binh.jun88.orc_reader_main.GraphicOverlay.Graphic;

public class FillGraphic extends Graphic {

    private final Paint paint;

    public FillGraphic(GraphicOverlay overlay, int fillColor) {
        super(overlay);

        paint = new Paint();
        paint.setColor(fillColor);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPaint(paint);
    }
}
