package edu.orangecoastcollege.cs273.fjuarez6.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class loads MusicEvent data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (MusicEvent) with data.
 */

public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static ArrayList<SuperHero> loadJSONFromAsset(Context context) throws IOException {
        ArrayList<SuperHero> allSuperHeroes = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("cs273superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperHeroesJSON = jsonRootObject.getJSONArray("CS273Superheroes");
            int numberOfEvents = allSuperHeroesJSON.length();

            for (int i = 0; i < numberOfEvents; i++) {
                JSONObject SuperHeroJSON = allSuperHeroesJSON.getJSONObject(i);

                SuperHero superHero = new SuperHero();
                superHero.setUsername(SuperHeroJSON.getString("Username"));

                superHero.setName(SuperHeroJSON.getString("Name"));
                superHero.setSuperpower(SuperHeroJSON.getString("Superpower"));
                superHero.setOnething(SuperHeroJSON.getString("OneThing"));

                superHero.setImageName(SuperHeroJSON.getString("ImageName"));

                allSuperHeroes.add(superHero);
            }
        }
        catch (JSONException e)
        {
            Log.e("cs273superheroes", e.getMessage());
        }

        return allSuperHeroes;
    }
}