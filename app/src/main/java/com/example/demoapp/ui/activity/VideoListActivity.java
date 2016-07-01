package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.demoapp.R;
import com.example.demoapp.common.BaseActivity;
import com.example.demoapp.common.Constants;
import com.example.demoapp.ui.fragment.VideoListFragment;

public class VideoListActivity extends BaseActivity implements VideoListFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VideoListActivity.class);
        activity.startActivityForResult(intent, Constants.ITEM_VIDEO);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display page title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, VideoListFragment.newInstance())
                    .commit();
        }

    }

    // contract method
    @Override
    public void onClick(String title, String filePath, String mimeType) {
        // return the video title/path & mimeType to the calling activity
        final Intent intent = new Intent();
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);

        new MaterialDialog.Builder(VideoListActivity.this)
                .title("Confirm video selection")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        VideoListActivity.this.setResult(RESULT_OK, intent);
                        VideoListActivity.this.finish();
                    }
                })
                .positiveText("Save")
                .negativeText("Cancel")
                .show();
    }



}