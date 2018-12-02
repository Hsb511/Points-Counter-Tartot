package team23.lecompteurdetartot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team23.lecompteurdetartot.database.PartyDAO;
import team23.lecompteurdetartot.database.PlayerDAO;
import team23.lecompteurdetartot.java_object.GameType;
import team23.lecompteurdetartot.java_object.Party;
import team23.lecompteurdetartot.java_object.Player;

public class BeloteActivity extends AppCompatActivity {
    //graphical parameters
    private static int NAME_SIZE_MAX = 14;
    private int screenWidth;
    private int screenHeight;
    private int columnMax = 0;

    //party parameters
    private Party currentParty;
    private String partyName;
    private GameType gameType;
    private int playersAmount;
    private ArrayList<Player> playersList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belote);

        //Setting gameType
        String gameTypeName = getIntent().getStringExtra("gameType");
        if (gameTypeName.equals("Belote")) {
            gameType = GameType.BELOTE;
        } else if (gameTypeName.equals("Manille")) {
            gameType = GameType.MANILLE;
        } else {
            Log.i("test", "pas de game");
        }

        //Setting players amount and column number for the GridLayout
        playersAmount = getIntent().getIntExtra("playersAmount", 0);
        GridLayout gamesLayout = findViewById(R.id.games_grid_layout);
        gamesLayout.setColumnCount(playersAmount + 2);

        //Setting the screen metrix
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //The case where we want to create a Party
        if (playersAmount != 0) {
            //We getting the info submitted in the partyCreationActivity such as partyName and the players name, to create the players and the party in the database
            partyName = getIntent().getStringExtra("partyName");
            ((TextView) findViewById(R.id.party_name_text_view)).setText(partyName);

            //opening transaction with DAO
            PlayerDAO playerDAO = new PlayerDAO(getApplication());
            playerDAO.open();

            //Initializing the table
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), 0,0);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), 0,1);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), playersAmount-1,0);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), playersAmount-1,1);
            for (int i = 0; i < playersAmount - 2; i++) {
                // we get the player from the Intent and then we get it back from the database
                Player player1 = getIntent().getParcelableExtra("player " + String.valueOf(2*i));
                player1 = playerDAO.getPlayerByName(player1.getName());
                playersList.add(player1);

                Player player2 = getIntent().getParcelableExtra("player " + String.valueOf(2*i+1));
                player2 = playerDAO.getPlayerByName(player2.getName());
                playersList.add(player2);

                // we create the first row by filling it with the names
                TextView playerNameTV = createTextViewForGridLayout(resizeName(player1.getName(), NAME_SIZE_MAX) + " / " + resizeName(player2.getName(), NAME_SIZE_MAX), screenWidth);
                // for the borders
                addFrameLayoutInTable(playerNameTV, i+1, 0);


                TextView scoreTV = createTextViewForGridLayout("0", screenWidth);
                scoreTV.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                scoreTV.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                addFrameLayoutInTable(scoreTV, i+1, 1);
            }

            PartyDAO partyDAO = new PartyDAO(getApplicationContext());
            partyDAO.open();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();

            currentParty = partyDAO.createParty(gameType, partyName, dateFormat.format(date), playersList);
            initializeGameLayout(playersList);
        }

        Button game_button = findViewById(R.id.add_game_button);
        game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.points_belote_layout).setVisibility(View.VISIBLE);
                //findViewById(R.id.add_game_button).setVisibility(View.GONE);

            }
        });

        Button menu_button = findViewById(R.id.go_main_menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMenuIntent = new Intent(BeloteActivity.this, MainActivity.class);
                startActivity(goMenuIntent);

            }
        });
    }


    /** method for adding FrameLayout with TextView, i.e. playerName or score
     * @param textView
     * @param row
     * @param column
     */
    protected void addFrameLayoutInTable(TextView textView, int row, int column) {
        textView.setHeight(Math.round((40*screenHeight)/1080));
        textView.setGravity(Gravity.CENTER);
        GridLayout gamesLayout = findViewById(R.id. games_grid_layout);
        FrameLayout borderLayout = createFrameLayoutForGrid(row, column, playersAmount + 1, columnMax);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        borderLayout.setLayoutParams(frameLayoutParams);
        borderLayout.addView(textView);
        GridLayout.Spec rowSpec = GridLayout.spec(column, 1);
        GridLayout.Spec columnSpec = GridLayout.spec(row, 1);
        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        gamesLayout.addView(borderLayout, gridLayoutParams);
    }

    /**
     * method for adding FrameLayout with imageButton, i.e. deletion Button
     * @param imageButton
     * @param row
     * @param column
     */
    protected void addFrameLayoutInTable(ImageButton imageButton, int row, int column) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.height = Math.round((40*screenHeight)/1080);
        lp.width = Math.round((30*screenWidth)/768);
        imageButton.setLayoutParams(lp);
        GridLayout gamesLayout = findViewById(R.id. games_grid_layout);
        FrameLayout borderLayout = createFrameLayoutForGrid(row, column, playersAmount + 1, columnMax);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(Math.round((40*screenHeight)/1080), Math.round((30*screenWidth)/768));
        borderLayout.setLayoutParams(frameLayoutParams);
        borderLayout.addView(imageButton);
        GridLayout.Spec rowSpec = GridLayout.spec(column, 1);
        GridLayout.Spec columnSpec = GridLayout.spec(row, 1);
        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        gamesLayout.addView(borderLayout, gridLayoutParams);
    }

    /**
     * instantiate a FrameLayout to fit a GridLayout, depending on where it is it will have different border style
     * @param columnIndex
     * @param rowIndex
     * @param columnMax
     * @param rowMax
     * @return
     */
    private FrameLayout createFrameLayoutForGrid (int columnIndex, int rowIndex, int columnMax, int rowMax) {
        int paddingLeft = 1;
        int paddingTop = 1;
        int paddingRight = 1;
        int paddingBottom = 1;

        if (columnIndex == 0) {
            paddingLeft = 2;
        } else if (columnIndex == columnMax) {
            paddingRight = 2;
        }
        if (rowIndex == 0) {
            paddingTop = 2;
        } else if (rowIndex == rowMax) {
            //paddingBottom = 2;
        }
        FrameLayout borderLayout = new FrameLayout(getApplicationContext());
        borderLayout.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
        borderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return borderLayout;
    }

    /**
     * instantiate a TextView corresponding to the first column, most of the time it will be for indexing.
     * @param index
     * @param show
     * @return
     */
    private TextView createTextViewFirstColumn(int index, boolean show) {
        TextView tv = new TextView(getApplicationContext());
        tv.setContentDescription("column_index");
        tv.setText(String.valueOf(index));
        tv.setWidth(Math.round((30*screenWidth)/768));
        tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        if (show) {
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (index > 9) {
            tv.setTextSize(11);
        }
        return tv;
    }

    /**
     * instantiating TextView for player Name or score in the table
     * @param resizedString
     * @param width
     * @return
     */
    private TextView createTextViewForGridLayout(String resizedString, int width) {
        TextView playerNameTV = new TextView(getApplicationContext());
        playerNameTV.setText(resizedString);
        playerNameTV.setGravity(Gravity.CENTER);
        playerNameTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        playerNameTV.setWidth(2*Math.round((width - Math.round((100*screenWidth)/768))/playersAmount));
        playerNameTV.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        playerNameTV.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        return playerNameTV;
    }

    /**
     * Simple method to resize player's name for the table depending on
     * @param name the name submitted in the PartyCreationActivity
     * @param max cf NAME_MAX_SIZE
     * @return
     */
    private String resizeName(String name, int max) {
        if (name.length() > max - (2 * playersAmount)) {
            return name.substring(0, max - (2 * playersAmount) - 1) + ".";
        } else {
            return name;
        }
    }

    private void initializeGameLayout(final ArrayList<Player> playersList) {
        //Button initialization
        Button validateGameButton = findViewById(R.id.add_game_belote_button);
        validateGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //findViewById(R.id.add_game_button).setVisibility(View.VISIBLE);
                findViewById(R.id.points_belote_layout).setVisibility(View.GONE);

                //We ge the LinearLayout
                LinearLayout pointsLayout = findViewById(R.id.points_linear_layout);

                //Creating a new horizontal LinearLayout for the team taking.
                final LinearLayout teamlinearLayout = findViewById(R.id.team_linear_layout);

                //We adding a Textview and a checkbox for the 2 teams to team_linear_layout
                for (int i=0; i<2; i++) {
                    ToggleButton teamToggleButton = new ToggleButton(getApplicationContext());
                    teamToggleButton.setText(playersList.get(i).getName() + " / " + playersList.get(2*i+1).getName());
                    teamlinearLayout.addView(teamToggleButton);
                }

                for (int i=0; i<2; i++) {
                    final ToggleButton teamToggleButton = (ToggleButton) teamlinearLayout.getChildAt(i+1);
                    final int rank = i;

                    teamToggleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rank == 0) {
                                ToggleButton theOtherTeamToggleButton = ((ToggleButton) teamlinearLayout.getChildAt(2));
                                if (theOtherTeamToggleButton.isChecked()) {
                                    theOtherTeamToggleButton.setChecked(false);
                                }
                            } else if (rank == 1) {
                                ToggleButton theOtherTeamToggleButton = ((ToggleButton) teamlinearLayout.getChildAt(1));
                                if (theOtherTeamToggleButton.isChecked()) {
                                    theOtherTeamToggleButton.setChecked(false);
                                }
                            }
                        }
                    });

                }



            }
        });
    }
}
