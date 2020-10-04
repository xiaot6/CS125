package edu.illinois.cs.cs125.spring2019.mp1;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import edu.illinois.cs.cs125.spring2019.mp1.lib.RGBAPixel;
import edu.illinois.cs.cs125.spring2019.mp1.lib.Transform;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Background tasks for use by our image transformation app.
 */
class Tasks {
    /** Default logging tag for messages from app tasks. */
    private static final String TAG = "MP1";

    /** Default quality level for bitmap compression. */
    private static final int DEFAULT_COMPRESSION_QUALITY_LEVEL = 100;

    /**
     * Save a bitmap to external storage for later use.
     */
    static class DownloadFileTask extends AsyncTask<String, Integer, Integer> {

        /** Reference to the calling activity so that we can return results. */
        private WeakReference<MainActivity> activityReference;

        /** Request queue to use for our API call. */
        private RequestQueue requestQueue;

        /**
         * Create a new task to download and save a file.
         *
         * We pass in a reference to the app so that this task can be static.
         * Otherwise we get warnings about leaking the context.
         *
         * @param context calling activity context
         * @param setRequestQueue Volley request queue to use for the API request
         */
        DownloadFileTask(final MainActivity context, final RequestQueue setRequestQueue) {
            activityReference = new WeakReference<>(context);
            requestQueue = setRequestQueue;
        }

        /**
         * Before we start draw the waiting indicator.
         */
        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Download a file, save it to the storage device if possible, and update the image.
         *
         * @param downloadURL the URL to download
         * @return return value is ignored but required to extend AsyncTask
         */
        @Override
        protected Integer doInBackground(final String... downloadURL) {
            final MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return 0;
            }
            final ImageView photoView = activity.findViewById(R.id.photoView);
            int targetWidth = photoView.getWidth();
            int targetHeight = photoView.getHeight();

            ImageRequest imageRequest = new ImageRequest(downloadURL[0],
                response -> {
                    /*
                     * If the download succeeded, try to draw the image on the screen.
                     */
                    activity.setForegroundBitmap(response);
                    try {
                        /*
                         * And also try to save the image.
                         */
                        File outputFile = activity.getSaveFilename();
                        if (outputFile == null) {
                            throw new Exception("null output file");
                        }
                        OutputStream outputStream = new FileOutputStream(outputFile);
                        response.compress(Bitmap.CompressFormat.JPEG,
                                DEFAULT_COMPRESSION_QUALITY_LEVEL, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        activity.addPhotoToGallery(Uri.fromFile(outputFile));
                    } catch (Exception e) {
                        Log.w(TAG, "Problem saving image: " + e);
                    }

                    // Clear the progress bar
                    ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                }, targetWidth, targetHeight,
                ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,
                e -> {
                    // If the download failed alert the user and clear the progress bar
                    Toast.makeText(activity.getApplicationContext(),
                            "Image download failed",
                            Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Image download failed: " + e);
                    ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            requestQueue.add(imageRequest);
            return 0;
        }
    }

    /**
     * Perform image transformation in the background.
     */
    static class ProcessImageTask extends AsyncTask<Bitmap, Bitmap, Bitmap> {

        /** Reference to the calling activity so that we can return results. */
        private WeakReference<MainActivity> activityReference;

        /** Action to perform. */
        private String action;

        /**
         * Create a new task to process an image.
         *
         * We pass in a reference to the app so that this task can be static.
         * Otherwise we get warnings about leaking the context.
         *
         * @param context calling activity context
         * @param setAction String identifying the action to perform
         */
        ProcessImageTask(final MainActivity context, final String setAction) {
            activityReference = new WeakReference<>(context);
            action = setAction;
        }

        /**
         * Before we start draw the waiting indicator.
         */
        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        /**
         * Convert a 1D Bitmap to a 2D RGBAPixel array.
         *
         * @param toConvert the bitmap to convert
         * @return the bitmap representation of the given RGBAPixel array
         */
        private RGBAPixel[][] bitmapToRGBA(final Bitmap toConvert) {
            int width = toConvert.getWidth();
            int height = toConvert.getHeight();

            int[] array1D = new int[width * height];
            toConvert.getPixels(array1D, 0, width, 0, 0, width, height);
            int[][] array2d = new int[width][height];
            Log.d(TAG, "bitmapToRGBA " + width + " x " + height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    array2d[x][y] = array1D[y * width + x];
                }
            }
            return RGBAPixel.fromIntArray(array2d);
        }

        /**
         * Convert 2D RGBAPixel array to a 1D Bitmap.
         *
         * @param toConvert the bitmap to convert
         * @return the bitmap representation of the given RGBAPixel array
         */
        private Bitmap rgbaToBitmap(final RGBAPixel[][] toConvert) {
            int[][] array2d = RGBAPixel.toIntArray(toConvert);
            int width = toConvert.length;
            int height = toConvert[0].length;
            int[] array1D = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    array1D[width * y + x] = array2d[x][y];
                }
            }
            return Bitmap.createBitmap(array1D, width, height, ARGB_8888);
        }

        /**
         * Convert an image to a byte array, upload to the Microsoft Cognitive Services API,
         * and return a result.
         *
         * @param currentBitmap the bitmap to process
         * @return unused unused result
         */
        protected Bitmap doInBackground(final Bitmap... currentBitmap) {
            RGBAPixel[][] pixels = bitmapToRGBA(currentBitmap[0]);

            switch (action) {
                case "shiftLeft":
                    pixels = Transform.shiftLeft(pixels, MainActivity.DEFAULT_SHIFT_VALUE);
                    break;
                case "shiftRight":
                    pixels = Transform.shiftRight(pixels, MainActivity.DEFAULT_SHIFT_VALUE);
                    break;
                case "shiftUp":
                    pixels = Transform.shiftUp(pixels, MainActivity.DEFAULT_SHIFT_VALUE);
                    break;
                case "shiftDown":
                    pixels = Transform.shiftDown(pixels, MainActivity.DEFAULT_SHIFT_VALUE);
                    break;
                case "rotateLeft":
                    pixels = Transform.rotateLeft(pixels);
                    break;
                case "rotateRight":
                    pixels = Transform.rotateRight(pixels);
                    break;
                case "expand":
                    pixels = Transform.expandVertical(pixels, MainActivity.DEFAULT_SCALE_FACTOR);
                    pixels = Transform.expandHorizontal(pixels, MainActivity.DEFAULT_SCALE_FACTOR);
                    break;
                case "shrink":
                    pixels = Transform.shrinkVertical(pixels, MainActivity.DEFAULT_SCALE_FACTOR);
                    pixels = Transform.shrinkHorizontal(pixels, MainActivity.DEFAULT_SCALE_FACTOR);
                    break;
                case "greenScreen":
                    pixels = Transform.greenScreen(pixels);
                    break;
                case "flipHorizontal":
                    pixels = Transform.flipHorizontal(pixels);
                    break;
                case "flipVertical":
                    pixels = Transform.flipVertical(pixels);
                    break;
                default:
                    Log.e(TAG, "No case registered for " + action);
                    break;
            }

            return rgbaToBitmap(pixels);
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.setForegroundBitmap(bitmap);
            activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        }
    }
}
