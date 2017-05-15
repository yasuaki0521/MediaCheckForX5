package com.yaslam.taimei.mediacheckforx5;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
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

//        mediaScan();

//        searchExternalAudioFiles();
        searchExternalAudioPlaylist();

//        searchExternalImages();
//        searchExternalFiles();
//        insertMediaDataToMediaStore();
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
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        if (cursor.getCount() > 0) {
            /*
            while (!cursor.isAfterLast()) {
                adapter.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                Log.v("audio title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            }
            */
            do {
//                adapter.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                Log.v("audio id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                Log.v("audio title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            } while (cursor.moveToNext());
        }
    }

    private void searchExternalAudioPlaylist() {

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] columns = new String[]{
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };

        Cursor cursor = cr.query(
                uri,
                columns,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                Log.v("Playlist Name", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
                Long playlistID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));

                searchExternalAudioPlaylistMember(playlistID);
//                addMemberToPlaylist(playlistID);
//                addToPlaylist(cr, playlistID);

            } while (cursor.moveToNext());
        }
    }

    private void searchExternalAudioPlaylistMember(Long playlistID) {

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);

        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] columns = new String[]{
//                MediaStore.Audio.Playlists.Members.CONTENT_DIRECTORY,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members._ID
        };

        Cursor cursor = cr.query(
                uri,
                columns,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                Log.v("Member Dir", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
            } while (cursor.moveToNext());
        }
    }


    /**
     * http://relog.xii.jp/mt5r/2011/03/android-9.html
     * @param playlistID
     */
    private void addMemberToPlaylist(Long playlistID) {
        /*
        V/audio id: 34842
        V/audio title: Beethoven: String Quartet #1 in F, Op.18: II. Adagio affettuoso ed appassionato
         */

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);

        ContentValues contentvalues = new ContentValues();
        Integer audio_id = 34842;
        contentvalues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio_id);
        contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 2); // 必ず必要. 無いとインサートできない

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri result_uri = null;
        result_uri = contentResolver.insert(uri, contentvalues);
        if(result_uri == null){
            Log.d("test", "fail add music : " + playlistID + ", " + audio_id + ", is null");
        }else if(((int) ContentUris.parseId(result_uri)) == -1){
            Log.d("test", "fail add music : " + playlistID + ", " + audio_id + ", " + result_uri.toString());
        }else{
            Log.d("test", "add music : " + playlistID + ", " + audio_id + ", " + result_uri.toString());
        }
    }

    /**
     * https://www.codota.com/android/scenarios/52fcbcfcda0abd6fad727f99/android.content.ContentResolver?tag=dragonfly
     * @param resolver
     * @param playlistId
     */
    public void addToPlaylist(ContentResolver resolver, long playlistId/*, QueryTask query*/)
    {
        Integer audio_id = 34577;
        if (playlistId == -1)
            return;

        // Find the greatest PLAY_ORDER in the playlist
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        int base = 0;
        if (cursor.moveToLast())
            base = cursor.getInt(0) + 1;
        cursor.close();


//        int count = from.getCount();
//        if (count > 0) {
            ContentValues[] values = new ContentValues[1];
//            for (int i = 0; i != count; ++i) {
//                from.moveToPosition(i);
                ContentValues value = new ContentValues();
                value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1));
                value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio_id);
                values[0] = value;
//            }
            resolver.bulkInsert(uri, values);
//        }

//        from.close();

//        return count;
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

        String filePath = "/mnt/external_sd1/Music/Fantome FLAC/01-Michi.flac";
        File audioFile = new File(filePath);

        String mSelectionClause = MediaStore.Files.FileColumns.DATA + " = ?";
        String[] mSelectionArgs = {audioFile.getAbsolutePath()};
        int row = getContentResolver().delete(
                uri,
                mSelectionClause,
                mSelectionArgs
        );



        if (cursor.getCount() > 0) {
            do {
                //Log.v("audio title in Files", cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)));
                if (cursor.getString(titleIndex) != null) {
                    Log.v("audio title in Files", cursor.getString(titleIndex));
                    //Log.v("audio type  in Files", cursor.getString(typeIndex));
                }
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

    private void insertMediaDataToMediaStore() {
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

        String filePath = "/mnt/external_sd1/Music/Fantome FLAC/01-Michi.flac";
        File audioFile = new File(filePath);

        // MediaMetadataRetrieverのインスタンスを用意する
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        // オーディオファイルを設定
        metadataRetriever.setDataSource(filePath);

        // タイトルの取得
        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        // アーチスト名の取得
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        // アルバム名の取得
        String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String mimetype = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);

        Log.v("metadata:", "title:" + title + ", artist:" + artist + ", album:" + album);


        // retrieve more metadata, duration etc.
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.AudioColumns.DATA, audioFile.getAbsolutePath());
        contentValues.put(MediaStore.Audio.AudioColumns.TITLE, title);
        contentValues.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, title);
        contentValues.put(MediaStore.Audio.AudioColumns.ARTIST, artist);
        contentValues.put(MediaStore.Audio.AudioColumns.ARTIST_KEY, artist);
        contentValues.put(MediaStore.Audio.AudioColumns.ALBUM, album);
        contentValues.put(MediaStore.Audio.AudioColumns.ALBUM_KEY, album);
        contentValues.put(MediaStore.Audio.AudioColumns.IS_MUSIC, true);
        contentValues.put(MediaStore.Audio.AudioColumns.MIME_TYPE, mimetype);
        // more columns should be filled from here

        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Uri externalUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Uri insertUri = getContentResolver().insert(externalUri, contentValues);
        Log.d("insert file url", insertUri.toString());


        /*
        static const KeyMap kKeyMap[] = {
                { "tracknumber", METADATA_KEY_CD_TRACK_NUMBER },
                { "discnumber", METADATA_KEY_DISC_NUMBER },
                { "album", METADATA_KEY_ALBUM },
                { "artist", METADATA_KEY_ARTIST },
                { "albumartist", METADATA_KEY_ALBUMARTIST },
                { "composer", METADATA_KEY_COMPOSER },
                { "genre", METADATA_KEY_GENRE },
                { "title", METADATA_KEY_TITLE },
                { "year", METADATA_KEY_YEAR },
                { "duration", METADATA_KEY_DURATION },
                { "writer", METADATA_KEY_WRITER },
                { "compilation", METADATA_KEY_COMPILATION },
                { "isdrm", METADATA_KEY_IS_DRM },
                { "date", METADATA_KEY_DATE },
                { "width", METADATA_KEY_VIDEO_WIDTH },
                { "height", METADATA_KEY_VIDEO_HEIGHT },

        }
        */
    }
}
