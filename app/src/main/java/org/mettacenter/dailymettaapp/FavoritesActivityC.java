package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by sunyata on 2015-09-05.
 */
public class FavoritesActivityC
        extends AppCompatActivity {

    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_favorites);
        FavoritesFragmentC tFavoritesFragment = new FavoritesFragmentC();
        getFragmentManager().beginTransaction().replace(
                R.id.fragment_container_favorites, tFavoritesFragment)
                .commit();
    }

}
