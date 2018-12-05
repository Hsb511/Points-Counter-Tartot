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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team23.lecompteurdetartot.database.PartyDAO;
import team23.lecompteurdetartot.database.PlayerDAO;
import team23.lecompteurdetartot.java_object.Chelem;
import team23.lecompteurdetartot.java_object.GameType;
import team23.lecompteurdetartot.java_object.Party;
import team23.lecompteurdetartot.java_object.Player;

public class BeloteActivity extends AppCompatActivity {
    //graphical parameters
    private static int NAME_SIZE_MAX = 14;
    private int screenWidth;
    private int screenHeight;
    private int currentRow = 0;
    private int scoreTeam1 = 0;
    private int scoreTeam2 = 0;

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
            currentRow ++;
            addFrameLayoutInTable(createTextViewFirstColumn(1, false), playersAmount-1,0);
            addFrameLayoutInTable(createTextViewFirstColumn(1, false), playersAmount-1,1);
            currentRow ++;
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

                updateScore(scoreTeam1, i+1);
            }

            PartyDAO partyDAO = new PartyDAO(getApplicationContext());
            partyDAO.open();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();

            currentParty = partyDAO.createParty(gameType, partyName, dateFormat.format(date), playersList);
        }


        initializeGameLayout(playersList);

        Button game_button = findViewById(R.id.add_game_button);
        game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.points_belote_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.add_game_button).setVisibility(View.GONE);
                findViewById(R.id.go_main_menu_button).setVisibility(View.GONE);
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
        FrameLayout borderLayout = createFrameLayoutForGrid(row, column, playersAmount + 1, currentRow);
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
        FrameLayout borderLayout = createFrameLayoutForGrid(row, column, playersAmount + 1, currentRow);
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

    /**
     * Initialize the GameLayout
     * @param playersList all the players
     */
    private void initializeGameLayout(final ArrayList<Player> playersList) {
        //Button initialization
        Button validateGameButton = findViewById(R.id.add_game_belote_button);
        validateGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout teamLinearLayout = findViewById(R.id.team_linear_layout);
                ToggleButton team1TB = (ToggleButton) teamLinearLayout.getChildAt(1);
                ToggleButton team2TB = (ToggleButton) teamLinearLayout.getChildAt(2);
                if (team1TB.isChecked() || team2TB.isChecked()) {
                    findViewById(R.id.points_belote_layout).setVisibility(View.GONE);
                    findViewById(R.id.go_main_menu_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.add_game_button).setVisibility(View.VISIBLE);
                    addGame();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noTeam), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //We get the LinearLayout
        LinearLayout pointsLayout = findViewById(R.id.points_linear_layout);

        //Calling the horizontal LinearLayout for the team taking and cleaning it
        final LinearLayout teamlinearLayout = findViewById(R.id.team_linear_layout);
        createTeamToggleButton(teamlinearLayout, true);

        final LinearLayout beloteLinearLayout = findViewById(R.id.belote_linear_layout);
        createTeamToggleButton(beloteLinearLayout, false);


        //We instianting the ClickListener for the + and - Button
        for (int i=1; i<6; i++) {
            final LinearLayout cardLinearLayout = (LinearLayout) pointsLayout.getChildAt(i);

            final Button minusButton = (Button) cardLinearLayout.getChildAt(1);
            final TextView amountCardTextView = (TextView) cardLinearLayout.getChildAt(2);
            final int amountCard = Integer.parseInt(amountCardTextView.getText().toString());
            Button plusButton = (Button) cardLinearLayout.getChildAt(3);

            //For local reasons we are forced to get the Layout and the TextView back each call
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout cardLL = (LinearLayout) minusButton.getParent();
                    TextView cardTV = (TextView) cardLL.getChildAt(2);
                    int amount = Integer.parseInt(cardTV.getText().toString());

                    if (amount > 0) {
                        cardTV.setText(String.valueOf(amount - 1));
                    }
                }
            });

            //For local reasons we are forced to get the Layout and the TextView back each call
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout cardLL = (LinearLayout) minusButton.getParent();
                    TextView cardTV = (TextView) cardLL.getChildAt(2);
                    int amount = Integer.parseInt(cardTV.getText().toString());

                    if (cardLL.equals((LinearLayout) findViewById(R.id.jack_linear_layout))) {
                        if (amount < 3) {
                            amount ++;
                        }
                    } else {
                        if (amount < 4) {
                            amount ++;
                        }
                    }

                   cardTV.setText(String.valueOf(amount));
                }
            });

            //Finally we initialize the CheckBox for capot
            final CheckBox capotCheckBox = findViewById(R.id.check_capot);
            capotCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (capotCheckBox.isChecked()) {
                        ((CheckBox) findViewById(R.id.check_dix_der)).setChecked(true);
                        ((CheckBox) findViewById(R.id.check_jack)).setChecked(true);
                        ((CheckBox) findViewById(R.id.check_nine)).setChecked(true);
                        ((TextView) findViewById(R.id.score_ace_text_view)).setText("4");
                        ((TextView) findViewById(R.id.score_ten_text_view)).setText("4");
                        ((TextView) findViewById(R.id.score_king_text_view)).setText("4");
                        ((TextView) findViewById(R.id.score_queen_text_view)).setText("4");
                        ((TextView) findViewById(R.id.amount_jack_text_view)).setText("3");
                    }
                }
            });

        }
    }

    /**
     * Method to graphically add the team Toggle Buttons
     * @param linearLayout
     */
    public void createTeamToggleButton(LinearLayout linearLayout, boolean attackingTeam){
        final boolean team = attackingTeam;
        if (linearLayout.getChildCount() > 1) {
            linearLayout.removeViewAt(1);
            linearLayout.removeViewAt(1);
        }

        //We adding a ToggleButton and a checkbox for the 2 teams to team_linear_layout
        for (int i=0; i<2; i++) {
            String teamValue = resizeName(playersList.get(2*i).getName(), NAME_SIZE_MAX) + " / " + resizeName(playersList.get(2*i+1).getName(), NAME_SIZE_MAX);
            ToggleButton teamToggleButton = new ToggleButton(getApplicationContext());
            teamToggleButton.setText(teamValue);
            teamToggleButton.setTextOff(teamValue);
            teamToggleButton.setTextOn(teamValue);
            linearLayout.addView(teamToggleButton);
        }

        //We instantiating the ClickListener for the newly created ToggleButton
        for (int i=0; i<2; i++) {
            final ToggleButton teamToggleButton = (ToggleButton) linearLayout.getChildAt(i+1);
            final int rank = i;

            teamToggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rank == 0) {
                        ToggleButton theOtherTeamToggleButton = ((ToggleButton) ((LinearLayout) teamToggleButton.getParent()).getChildAt(2));
                        if (team) {
                            theOtherTeamToggleButton.setChecked(!teamToggleButton.isChecked());
                        } else if (theOtherTeamToggleButton.isChecked()) {
                            theOtherTeamToggleButton.setChecked(!teamToggleButton.isChecked());
                        }
                    } else if (rank == 1) {
                        ToggleButton theOtherTeamToggleButton = ((ToggleButton) ((LinearLayout) teamToggleButton.getParent()).getChildAt(1));
                        if (team) {
                            theOtherTeamToggleButton.setChecked(!teamToggleButton.isChecked());
                        } else if (theOtherTeamToggleButton.isChecked()) {
                            theOtherTeamToggleButton.setChecked(!teamToggleButton.isChecked());
                        }
                    }
                }
            });
        }
    }

    public int addGame() {
        LinearLayout teamLinearlayout = findViewById(R.id.team_linear_layout);
        ToggleButton team1TB = (ToggleButton) teamLinearlayout.getChildAt(1);
        ToggleButton team2TB = (ToggleButton) teamLinearlayout.getChildAt(2);
        LinearLayout beloteLinearLayout = findViewById(R.id.belote_linear_layout);
        ToggleButton beloteTeam1TB =  (ToggleButton) beloteLinearLayout.getChildAt(1);
        ToggleButton beloteTeam2TB =  (ToggleButton) beloteLinearLayout.getChildAt(2);
        CheckBox capotCB = findViewById(R.id.check_capot);

        Log.i("testDyn", "Team 1 : " + String.valueOf(team1TB.isChecked()) + " ; Team 2 : " + String.valueOf(team2TB.isChecked()));
        Log.i("testDyn", "Belote 1 : " + String.valueOf(beloteTeam1TB.isChecked()) + " ; Belote 2 : " + String.valueOf(beloteTeam2TB.isChecked()));

        int score = 0;
        int scoreDefense = 0;

        if (capotCB.isChecked()) {
            score += 252;
        } else {
            TextView jackTV = findViewById(R.id.amount_jack_text_view);
            TextView queenTV = findViewById(R.id.score_queen_text_view);
            TextView kingTV = findViewById(R.id.score_king_text_view);
            TextView tenTV = findViewById(R.id.score_ten_text_view);
            TextView aceTV = findViewById(R.id.score_ace_text_view);
            CheckBox nineCB = findViewById(R.id.check_nine);
            int nine = nineCB.isChecked() ? 1 : 0;
            CheckBox jackCB = findViewById(R.id.check_jack);
            int jack = jackCB.isChecked() ? 1 : 0;
            CheckBox derCB = findViewById(R.id.check_dix_der);
            int der = derCB.isChecked() ? 1 : 0;

            score += 2 * Integer.parseInt(jackTV.getText().toString());
            score += 3 * Integer.parseInt(queenTV.getText().toString());
            score += 4 * Integer.parseInt(kingTV.getText().toString());
            score += 10 * Integer.parseInt(tenTV.getText().toString());
            score += 11 * Integer.parseInt(aceTV.getText().toString());
            score += 14 * nine;
            score += 20 * jack;
            score += 10 * der;

            scoreDefense += 162 - score;

            if (team1TB.isChecked() && beloteTeam1TB.isChecked() || team2TB.isChecked() && beloteTeam2TB.isChecked()) {
                score += 20;
            } else if (team1TB.isChecked() && beloteTeam2TB.isChecked() || team2TB.isChecked() && beloteTeam2TB.isChecked()) {
                scoreDefense += 20;
            }
        }

        if (score < scoreDefense) {
            scoreDefense += score;
            score = 0;
            if (team1TB.isChecked() && beloteTeam1TB.isChecked() || team2TB.isChecked() && beloteTeam2TB.isChecked()) {
                score = 20;
            }
        }


        //Creating each TextView for each case in the row
        TextView scoreAttackTextView = createTextViewForGridLayout(String.valueOf(score), screenWidth);
        TextView scoreDefenseTextView  = createTextViewForGridLayout(String.valueOf(scoreDefense), screenWidth);
        TextView leftSideTextView = createTextViewFirstColumn(currentRow - 1, true);
        TextView rightSideTextView = createTextViewFirstColumn(currentRow - 1, false);

        //Adding them in a FrameLayout and then in the GridLayout depending on the attacker
        if (team1TB.isChecked()) {
            addFrameLayoutInTable(leftSideTextView, 0,currentRow);
            addFrameLayoutInTable(scoreAttackTextView,1, currentRow);
            addFrameLayoutInTable(scoreDefenseTextView, 2, currentRow);
            addFrameLayoutInTable(rightSideTextView, 3, currentRow);
            currentRow ++;

            scoreTeam1 += score;
            scoreTeam2 += scoreDefense;

            Log.i("testDyn", "Team 1 : " + String.valueOf(team1TB.isChecked()) + " ; Team 2 : " + String.valueOf(team2TB.isChecked()));

        } else if (team2TB.isChecked()) {
            addFrameLayoutInTable(leftSideTextView, 0,currentRow);
            addFrameLayoutInTable(scoreDefenseTextView, 1, currentRow);
            addFrameLayoutInTable(scoreAttackTextView,2, currentRow);
            addFrameLayoutInTable(rightSideTextView, 3, currentRow);
            currentRow ++;


            scoreTeam1 += scoreDefense;
            scoreTeam2 += score;

            Log.i("testDyn", "Team 1 : " + String.valueOf(team1TB.isChecked()) + " ; Team 2 : " + String.valueOf(team2TB.isChecked()));
        }


        updateScore(scoreTeam1, 1);
        updateScore(scoreTeam2, 2);

        return score;
    }

    private void updateScore(int scoreTeam, int column) {
        TextView scoreTV = createTextViewForGridLayout(String.valueOf(scoreTeam), screenWidth);
        scoreTV.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        scoreTV.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        addFrameLayoutInTable(scoreTV, column, 1);
    }
}
