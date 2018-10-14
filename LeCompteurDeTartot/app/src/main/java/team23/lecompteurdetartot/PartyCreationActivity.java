package team23.lecompteurdetartot;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaCodec;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team23.lecompteurdetartot.database.PlayerDAO;
import team23.lecompteurdetartot.java_object.GameType;
import team23.lecompteurdetartot.java_object.Player;

public class PartyCreationActivity extends AppCompatActivity {
    protected int playersAmount = 0;
    private String gameTpe = "Tarot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_creation);

        //Set the spinners
        Spinner spinner = findViewById(R.id.players_amount_spinner);
        setSpinner(spinner, false);

        Spinner typeSpinner = findViewById(R.id.game_type_spinner);
        setSpinner(typeSpinner, true);

        //Set the listView
        final LinearLayout playersListLayout = findViewById(R.id.players_list_layout);
        playersAmount = Integer.parseInt(spinner.getSelectedItem().toString());
        setPlayerListLayout(playersAmount);

        //Set the validation Button
        Button validationButton = findViewById(R.id.validate_creation_button);
        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we create the Intent to go to the PartyActivity
                Intent goToSettingsActivity = new Intent(PartyCreationActivity.this, PartyActivity.class);
                //we add the playersAmount
                goToSettingsActivity.putExtra("playersAmount", playersAmount);

                //we add the partyName, by default it's the default string + the date
                String partyName = ((EditText) findViewById(R.id.party_name_edit_text)).getText().toString();
                if (partyName.equals("")) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    partyName = getResources().getString(R.string.default_party_name) + " " + dateFormat.format(date);
                }
                goToSettingsActivity.putExtra("partyName", partyName);
                goToSettingsActivity.putExtra("gameType", gameTpe);

                //we add the players instantiated with the names
                boolean goToNextActivity = true;
                ArrayList<String> newPlayers = new ArrayList<>();
                for (int i = 0; i < playersAmount; i++) {
                    EditText playerEditText = (EditText) playersListLayout.getChildAt(i);
                    String name = playerEditText.getText().toString();
                    Log.i("regex", String.valueOf(Pattern.matches("^[a-zA-Z0-9éèùàç@ëôâê]{1,23}$", name)) + " , " + name);
                    if (!Pattern.matches("^[a-zA-Z0-9éèùàç@ëôâê_]{1,23}$", name)) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.regex_toast), Toast.LENGTH_SHORT).show();
                        goToNextActivity = false;
                        break;
                    }
                    if (name.equals("")) {
                        Toast pieceToast= Toast.makeText(getApplicationContext(), playerEditText.getHint() + " " + getResources().getString(R.string.no_name_toast), Toast.LENGTH_SHORT);
                        pieceToast.show();
                        goToNextActivity = false;
                        break;
                    } else {
                        for (int k = i+1; k < playersAmount; k++) {
                            if (name.equals(((EditText) playersListLayout.getChildAt(k)).getText().toString())) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.double_toast), Toast.LENGTH_SHORT).show();
                                goToNextActivity = false;
                                break;
                            }
                        }

                        PlayerDAO playerDAO = new PlayerDAO(getApplicationContext());
                        playerDAO.open();

                        Player player;
                        if (playerDAO.findPlayerByName(name)) {
                            player = playerDAO.getPlayerByName(name);

                        } else {
                            player = playerDAO.createPlayer(name, 0, 0);
                            newPlayers.add(player.getName());
                        }

                        goToSettingsActivity.putExtra("player " + String.valueOf(i), player);
                    }

                }

                if (goToNextActivity) {
                    startActivity(goToSettingsActivity);
                    //we create the toast to show how many players have been created
                    int newPlayersAmount = newPlayers.size();
                    String textToast = String.valueOf(newPlayersAmount) + " ";
                    Resources stringResource = getResources();
                    if (newPlayersAmount < 2) {
                        textToast += stringResource.getString(R.string.new_player_toast);
                        if (newPlayersAmount == 1) {
                            textToast += ": " + newPlayers.get(0);
                        }
                    } else {
                        textToast += stringResource.getString(R.string.new_players_toast);
                        for (int i = 0; i < newPlayersAmount; i++) {
                            textToast += " " + newPlayers.get(i);
                        }
                    }
                    Toast.makeText(getApplicationContext(), textToast,Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void setSpinner(final Spinner spinner, boolean which) {

        if (which) {

            String[] myResArray = getResources().getStringArray(R.array.game_type_array);
            List<String> typeList  = Arrays.asList(myResArray);

            ArrayAdapter<String> adapters = new ArrayAdapter<>(this, R.layout.spinner_item, typeList);
            adapters.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter(adapters);

        } else {
            ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                    R.array.players_amount_array, R.layout.spinner_item);


            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter(adapter);

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> av, View view, int parent, long id) {
                Log.i("spinner", "début onitem" );
                String value = ((TextView) view).getText().toString();


                if (view != null) {
                    Log.i("spinner", value);
                    if (value.indexOf('a') >= 0 || value.indexOf('e') >= 0) {
                        gameTpe = value;
                        Spinner playerAmountSpinner = findViewById(R.id.players_amount_spinner);
                        if (!value.equals("Tarot")) {
                            playersAmount = 4;
                            playerAmountSpinner.setEnabled(false);
                            playerAmountSpinner.setSelection(1);
                        } else {
                            playerAmountSpinner.setEnabled(true);
                        }
                    } else {
                        playersAmount = Integer.parseInt(value);
                        setPlayerListLayout(playersAmount);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }
    /**
     * procedure that change dynamically the edittext of the players
     * @param playersAmount the amount of players that we have chosen with the spinner (by default it's 3)
     */
    protected void setPlayerListLayout(int playersAmount) {
        LinearLayout playersListView = findViewById(R.id.players_list_layout);
        int childCount = playersListView.getChildCount();
        //if the new number of players is more than the one chosen before
        if (childCount > playersAmount) {
            for (int i = childCount; i > playersAmount; i--) {
                Log.i("test", String.valueOf(i));
                playersListView.removeViewAt(i-1);
            }
        }

        //otherwise we add new EditTexts
        else {
            for (int i = childCount; i < playersAmount; i++) {
                EditText playerEditText = new EditText(getApplicationContext());
                playerEditText.setHint(getResources().getString(R.string.player_edit_text) + " " + String.valueOf(i+1));
                playerEditText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                playerEditText.setTextColor(getResources().getColor(R.color.colorPrimary));
                playersListView.addView(playerEditText);
            }
        }


    }
}
