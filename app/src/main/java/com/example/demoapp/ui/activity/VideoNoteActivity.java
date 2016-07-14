package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.LoadInfoItemTask;
import com.example.demoapp.ui.fragment.VideoNoteFragment;

import java.io.File;

public class VideoNoteActivity extends AppCompatActivity
        implements VideoNoteFragment.Contract{

    private CoordinatorLayout mLayout;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VideoNoteActivity.class);
        activity.startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public static void launch(Activity activity, View layout, long id, String filePath, String previewPath, String mimeType) {

        Intent intent = new Intent(activity, VideoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_PREVIEW_PATH, previewPath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(layout.findViewById(R.id.item_thumbnail), activity.getString(R.string.thumbnail_transition))
                );
        activity.startActivity(intent, options.toBundle());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        VideoNoteFragment fragment = (VideoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
            String previewPath = getIntent().getStringExtra(Constants.ITEM_PREVIEW_PATH);
            String mimeType = getIntent().getStringExtra(Constants.ITEM_MIME_TYPE);
            fragment = VideoNoteFragment.newInstance(id, filePath, previewPath, mimeType);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    // impl contract methods
    @Override
    public void playVideo(String filePath, String mimeType) {
        // play video onClick
        if(filePath != null && mimeType != null) {
            Uri video = Uri.fromFile(new File(filePath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(video, mimeType);
            if (Utils.isAppInstalled(this, intent)) {
                startActivity(intent);
            } else {
                Utils.showSnackbar(mLayout, "No suitable app found to play video");
            }
        } else {
            Utils.showSnackbar(mLayout, "Error, video file not found");
        }
    }

    @Override
    public void displayPhotoInfo(long id) {
        new LoadInfoItemTask(VideoNoteActivity.this).execute(id);
    }

    @Override
    public void delete(final long id) {
        Utils.deleteItemFromDevice(this, id);
    }


}
