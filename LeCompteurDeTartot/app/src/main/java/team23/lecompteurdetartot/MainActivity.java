package team23.lecompteurdetartot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    final static int LOGO_DIMENSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        resizeLogo();

        Button partyCreationButton = findViewById(R.id.create_new_party_button);
        partyCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPartyCreationActivity = new Intent(MainActivity.this, PartyCreationActivity.class);
                startActivity(goToPartyCreationActivity);
            }
        });

        Button playerActivityRedirection = findViewById(R.id.look_players_button);
        playerActivityRedirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPlayerActivity = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(goToPlayerActivity);
            }
        });

        Button partyListActivityRedirection = findViewById(R.id.look_parties_button);
        partyListActivityRedirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPartyListActivity = new Intent(MainActivity.this, PartyListActivity.class);
                startActivity(goToPartyListActivity);
            }
        });
    }

    /**
     * this private method is to resize the Bitmap and has been found here https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
     * @param bm the Bitmap to resize
     * @param newWidth the new width of the Bitmap
     * @param newHeight the new height of the Bitmap
     * @return the resized Bitmap
     */
    private Bitmap getResizedBitmap (Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    /**
     * this private procedure is called in the onCreate() to resize correctly the logo on the menu depending on the screen size
     */
    private void resizeLogo() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        ImageView tartotLogo = findViewById(R.id.tartot_logo_iv);
        Bitmap tartotLogoBp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_tartot);
        int logoHeight = tartotLogoBp.getHeight();
        int logoWidth = tartotLogoBp.getWidth();
        int logoNewHeight = height/LOGO_DIMENSION;
        int logoNewWidth = Math.round(logoWidth/ ((float) (logoHeight/logoNewHeight)));
        Bitmap tartot_logo_resized = getResizedBitmap(tartotLogoBp, logoNewWidth, logoNewHeight);
        tartotLogo.setImageBitmap(tartot_logo_resized);
    }
}
