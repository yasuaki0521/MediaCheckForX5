package com.yaslam.taimei.mediacheckforx5;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mediaScan();

        searchExternalAudioFiles();
        searchExternalImages();
        searchExternalFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchExternalAudioFiles() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] columns = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE
        };

        Cursor cursor = cr.query(
                uri,
                columns,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        if (cursor.getCount() > 0) {
            /*
            while (!cursor.isAfterLast()) {
                adapter.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                Log.v("audio title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            }
            */
            do {
                adapter.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                Log.v("audio title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            } while (cursor.moveToNext());
        }
    }

    private void searchExternalFiles() {


        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

        Uri uri = MediaStore.Files.getContentUri("external");

        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID,
                /*
                MediaStore.Files.FileColumns.ARTIST,
                MediaStore.Files.FileColumns.ALBUM,
                MediaStore.Files.FileColumns.DURATION,
                MediaStore.Files.FileColumns.TRACK,
                */
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE
        };

        Cursor cursor = cr.query(
                uri,
                columns,
                //selection,
                null,
                null,
                null
        );

        int titleIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
        //int typeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                //Log.v("audio title in Files", cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)));
                Log.v("audio title in Files", cursor.getString(titleIndex));
                //Log.v("audio type  in Files", cursor.getString(typeIndex));
            } while (cursor.moveToNext());
        }
    }

    private void searchExternalImages() {
        ContentResolver cr = getContentResolver();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection =
                {
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME
                };
        //ファイルのパスと名前
        Cursor cursor = cr.query(uri, projection, null, null, null);

        if (cursor == null)
            return;

        int pathIndex = cursor.getColumnIndex(
                MediaStore.Images.Media.DATA);
        int nameIndex = cursor.getColumnIndex(
                MediaStore.Images.Media.DISPLAY_NAME);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i("image", "imageName = " + cursor.getString(nameIndex));
            cursor.moveToNext();
        }
    }

    private void mediaScan() {
        /*
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //File f = new File("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        //File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/1-01 Beethoven_ Piano Sonata #1 In F Minor, Op. 2_1 - 1. Allegro.m4a");
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/01-Michi.flac");
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
         */

        //String[] paths = {Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+ "/01-Michi.m"};
        String[] paths = {Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+ "/01-Michi.m4a"};
        //String[] paths = {Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+ "/01-Michi.mp3"};
        //String[] mimeTypes = {"audio/mpeg"};
        String[] mimeTypes = {"audio/mp4"};
        MediaScannerConnection.scanFile(getApplicationContext(), paths, mimeTypes,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("MediaScanWork", "file " + path
                                + " was scanned seccessfully: " + uri);
                    }
        });
    }
}
