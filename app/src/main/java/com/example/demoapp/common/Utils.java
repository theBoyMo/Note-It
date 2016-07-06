package com.example.demoapp.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.demoapp.R;
import com.example.demoapp.event.ModelLoadedEvent;
import com.example.demoapp.model.DatabaseHelper;
import com.example.demoapp.thread.DeleteItemsThread;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    // hide the keyboard on executing search
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarSticky(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
    }

    // Check that a network connection is available
    public  static boolean isClientConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Long generateCustomId() {
        // define an id based on the date time stamp
        Locale locale = new Locale("en_US");
        Locale.setDefault(locale);
        String pattern = "yyyyMMddHHmmssSS"; // pattern used to sort objects
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());

        return Long.valueOf(formatter.format(new Date()));
    }


    // called from within a bkgd thread
    public static void queryAllItems(Context context) {
        try {
            Cursor results = DatabaseHelper.getInstance(context).loadItems(context);
            EventBus.getDefault().postSticky(new ModelLoadedEvent(results));
        } catch (Exception e) {
            Timber.e("%s: error loading items from dbase, %s", Constants.LOG_TAG, e.getMessage());
        }
    }

    public static ContentValues updateContentValues(long id, String title, String description) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, id);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        return cv;
    }

    public static ContentValues setContentValuesTextNote(long id, int type, String title, String description) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, id);
        cv.put(Constants.ITEM_TYPE, type);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        return cv;
    }

    public static ContentValues setContentValuesVideoNote(long id, int type, String title, String description, String filePath, String thumbnailPath, String mimeType) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, id);
        cv.put(Constants.ITEM_TYPE, type);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        cv.put(Constants.ITEM_FILE_PATH, filePath);
        cv.put(Constants.ITEM_THUMBNAIL_PATH, thumbnailPath);
        cv.put(Constants.ITEM_MIME_TYPE, mimeType);
        return cv;
    }

    public static ContentValues setContentValuesAudioNote(long id, int type, String title, String description, String filePath, String mimeType) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, id);
        cv.put(Constants.ITEM_TYPE, type);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        cv.put(Constants.ITEM_FILE_PATH, filePath);
        cv.put(Constants.ITEM_MIME_TYPE, mimeType);
        return cv;
    }

    public static void setupToolbar(Activity activity, Toolbar toolbar) {
        if (toolbar != null) {
            ((AppCompatActivity)activity).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
            if (actionBar != null) {
                // hide title by default
                actionBar.setDisplayShowTitleEnabled(false);
                // set navigation icon and color
                toolbar.setNavigationIcon(Utils.tintDrawable(ContextCompat
                        .getDrawable(activity, R.drawable.action_back), R.color.colorButtonIcon));
                // set title text color
                toolbar.setTitleTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryText));
                // add overflow icon via the menu, use this method to tint them
                toolbar.setOverflowIcon(Utils.tintDrawable(ContextCompat
                        .getDrawable(activity, R.drawable.action_delete_black), R.color.colorButtonIcon));
            }
        }
    }


    // Bitmap/image helper methods
    public static Bitmap generateBitmap(String path) {
        return ThumbnailUtils.createVideoThumbnail(new File(path).getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return null;
    }


    public static void loadThumbnail(Context context, String thumbnailPath, ImageView view) {
        Picasso.with(context)
                .load(new File(thumbnailPath))
                .resize(160, 160)
                .centerCrop()
                .placeholder(R.drawable.action_video_placeholder)
                .error(R.drawable.action_video_placeholder)
                .into(view);
    }

    public static void loadLargeThumbnail(Context context, String thumbnailPath, ImageView view) {
        Picasso.with(context)
                .load(new File(thumbnailPath))
                .resize(250, 250)
                .centerCrop()
                .placeholder(R.drawable.action_video_placeholder)
                .error(R.drawable.action_video_placeholder)
                .into(view);
    }


    public static boolean hasMicrophone(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public static String generateAudioFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "AUDIO_" + timeStamp + "_" + Constants.AUDIO_BASENAME;
    }

    public static Uri generateVideoFileUri() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "NoteTakingApp");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Timber.i("%s: failed to create directory", Constants.LOG_TAG);
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File videoFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        return Uri.fromFile(videoFile);
    }


    public static void deleteItemFromDatabase(final Activity context, final long id) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.note_deletion_dialog_title))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (id > 0) {
                            String[] args = {String.valueOf(id)};
                            new DeleteItemsThread(context, args).start();
                        }
                        context.finish();
                    }
                })
                .positiveText(context.getString(R.string.dialog_positive_text))
                .negativeText(context.getString(R.string.dialog_negative_text))
                .show();
    }


}
