package org.mettacenter.dailymettaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sunyata on 2015-09-05.
 */
public class BookmarksActivityC
        extends AppCompatActivity {

    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        BookmarksFragmentC tBookmarksFragment = new BookmarksFragmentC();
        getFragmentManager().beginTransaction().replace(
                R.id.fragment_container_bookmarks, tBookmarksFragment)
                .commit();
    }

}
