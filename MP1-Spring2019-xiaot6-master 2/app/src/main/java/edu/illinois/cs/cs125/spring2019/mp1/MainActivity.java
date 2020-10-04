package edu.illinois.cs.cs125.spring2019.mp1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Main screen for our image recognition app.
 */
public final class MainActivity extends AppCompatActivity {

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP1";

    /** Constant to perform a read file request. */
    private static final int READ_REQUEST_CODE = 42;

    /** Constant to request an image capture. */
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

    /** Constant to request permission to write to the external storage device. */
    private static final int REQUEST_WRITE_STORAGE = 112;

    /** Default shift value used by the app. */
    protected static final int DEFAULT_SHIFT_VALUE = 50;

    /** Default scale factor used by the app. */
    protected static final int DEFAULT_SCALE_FACTOR = 2;

    /** Maximim pixels to pass to the transformation functions. */
    private static final int MAX_PIXELS = 1024 * 768;

    /** Default JPEG save quality. */
    private static final int DEFAULT_JPEG_QUALITY = 85;

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** Whether we can write to public storage. */
    private boolean canWriteToPublicStorage = false;

    /** Mapping between background image IDs and bitmaps. */
    private SparseArray<Bitmap> backgroundImages = new SparseArray<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
         * Update the image when the ImageView size changes. Required because orientation listeners fire before the
         * layout has changed.
         */
        final ImageView imageView = findViewById(R.id.photoView);
        imageView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (foregroundBitmap != null) {
                setForegroundBitmap(foregroundBitmap);
            }
        });

        /*
         * Button handlers for loading files into the viewer.
         */
        final ImageButton openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(v -> {
            Log.d(TAG, "Open file button clicked");
            startOpenFile();
        });
        final ImageButton takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(v -> {
            Log.d(TAG, "Take photo button clicked");
            startTakePhoto();
        });
        final ImageButton downloadFile = findViewById(R.id.downloadFile);
        downloadFile.setOnClickListener(v -> {
            Log.d(TAG, "Download file button click");
            startDownloadFile();
        });

        /*
         * Button handlers for changing the background image.
         */
        final ImageButton setBonIverBackground = findViewById(R.id.background_boniver);
        setBonIverBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set Bon Iver background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.boniver_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setCornfieldBackground = findViewById(R.id.background_cornfield);
        setCornfieldBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set cornfield background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.cornfield_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setDatacenterBackground = findViewById(R.id.background_datacenter);
        setDatacenterBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set datacenter background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.datacenter_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setFacebookBackground = findViewById(R.id.background_facebook);
        setFacebookBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set Facebook background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.facebook_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setMemorialStadiumBackground = findViewById(R.id.background_memorialstadium);
        setMemorialStadiumBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set Memorial Stadium background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.memorialstadium_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setSiebelBackground = findViewById(R.id.background_siebel);
        setSiebelBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set Siebel background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.siebel_background);
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton setTajMahalBackground = findViewById(R.id.background_tajmahal);
        setTajMahalBackground.setOnClickListener(v -> {
            Log.d(TAG, "Set Taj Mahal background button click");
            backgroundBitmap = backgroundImages.get(R.drawable.tajmahal_background);
            setForegroundBitmap(foregroundBitmap);
        });

        /*
         * Button handlers for image transformations.
         */
        final ImageButton shiftLeft = findViewById(R.id.shiftLeft);
        shiftLeft.setOnClickListener(v -> {
            Log.d(TAG, "Shift left button clicked");
            startProcessImage("shiftLeft");
        });
        final ImageButton shiftRight = findViewById(R.id.shiftRight);
        shiftRight.setOnClickListener(v -> {
            Log.d(TAG, "Shift right button clicked");
            startProcessImage("shiftRight");
        });
        final ImageButton shiftUp = findViewById(R.id.shiftUp);
        shiftUp.setOnClickListener(v -> {
            Log.d(TAG, "Shift up button clicked");
            startProcessImage("shiftUp");
        });
        final ImageButton shiftDown = findViewById(R.id.shiftDown);
        shiftDown.setOnClickListener(v -> {
            Log.d(TAG, "Shift down button clicked");
            startProcessImage("shiftDown");
        });
        final ImageButton rotateLeft = findViewById(R.id.rotateLeft);
        rotateLeft.setOnClickListener(v -> {
            Log.d(TAG, "Rotate left button clicked");
            startProcessImage("rotateLeft");
        });
        final ImageButton rotateRight = findViewById(R.id.rotateRight);
        rotateRight.setOnClickListener(v -> {
            Log.d(TAG, "Rotate right button clicked");
            startProcessImage("rotateRight");
        });
        final ImageButton expand = findViewById(R.id.expand);
        expand.setOnClickListener(v -> {
            Log.d(TAG, "Expand button clicked");
            startProcessImage("expand");
        });
        final ImageButton shrink = findViewById(R.id.shrink);
        shrink.setOnClickListener(v -> {
            Log.d(TAG, "Shrink button clicked");
            startProcessImage("shrink");
        });
        final ImageButton greenScreen = findViewById(R.id.greenScreen);
        greenScreen.setOnClickListener(v -> {
            Log.d(TAG, "Green screen button clicked");
            startProcessImage("greenScreen");
        });
        final ImageButton flipHorizontal = findViewById(R.id.flipHorizontal);
        flipHorizontal.setOnClickListener(v -> {
            Log.d(TAG, "flip Horizontal button clicked");
            startProcessImage("flipHorizontal");
        });
        final ImageButton flipVertical = findViewById(R.id.flipVertical);
        flipVertical.setOnClickListener(v -> {
            Log.d(TAG, "flip Vertical button clicked");
            startProcessImage("flipVertical");
        });
        final ImageButton clearForeground = findViewById(R.id.clearForeground);
        clearForeground.setOnClickListener(v -> {
            Log.d(TAG, "Clear foreground button clicked");
            setForegroundBitmap(null);
        });
        final ImageButton clearBackground = findViewById(R.id.clearBackground);
        clearBackground.setOnClickListener(v -> {
            backgroundBitmap = null;
            Log.d(TAG, "Clear background button clicked");
            setForegroundBitmap(foregroundBitmap);
        });
        final ImageButton save = findViewById(R.id.save);
        save.setOnClickListener(v -> saveCurrentBitmap());


        /*
         * Start off with buttons disabled and enable them once the user loads an image.
         */
        enableOrDisableButtons(false);

        /*
         * Configure the progress bar.
         */
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark),
                        PorterDuff.Mode.SRC_IN);

        /*
         * Determine if we've been granted storage permissions. If not, ask again.
         */
        canWriteToPublicStorage = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "Do we have permission to write to external storage: "
                + canWriteToPublicStorage);
        if (!canWriteToPublicStorage) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        /*
         * Load background images into a map keyed by their drawable ID.
         */
        for (int backgroundID : new int[] {R.drawable.boniver_background,
            R.drawable.cornfield_background,
            R.drawable.datacenter_background,
            R.drawable.facebook_background,
            R.drawable.memorialstadium_background,
            R.drawable.tajmahal_background,
            R.drawable.siebel_background}) {
            backgroundImages.put(backgroundID, BitmapFactory.decodeResource(getResources(), backgroundID));
        }

        // Set up queue for our Volley network requests.
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Initiate the image transformation process.
     *
     * @param action String identifying the action to perform
     */
    private void startProcessImage(final String action) {
        if (foregroundBitmap == null) {
            Toast.makeText(getApplicationContext(), "No image selected",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "No image selected");
            return;
        }

        /*
         * Launch our background task which actually makes the request. It will call
         * setForegroundBitmap when it completes.
         */
        enableOrDisableButtons(false);
        Bitmap toTransform = getForegroundBitmap();
        new Tasks.ProcessImageTask(MainActivity.this, action).execute(toTransform);
    }

    /** Current file that we are using for our image request. */
    private boolean photoRequestActive = false;

    /** Whether a current photo request is being processed. */
    private File currentPhotoFile = null;

    /** Take a photo using the camera. */
    private void startTakePhoto() {
        if (photoRequestActive) {
            Log.w(TAG, "Overlapping photo requests");
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentPhotoFile = getSaveFilename();
        if (takePictureIntent.resolveActivity(getPackageManager()) == null
                || currentPhotoFile == null) {
            Toast.makeText(getApplicationContext(), "Problem taking photo",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Problem taking photo");
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(this, "edu.illinois.cs.cs125.spring2019.mp1", currentPhotoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        photoRequestActive = true;
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }

    /** URL storing the file to download. */
    private String downloadFileURL;

    /** Initiate the file download process. */
    private void startDownloadFile() {

        // Build a dialog that we will use to ask for the URL to the photo

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Download File");
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, unused) -> {

            // If the user clicks OK, try and download the file
            downloadFileURL = input.getText().toString().trim();
            Log.d(TAG, "Got download URL " + downloadFileURL);
            new Tasks.DownloadFileTask(MainActivity.this, requestQueue)
                    .execute(downloadFileURL);
        });
        builder.setNegativeButton("Cancel", (dialog, unused) -> dialog.cancel());

        // Display the dialog
        builder.show();
    }

    /**
     * Start an open file dialog to look for image files.
     */
    private void startOpenFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /** Save the current composite bitmap to disk. */
    private void saveCurrentBitmap() {
        if (compositeBitmap == null) {
            return;
        }

        File saveFilename = getSaveFilename();
        try {
            assert saveFilename != null;
            FileOutputStream fileOutputStream = new FileOutputStream(saveFilename);
            compositeBitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_JPEG_QUALITY, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Problem saving photo",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Problem saving photo");
            return;
        }

        Toast.makeText(getApplicationContext(), "Photo saved as " + saveFilename,
                Toast.LENGTH_LONG).show();
        addPhotoToGallery(Uri.fromFile(saveFilename));
    }

    /*
     * Event handler called when file open or photo activities complete.
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent resultData) {
        if (resultCode != Activity.RESULT_OK) {
            Log.w(TAG, "onActivityResult with code " + requestCode + " failed");
            if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
                photoRequestActive = false;
            }
            return;
        }
        Uri currentPhotoURI;
        if (requestCode == READ_REQUEST_CODE) {
            currentPhotoURI = resultData.getData();
        } else if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            currentPhotoURI = Uri.fromFile(currentPhotoFile);
            photoRequestActive = false;
        } else {
            Log.w(TAG, "Unhandled activityResult with code " + requestCode);
            return;
        }
        Log.d(TAG, "Photo selection produced URI " + currentPhotoURI);
        loadPhoto(currentPhotoURI);
    }

    /** Current foreground image. */
    private Bitmap foregroundBitmap = null;

    /** Current background image. */
    private Bitmap backgroundBitmap = null;

    /** Current composite image. */
    private Bitmap compositeBitmap = null;

    /**
     * Load a photo and prepare for viewing.
     *
     * @param currentPhotoURI URI of the image to process
     */
    private void loadPhoto(final Uri currentPhotoURI) {
        enableOrDisableButtons(false);
        final ImageButton rotateLeft = findViewById(R.id.rotateLeft);
        rotateLeft.setClickable(false);
        rotateLeft.setEnabled(false);

        if (currentPhotoURI == null) {
            Toast.makeText(getApplicationContext(), "No image selected",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "No image selected");
            return;
        }
        String uriScheme = currentPhotoURI.getScheme();

        byte[] imageData;
        try {
            assert uriScheme != null;
            switch (uriScheme) {
                case "file":
                    imageData =
                            FileUtils.readFileToByteArray(new File(
                                    Objects.requireNonNull(currentPhotoURI.getPath())));
                    break;
                case "content":
                    InputStream inputStream = getContentResolver().openInputStream(currentPhotoURI);
                    assert inputStream != null;
                    imageData = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Unknown scheme " + uriScheme,
                            Toast.LENGTH_LONG).show();
                    return;
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error processing file",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Error processing file: " + e);
            return;
        }

        final ImageView photoView = findViewById(R.id.photoView);
        double targetWidth = photoView.getWidth();
        double targetHeight = photoView.getHeight();

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, decodeOptions);

        double actualWidth = decodeOptions.outWidth;
        double actualHeight = decodeOptions.outHeight;
        double exactScaleFactor = Math.min(actualWidth / targetWidth, actualHeight / targetHeight);
        int scaleFactor = (int) Math.round(Math.pow(2, Math.ceil(Math.log(exactScaleFactor) / Math.log(2))));
        BitmapFactory.Options modifyOptions = new BitmapFactory.Options();
        modifyOptions.inJustDecodeBounds = false;
        modifyOptions.inSampleSize = scaleFactor;

        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, modifyOptions);

        setForegroundBitmap(decodedBitmap);
    }

    /**
     * Update the currently displayed image.
     *
     * @param setForegroundBitmap the new bitmap to display
     */
    void setForegroundBitmap(final Bitmap setForegroundBitmap) {
        ImageView photoView = findViewById(R.id.photoView);
        int canvasWidth = photoView.getWidth();
        int canvasHeight = photoView.getHeight();
        Log.d(TAG, "Canvas size: " + canvasWidth + " x " + canvasHeight);

        /*
         * Create a correctly-sized canvas and then layer both background foreground onto it.
         */
        Bitmap.Config bitmapConfiguration = Bitmap.Config.ARGB_8888;
        Bitmap newBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, bitmapConfiguration);
        Canvas canvas = new Canvas(newBitmap);

        /*
         * If we're using a background image add that first.
         */
        if (backgroundBitmap != null) {
            double backgroundWidth = backgroundBitmap.getWidth();
            double backgroundHeight = backgroundBitmap.getHeight();
            double scaleFactor = Math.max((double) canvasWidth / backgroundWidth,
                    (double) canvasHeight / backgroundHeight);
            Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
                    (int) Math.round(backgroundWidth * scaleFactor),
                    (int) Math.round(backgroundHeight * scaleFactor), false);
            int backgroundCenterWidth = (canvasWidth - scaledBackgroundBitmap.getWidth()) / 2;
            int backgroundCenterHeight = (canvasHeight - scaledBackgroundBitmap.getHeight()) / 2;
            canvas.drawBitmap(scaledBackgroundBitmap, backgroundCenterWidth, backgroundCenterHeight, null);
        }

        /*
         * Next add the foreground image. This ensures that alpha in the foreground will cause the background to
         * be visible.
         */
        if (setForegroundBitmap != null) {
            double foregroundWidth = setForegroundBitmap.getWidth();
            double foregroundHeight = setForegroundBitmap.getHeight();
            Log.d(TAG, "Foreground image: " + foregroundWidth + " x " + foregroundHeight);
            double scaleFactor = Math.min((double) canvasWidth / foregroundWidth,
                    (double) canvasHeight / foregroundHeight);
            Log.d(TAG, "scaleFactor " + scaleFactor);
            Bitmap scaledForegroundBitmap = Bitmap.createScaledBitmap(setForegroundBitmap,
                    (int) Math.round(foregroundWidth * scaleFactor),
                    (int) Math.round(foregroundHeight * scaleFactor), false);
            int foregroundCenterWidth = (canvasWidth - scaledForegroundBitmap.getWidth()) / 2;
            int foregroundCenterHeight = (canvasHeight - scaledForegroundBitmap.getHeight()) / 2;
            canvas.drawBitmap(scaledForegroundBitmap, foregroundCenterWidth, foregroundCenterHeight, null);

            Bitmap newForegroundBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, bitmapConfiguration);
            Canvas foregroundCanvas = new Canvas(newForegroundBitmap);
            foregroundCanvas.drawBitmap(scaledForegroundBitmap, foregroundCenterWidth, foregroundCenterHeight, null);
            foregroundBitmap = newForegroundBitmap;
        } else {
            foregroundBitmap = null;
        }

        compositeBitmap = newBitmap;
        photoView.setImageBitmap(newBitmap);
        enableOrDisableButtons(setForegroundBitmap != null);
    }

    /**
     * Get a scaled copy of the current bitmap.
     *
     * Our image tranformation functions are fairly slow, and particularly slow if we provide a full-density image on
     * high-resolution devices. So we reduce the canvas size before calling them.
     *
     * @return a scaled copy of the current bitmap.
     */
    private Bitmap getForegroundBitmap() {
        int currentWidth = foregroundBitmap.getWidth();
        int currentHeight = foregroundBitmap.getHeight();
        if (currentWidth * currentHeight < MAX_PIXELS) {
            return foregroundBitmap;
        }

        double scaleFactor = (double) MAX_PIXELS / ((double) (currentWidth * currentHeight));
        int newWidth = (int) Math.round(currentWidth * scaleFactor);
        int newHeight = (int) Math.round(currentHeight * scaleFactor);
        if (newWidth % 2 == 1) {
            newWidth--;
        }
        if (newHeight % 2 == 1) {
            newHeight--;
        }

        return Bitmap.createScaledBitmap(foregroundBitmap, newWidth, newHeight, false);
    }

    /**
     * Helper function to swap button states.
     *
     * We disable the buttons when we don't have a valid image to process.
     *
     * @param enableOrDisable whether to enable or disable the buttons
     */
    private void enableOrDisableButtons(final boolean enableOrDisable) {
        Log.d(TAG, "enableOrDisable " + enableOrDisable);
        for (int viewID : new int[] {R.id.shiftDown, R.id.shiftDown, R.id.rotateLeft, R.id.rotateRight,
            R.id.shiftLeft, R.id.shiftRight, R.id.expand, R.id.greenScreen, R.id.shrink,
            R.id.flipVertical, R.id.flipHorizontal}) {
            final ImageButton button = findViewById(viewID);
            button.setClickable(enableOrDisable);
            button.setEnabled(enableOrDisable);
        }
    }

    /**
     * Add a photo to the gallery so that we can use it later.
     *
     * @param toAdd URI of the file to add
     */
    void addPhotoToGallery(final Uri toAdd) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(toAdd);
        this.sendBroadcast(mediaScanIntent);
        Log.d(TAG, "Added photo to gallery: " + toAdd);
    }

    /**
     * Get a new file location for saving.
     *
     * @return the path to the new file or null of the create failed
     */
    File getSaveFilename() {
        String imageFileName = "MP1_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File storageDir;
        if (canWriteToPublicStorage) {
            storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.w(TAG, "Problem saving file: " + e);
            return null;
        }
    }
}
