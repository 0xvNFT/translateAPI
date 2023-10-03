package com.dichthuatjun88binh.jun88.orc_reader_main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.dichthuatjun88binh.jun88.orc_reader_main.GraphicOverlay.Graphic;
import com.google.mlkit.vision.text.Text;

public class TextGraphicBlock extends Graphic {
    private static final String TAG = "TextGraphic";
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int TEXT_FILL = Color.argb(150, 0, 0, 0);
    private static final float TEXT_SIZE = 48f;
    private static final float STROKE_WIDTH = 4.0f;
    private static final float PADDING_FILL = 10f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final Text.Line textLine;
    private final String translatedText;
    private final float paddingDrawTextWidth;
    private final float paddingDrawTextHeight;

    public TextGraphicBlock(GraphicOverlay overlay, Text.Line textLine, String translatedText, float paddingDrawTextWidth, float paddingDrawTextHeight) {
        super(overlay);
        this.textLine = textLine;
        this.translatedText = translatedText;
        this.paddingDrawTextWidth = paddingDrawTextWidth;
        this.paddingDrawTextHeight = paddingDrawTextHeight;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_FILL);
        rectPaint.setStyle(Paint.Style.FILL);
//        rectPaint.setStyle(Paint.Style.STROKE);
//        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "on draw text graphic");
        if (textLine == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(textLine.getBoundingBox());
        Rect rectBG = new Rect((int) (rect.left - PADDING_FILL + paddingDrawTextWidth), (int) (rect.top - PADDING_FILL + paddingDrawTextHeight), (int) (rect.right + PADDING_FILL + paddingDrawTextWidth), (int) (rect.bottom + PADDING_FILL + +paddingDrawTextHeight));
        canvas.drawRect(rectBG, rectPaint);

//        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        textPaint.setTextSize(TEXT_SIZE);
        Rect bounds = new Rect();
        textPaint.getTextBounds(translatedText, 0, translatedText.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSizeWidth = TEXT_SIZE * (rect.right - rect.left) / bounds.width();
        float desiredTextSizeHeight = TEXT_SIZE * (rect.bottom - rect.top) / bounds.height();

        // Set the paint for that size.
        textPaint.setTextSize(Math.min(desiredTextSizeWidth, desiredTextSizeHeight));
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText(translatedText, rect.left + paddingDrawTextWidth, rect.bottom + paddingDrawTextHeight, textPaint);
    }


}
