package com.example.adrien.projettechs5;

import android.graphics.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import static android.graphics.Bitmap.*;
import static android.graphics.Color.*;

public class ImageEffects extends AppCompatActivity {

    private static int NBIMAGES = 4;
    private static int imagesIDs[] = new int[NBIMAGES];
    private static int mainImageID;

    public static int minIndex(int tab[]) {
        int min = 0;

        while (tab[min] == 0)
            min++;

        return min;
    }
    public static int maxIndex(int tab[]) {
        int max = tab.length - 1;

        while (tab[max] == 0)
            max--;

        return max;
    }

    public static int[] hist(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int hist[] = new int[256];

        for (int x = 0 ; x < width ; x++) {
            for (int y = 0 ; y < height ; y++) {
                hist[red(bmp.getPixel(x, y))]++;
            }
        }

        return hist;
    }

    /*
     * @param range a 2-sized int tab containing the range containing the new histogram
     */
    public static Bitmap dynamicColorContraction(Bitmap src, int range[]) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap newBitmap = createBitmap(src);

        int hist[] = hist(src);
        int min = minIndex(hist);
        int max = maxIndex(hist);

        int LUT[] = new int[256];
        for (int i = 0 ; i < 256 ; i++)
            LUT[i] = (range[1] - range[0]) * (i - min) / (max - min);

        int pixels[] = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        double r, g, b;
        for (int i = 0 ; i < pixels.length ; i++) {
            r = LUT[red(pixels[i])];
            g = LUT[green(pixels[i])];
            b = LUT[blue(pixels[i])];
            pixels[i] = argb(alpha(pixels[i]), (int) r, (int) g, (int) b);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    public static Bitmap dynamicColorExtension(Bitmap bmp) {
        int range[] = {0, 255};
        return dynamicColorContraction(bmp, range);
    }

    /*
     * @param range a 2-sized int tab containing the range containing the new histogram
     */
    public static Bitmap dynamicContraction(Bitmap src, int range[]) {
        int width = src.getWidth();
        int height = src.getHeight();

        int hist[] = hist(src);
        int min = minIndex(hist);
        int max = maxIndex(hist);

        int LUT[] = new int[256];
        for (int i = 0 ; i < 256 ; i++)
            LUT[i] = (range[1] - range[0]) * (i - min) / (max - min);

        Bitmap newBitmap = createBitmap(src);

        int pixels[] = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        double gray;
        for (int i = 0 ; i < pixels.length ; i++) {
            gray = LUT[red(pixels[i])];
            pixels[i] = argb(alpha(pixels[i]), (int) gray, (int) gray, (int) gray);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    public static Bitmap dynamicExtension(Bitmap bmp) {
        int range[] = {0, 255};
        return dynamicContraction(bmp, range);
    }

    public static int[] cumulatedHist(int hist[]) {
        int length = hist.length;
        int cumulatedHist[] = new int[length];

        for (int i = 0 ; i < length ; i++) {
            cumulatedHist[i] = 0;
            for (int j = 1 ; j <= i ; j++)
                cumulatedHist[i] += hist[j];
        }
        return cumulatedHist;
    }

    public static Bitmap histEqualization(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int nbPixels = width * height;

        int cHist[] = cumulatedHist(hist(src));

        Bitmap newBitmap = createBitmap(src);

        int pixels[] = new int[nbPixels];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        double r, g, b;
        for (int i = 0 ; i < pixels.length ; i++) {
            r = cHist[red(pixels[i])] * 255 / nbPixels;
            g = cHist[green(pixels[i])] * 255 / nbPixels;
            b = cHist[blue(pixels[i])] * 255 / nbPixels;
            pixels[i] = argb(alpha(pixels[i]), (int) r, (int) g, (int) b);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    /*
     * garde qu'une seule couleur (genre le rouge)
     * @param range a 2-sized int tab containing the HSV range containing the color to keep
     */
    public static Bitmap keepRangeColor(Bitmap src, int range[]) {
        int width = src.getWidth();
        int height = src.getHeight();

        int pixels[] = new int[width * height];
        float hsv[] = new float[3];

        Bitmap newBitmap = createBitmap(src);

        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0 ; i < pixels.length ; i++) {
            colorToHSV(pixels[i], hsv);
            if (hsv[0] < range[0] || hsv[0] > range[1])
                hsv[1] = 0;
            pixels[i] = HSVToColor(hsv);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    public static Bitmap keepOnlyBlue(Bitmap bmp) {
        int range[] = {160, 260};
        return keepRangeColor(bmp, range);
    }

    // ajoute une teinte random
    public static Bitmap colorize(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        int pixels[] = new int[width * height];
        float hsv[] = new float[3];

        Bitmap newBitmap = createBitmap(src);

        double randColor = Math.random() * 360;

        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0 ; i < pixels.length ; i++) {
            colorToHSV(pixels[i], hsv);
            hsv[0] = (float) randColor;
            pixels[i] = HSVToColor(hsv);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    public static void drawHorizontalLine(Bitmap bmp, int y, int color) {
        for (int x = 0 ; x < bmp.getWidth() ; x++)
            bmp.setPixel(x, y, color);
    }

    public static Bitmap toGray(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int r, g, b;
        int pixels[] = new int[width * height];

        Bitmap newBitmap = createBitmap(src);

        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0 ; i < pixels.length ; i++) {
            r = red(pixels[i]);
            g = green(pixels[i]);
            b = blue(pixels[i]);
            r = g = b = (int) (0.3 * r + 0.59 * g + 0.11 * b);
            pixels[i] = rgb(r, g, b);
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBitmap;
    }

    public int applyMask(Bitmap bmp, int x, int y, Bitmap mask, int maskSize, int pace) {
        int srcPixel, maskPixel;
        int newPixel = 0;

        for (int x2 = 0 ; x2 < maskSize ; x2++) {
            for (int y2 = 0 ; y2 < maskSize ; y2++) {
                maskPixel = red(mask.getPixel(x2, y2));
                srcPixel = red(bmp.getPixel(x - pace + x2, y - pace + y2));
                newPixel += maskPixel * srcPixel;
            }
        }

        newPixel = rgb(newPixel, newPixel, newPixel);
        return newPixel;
    }

    private int calibrate(double color) {
        if (color < 0) return 0;
        if (color > 255) return 255;
        return (int) color;
    }

    // retourne le nouveau pixel auquel le masque a été appliqué
    private int applyMask(int pixels[], int pos, int height, double mask[], int maskSize, int pace) {
        double r, g, b;
        r = g = b = 0;
        int imgPixel;
        double maskPixel;
        for (int i = 0 ; i < maskSize ; i++) {
            for (int j = 0 ; j < maskSize ; j++) {
                imgPixel = pixels[pos - (pace - i) * height - (pace - j)];
                maskPixel = mask[i * maskSize + j];
                r += (red(imgPixel) * maskPixel);
                g += (green(imgPixel) * maskPixel);
                b += (blue(imgPixel) * maskPixel);
            }
        }

        return argb(alpha(pixels[pos]), calibrate(r), calibrate(g), calibrate(b));
    }

    public Bitmap convolutionFromMask(Bitmap src, double mask[]){
        int width = src.getWidth();
        int height = src.getHeight();
        int oldPixels[] = new int[width * height];
        int newPixels[] = new int[width * height];

        double tempMaskSize = Math.sqrt(mask.length);
        int maskSize = (int) tempMaskSize;
        if (maskSize != tempMaskSize || maskSize % 2 == 0) {
            Log.i("debug1", "Unvalid mask for convolution.");
            return src;
        }
        int pace = maskSize / 2;

        Bitmap newBitmap = createBitmap(src);

        src.getPixels(oldPixels, 0, width, 0, 0, width, height);

        int pos;
        for (int x = 0 ; x < width ; x++){
            for (int y = 0 ; y < height ; y++) {
                pos = x * height + y;
                newPixels[pos] = (x < pace || y < pace || x > width - pace - 1 || y > height - pace - 1) ? BLACK : applyMask(oldPixels, pos, height, mask, maskSize, pace);
            }
        }

        newBitmap.setPixels(newPixels,0, width,0,0, width, height);

        return newBitmap;
    }

    private double[] multiplyMask(double mask[], double n) {
        for (int i = 0 ; i < mask.length ; i++) {
            mask[i] *= n;
        }
        return mask;
    }

    public Bitmap convolution(Bitmap bmp) {
        //double mask[] = {0, 0, 0, 0, 1, 0, 0, 0, 0};
        //double mask[] = {1, 0, -1, 0, 0, 0, -1, 0, -1};
        //double mask[] = {0, 1, 0, 1, -4, 1, 0, 1, 0};
        double mask[] = {-1, -1, -1, -1, 8, -1, -1, -1, -1};
        //double mask[] = {0, -1, 0, -1, 5, -1, 0, -1, 0};
        //double mask[] = {1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1};
        //double mask[] = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        //double mask[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        //double mask[] = {1, 4, 6, 4, 1, 4, 16, 24, 16, 4, 6, 24, 36, 24, 6, 4, 16, 24, 16, 4, 1, 4, 6, 4, 1};
        //double mask[] = {1, 4, 6, 4, 1, 4, 16, 24, 16, 4, 6, 24, -476, 24, 6, 4, 16, 24, 16, 4, 1, 4, 6, 4, 1};
        return convolutionFromMask(bmp, mask);
    }

    public static Bitmap resize(Bitmap bmp, int x, int y) {
        return createScaledBitmap(bmp, x, y, false);
    }

    public void applyFunctionToMainImage(String methodName) {
        ImageView iv = (ImageView) findViewById(R.id.mainImage);
        iv.buildDrawingCache();
        Bitmap bmp = iv.getDrawingCache();
        try {
            bmp = (Bitmap) this.getClass().getMethod(methodName, bmp.getClass()).invoke(this, (Object) bmp);
        } catch (Exception e) {
            Log.i("debug1", "Exception : \"" + e.toString() + "\"");
            Log.i("debug1", "Method \"" + methodName + "\" not existing.");
        }
        iv.setImageBitmap(bmp);
    }

    public void onConvolutionButtonClick(View c) {
        applyFunctionToMainImage("convolution");
    }

    public void onGrayButtonClick(View v) {
        applyFunctionToMainImage("toGray");
    }

    public void onBlueButtonClick(View v) {
        applyFunctionToMainImage("keepOnlyBlue");
    }

    public void onDynamicExtensionButtonClick(View v) {
        applyFunctionToMainImage("dynamicColorExtension");
    }

    public void onHistEqualizationButtonClick(View v) {
        applyFunctionToMainImage("histEqualization");
    }

    public void onColorizeButtonClick(View v) {
        applyFunctionToMainImage("colorize");
    }

    public void onReinitializeButtonClick(View v) {
        ImageView iv = findViewById(R.id.mainImage);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), mainImageID);
        bmp = resize(bmp, 250, 250);
        iv.setImageBitmap(bmp);

        TextView tv = findViewById(R.id.imageSizeText);
        tv.setText("image size : (" + bmp.getWidth() + ", " + bmp.getHeight() + ")");
    }

    public void onSwitchButtonClick(View v) {
        int length = imagesIDs.length;
        switch (length) {
            case 0:
                break;
            case 1:
                mainImageID = imagesIDs[0];
                break;
            default :
                int newImageID = imagesIDs[(int) (Math.random() * length)];
                for (; newImageID == mainImageID ; newImageID = imagesIDs[(int) (Math.random() * length)])
                    Log.i("debug1", Integer.toString(newImageID));
                mainImageID = newImageID;
        }
        onReinitializeButtonClick(null);
    }

    private void init() {
        imagesIDs[0] = R.drawable.plage;
        imagesIDs[1] = R.drawable.rogerrafa;
        imagesIDs[2] = R.drawable.lilgirl;
        imagesIDs[3] = R.drawable.moche;
        mainImageID = imagesIDs[3];
        onReinitializeButtonClick(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("CV", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_effects);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("CV", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("CV", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("CV", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("CV", "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("CV", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("CV", "onDestroy");
    }

}
