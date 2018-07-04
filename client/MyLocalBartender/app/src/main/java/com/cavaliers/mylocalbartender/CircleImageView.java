package com.cavaliers.mylocalbartender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    private Bitmap mBitmap;

    public CircleImageView(Context context) {
        super(context,null,0);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mBitmap == null) {

            Drawable drawable = getDrawable();

            if(drawable != null && getWidth() > 0 && getHeight() > 0) {

                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                //image view height is always smaller then width
                createCircularBitmap(bitmap, getWidth());
            }
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);

    }

    public void createCircularBitmap(@NonNull final Bitmap bmp, final int width) {

        //empty Bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, width, Config.ARGB_8888);

        Canvas canvas = new Canvas(newBitmap);

        final Paint paint = new Paint();

        //rect to store the new circular Bitmap
        final Rect rect = new Rect(0, 0, width, width);

        //draw circle where image will be inside
        canvas.drawCircle( (width / 2f), (width / 2f),(width / 2f) , paint);

        //set the way how image src will be displayed
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bmp, null, rect, paint);

        this.mBitmap = newBitmap;
    }

    public void setImage(Bitmap image){

        this.mBitmap = image;
        createCircularBitmap(image, image.getWidth());
        invalidate();
    }

}
