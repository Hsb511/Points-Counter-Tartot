package team23.lecompteurdetartot;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team23.lecompteurdetartot.database.GameDAO;
import team23.lecompteurdetartot.database.MySQLiteGame;
import team23.lecompteurdetartot.database.PartyDAO;
import team23.lecompteurdetartot.database.PlayerDAO;
import team23.lecompteurdetartot.java_object.Bid;
import team23.lecompteurdetartot.java_object.Chelem;
import team23.lecompteurdetartot.java_object.Game;
import team23.lecompteurdetartot.java_object.GameType;
import team23.lecompteurdetartot.java_object.Handful;
import team23.lecompteurdetartot.java_object.Party;
import team23.lecompteurdetartot.java_object.Player;

public class PartyActivity extends AppCompatActivity {
    private String gameName;
    private int scoreMax = 91;
    private int playersAmount = 0;
    private ArrayList<Player> playersList = new ArrayList<>();
    private String partyName = "";
    private static int NAME_SIZE_MAX = 14;
    private int columnNumber = 0;
    private boolean playWithMisery = true;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Party currentParty;

    private Bid bid = Bid.PASS;
    private GameType gameType = GameType.TAROT;
    private int oneAtEnd = -1;
    private int chelemTeam = -1;
    private int chelemPoints = -1;
    private Handful handfulAttack = null;
    private Handful handfulDefense = null;
    private ArrayList<Player> miseryPlayerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        String gameTypeName = getIntent().getStringExtra("gameType");
        if (gameTypeName.equals("Tarot")) {
            gameType = GameType.TAROT;
        } else if (gameTypeName.equals("Belote")) {
            gameType = GameType.BELOTE;
        } else if (gameTypeName.equals("Manille")) {
            gameType = GameType.MANILLE;
        } else {
            Log.i("test", "pas de game");
        }


        playersAmount = getIntent().getIntExtra("playersAmount", 0);
        GridLayout gamesLayout = findViewById(R.id.games_grid_layout);
        gamesLayout.setColumnCount(playersAmount + 2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        if (playersAmount != 0) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();

            partyName = getIntent().getStringExtra("partyName");
            PlayerDAO playerDAO = new PlayerDAO(getApplication());
            playerDAO.open();
            ((TextView) findViewById(R.id.party_name_text_view)).setText(partyName);

            addFrameLayoutInTable(createTextViewFirstColumn(0, false), 0,0);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), 0,1);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), playersAmount+1,0);
            addFrameLayoutInTable(createTextViewFirstColumn(0, false), playersAmount+1,1);
            for (int i = 0; i < playersAmount; i++) {
                // we get the player from the Intent and then we get it back from the database
                Player player = getIntent().getParcelableExtra("player " + String.valueOf(i));
                player = playerDAO.getPlayerByName(player.getName());

                // we create the first row by filling it with the names
                TextView playerNameTV = createTextViewForPlayerName(resizeName(player.getName(), NAME_SIZE_MAX), screenWidth);
                // for the borders
                addFrameLayoutInTable(playerNameTV, i+1, 0);

                playersList.add(player);

                TextView scoreTV = createTextViewForPlayerName("0", screenWidth);
                scoreTV.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                scoreTV.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                addFrameLayoutInTable(scoreTV, i+1, 1);
            }

            PartyDAO partyDAO = new PartyDAO(getApplicationContext());
            partyDAO.open();
            currentParty = partyDAO.createParty(gameType, partyName, dateFormat.format(date), playersList);
            initializeGameLayout(playersList);

        } else {
            long partyId = getIntent().getLongExtra("partyId", 1);
        }

        Button game_button = findViewById(R.id.go_fragemnt_button);
        game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.game_test_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.go_fragemnt_button).setVisibility(View.GONE);
                //initializeGameLayout(playersList);

            }
        });

        ((Button) findViewById(R.id.shadow_button)).setClickable(false);

    }

    //Methods to update the score table
        //The first one is for adding any text in any row and column
    protected void addFrameLayoutInTable(TextView textView, int row, int column) {
        GridLayout gamesLayout = findViewById(R.id. games_grid_layout);
        FrameLayout borderLayout = createFrameLayoutForGrid(row, column, playersAmount + 1, columnNumber);
        borderLayout.addView(textView);
        GridLayout.Spec rowSpec = GridLayout.spec(column, 1);
        GridLayout.Spec columnSpec = GridLayout.spec(row, 1);
        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        gamesLayout.addView(borderLayout, gridLayoutParams);
    }

        //The second one is for adding the index in the first colum
    private TextView createTextViewFirstColumn(int index, boolean show) {
        TextView tv = new TextView(getApplicationContext());
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

    private String resizeName(String name, int max) {
        if (name.length() > max - (2 * playersAmount)) {
            return name.substring(0, max - (2 * playersAmount) - 1) + ".";
        } else {
            return name;
        }
    }

    private TextView createTextViewForPlayerName (String resizedString, int width) {
        TextView playerNameTV = new TextView(getApplicationContext());
        playerNameTV.setText(resizedString);
        playerNameTV.setGravity(Gravity.CENTER);
        playerNameTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        playerNameTV.setWidth(Math.round((width - Math.round((100*screenWidth)/768))/playersAmount));
        playerNameTV.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        playerNameTV.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        return playerNameTV;
    }

    private FrameLayout createFrameLayoutForGrid (int columnNumber, int rowNumber, int columnMax, int rowMax) {
        int paddingLeft = 1;
        int paddingTop = 1;
        int paddingRight = 1;
        int paddingBottom = 1;

        if (columnNumber == 0) {
            paddingLeft = 2;
        } else if (columnNumber == columnMax) {
            paddingRight = 2;
        }
        if (rowNumber == 0) {
            paddingTop = 2;
        } else if (rowNumber == rowMax) {
            //paddingBottom = 2;
        }
        FrameLayout borderLayout = new FrameLayout(getApplicationContext());
        borderLayout.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
        borderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return borderLayout;
    }


    protected void showDoneLayout(boolean passIsCheched) {

        ConstraintLayout done_layout = findViewById(R.id.done_layout);
        if (passIsCheched) {
            done_layout.setVisibility(View.GONE);
        } else {
            done_layout.setVisibility(View.VISIBLE);
        }
    }

    private void initializeGameLayout(ArrayList<Player> players) {
        //We call the different graphical object for the initialization
        findViewById(R.id.game_test_layout).setVisibility(View.GONE);
        final ToggleButton doneButton = findViewById(R.id.done_button);
        final ToggleButton passButton = findViewById(R.id.pass_button);
        ConstraintLayout done_layout = findViewById(R.id.done_layout);
        final LinearLayout dealer_layout = findViewById(R.id.dealer_linear_layout);
        final LinearLayout contractLayout = findViewById(R.id.contract_layout);
        final LinearLayout oudlersNumberLayout = findViewById(R.id.oudler_number_layout);
        LinearLayout attacker_layout = findViewById(R.id.attacker_layout);
        LinearLayout calledLayout = findViewById(R.id.called_layout);
        SeekBar pointsBar = findViewById(R.id.points_seekBar);
        final EditText attackPoints = findViewById(R.id.attacker_points_edit_text);
        final EditText defensePoints = findViewById(R.id.defense_points_edit_text);
        final Button handfulButton = findViewById(R.id.handful_button);
        final Button oneAtEndButton = findViewById(R.id.one_to_end_button);
        Button chelemButton = findViewById(R.id.chelem_button);
        Button miseryButton = findViewById(R.id.misery_button);


        //default settings
        doneButton.setChecked(true);
        passButton.setChecked(false);
        done_layout.setVisibility(View.VISIBLE);

        //We initialize the dealer's layout
        putPlayersInLayout(players, dealer_layout);

        //We initialize the done and pass buttons
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passIsChecked = passButton.isChecked();
                showDoneLayout(!passIsChecked);                 //the passButton change of state because we clicked on done
                passButton.setChecked(!passIsChecked);
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passIsChecked = passButton.isChecked();
                showDoneLayout(passIsChecked);
                doneButton.setChecked(!passIsChecked);
            }
        });


        //We initialize the contract's buttons (for done only)
        for (int j = 0; j < contractLayout.getChildCount(); j++) {
            final ToggleButton contractButton = (ToggleButton) contractLayout.getChildAt(j);
            contractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bid = bid.intToBid(Integer.parseInt(v.getContentDescription().toString()));
                    for (int k = 0; k < contractLayout.getChildCount(); k ++) {
                        ToggleButton otherButton = (ToggleButton) contractLayout.getChildAt(k);
                        if (!contractButton.getText().equals(otherButton.getText())) {
                            otherButton.setChecked(false);
                        }
                    }
                }
            });
        }

        //We initialize the oudler number layout (for done only)
        for (int j = 1; j < oudlersNumberLayout.getChildCount(); j++) {
            final ToggleButton oudlersButton = (ToggleButton) oudlersNumberLayout.getChildAt(j);
            oudlersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int k = 1; k < oudlersNumberLayout.getChildCount(); k ++) {
                        ToggleButton otherButton = (ToggleButton) oudlersNumberLayout.getChildAt(k);
                        if (!oudlersButton.getText().equals(otherButton.getText())) {
                            otherButton.setChecked(false);
                        }
                    }
                }
            });
        }

        //We initialize the attacker's layout (for done only)
        putPlayersInLayout(players, attacker_layout);

        //We initialize the called's layout (for done and for 5 players only)
        if (players.size() == 5) {
            putPlayersInLayout(players, calledLayout);
        } else {
            calledLayout.setVisibility(View.GONE);
        }

        //We initialize the points of the attacker
        attackPoints.setFilters(new InputFilter[]{new InputFilterMinMax("0", "91")});

        //We initialize the seekbar
        pointsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int points = seekBarValueToPoints(progress);
                String scoreAttack = String.valueOf(points);
                if (points < 10) {
                    scoreAttack = 0 + scoreAttack;
                }
                String scoreDefense = String.valueOf(scoreMax - points);
                if (scoreMax - points < 10) {
                    scoreDefense = 0 + scoreDefense;
                }
                attackPoints.setText(scoreAttack);
                defensePoints.setText(scoreDefense);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //We initialize the points of the defense
        defensePoints.setFilters(new InputFilter[]{new InputFilterMinMax("0", "91")});

        //We initialize the button for the announces
            //We initialize
        if (playWithMisery) {
            miseryButton.setVisibility(View.VISIBLE);
            handfulButton.setTextSize(10);
            oneAtEndButton.setTextSize(8);
            chelemButton.setTextSize(10);
        } else {
            miseryButton.setVisibility(View.GONE);
            handfulButton.setTextSize(14);
            oneAtEndButton.setTextSize(14);
            chelemButton.setTextSize(14);
        }

        //We initialize the buttons for the announces
        initializeAnnounceButtons();
        initializeHandfulLayout();
        initializeChelemLayout();

        //we initialize the button for the game validation
        findViewById(R.id.create_game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO CHECK THE DATA AND ADD THE GAME IN THE PARTY
                boolean createGame = true;
                PlayerDAO playerDAO = new PlayerDAO(getApplicationContext());
                playerDAO.open();
                long dealerId = findPlayerIdInLayout((LinearLayout) findViewById(R.id.dealer_linear_layout));
                int oudlersAmount = -1;
                long attackerId = 0;
                long calledId = 0;


                // we check if the dealer is set and get it
                if (dealerId == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_dealer), Toast.LENGTH_SHORT).show();
                    createGame = false;
                }

                // if we are in a done, we check if the value has been set
                ToggleButton passButton = findViewById(R.id.pass_button);
                boolean pass = passButton.isChecked();
                if (!pass) {
                    //we check if a Bid has been chosen and get it
                    boolean bidFounded = false;
                    for (int i = 0; i < contractLayout.getChildCount(); i++) {
                        ToggleButton bidButton = (ToggleButton) contractLayout.getChildAt(i);
                        if (bidButton.isChecked()) {
                            bidFounded = true;
                            bid = bid.intToBid(Integer.parseInt(bidButton.getContentDescription().toString()));
                        }
                    }
                    if (!bidFounded) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_bid), Toast.LENGTH_SHORT).show();
                        createGame = false;
                    }

                    //We check if the oudlers amount has been chosen and get it
                    boolean oudlersAmountFounded = false;
                    for (int i = 1; i < oudlersNumberLayout.getChildCount(); i++) {
                        ToggleButton oudlersAmountButton = (ToggleButton) oudlersNumberLayout.getChildAt(i);
                        if (oudlersAmountButton.isChecked()) {
                            oudlersAmountFounded = true;
                            oudlersAmount = Integer.parseInt(oudlersAmountButton.getText().toString());
                        }
                    }
                    if (!oudlersAmountFounded) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_oudlers_amount), Toast.LENGTH_SHORT).show();
                        createGame = false;
                    }



                    //we check if the attacker has been set and get it
                    attackerId = findPlayerIdInLayout((LinearLayout) findViewById(R.id.attacker_layout));
                    if (attackerId == 0) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_attacker), Toast.LENGTH_SHORT).show();
                        createGame = false;
                    }

                    //if we have 5 players, we check if the player called has been set and get it
                    if (playersAmount == 5) {
                        calledId = findPlayerIdInLayout((LinearLayout) findViewById(R.id.called_layout));
                        if (calledId == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_called), Toast.LENGTH_SHORT).show();
                            createGame = false;
                        }
                    }
                }

                if (createGame) {
                    findViewById(R.id.game_test_layout).setVisibility(View.GONE);
                    findViewById(R.id.go_fragemnt_button).setVisibility(View.VISIBLE);

                    ArrayList<Game> test = new ArrayList<>();
                    //test.add(new Game(0, Bid.PASS, new Player("test")));
                    GameDAO gameDAO = new GameDAO(getApplicationContext());
                    gameDAO.open();
                    Game newGame;
                    long idMisery1 = 0;
                    long idMisery2 = 0;
                    int handfulPointsAttack = -1;
                    int handfulPointsDefense = -1;
                    if (handfulAttack != null) {
                        handfulPointsAttack = handfulAttack.getPoints();
                    }
                    if (handfulDefense != null) {
                        handfulPointsDefense = handfulDefense.getPoints();
                    }

                    long miseryId1 = 0;
                    long miseryId2 = 0;
                    long miseryId3 = 0;
                    long miseryId4 = 0;
                    long miseryId5 = 0;

                    if (playWithMisery) {
                        if (miseryPlayerList.size() > 0) {
                            Player misery1 = miseryPlayerList.get(0);
                            if (misery1 != null) {
                                miseryId1 = misery1.getId();
                            }
                        } else if (miseryPlayerList.size() > 1) {
                            Player misery2 = miseryPlayerList.get(1);
                            if (misery2 != null) {
                                miseryId2 = misery2.getId();
                            }
                        } else if (miseryPlayerList.size() > 2) {
                            Player misery3 = miseryPlayerList.get(2);
                            if (misery3 != null) {
                                miseryId3 = misery3.getId();
                            }
                        } else if (miseryPlayerList.size() > 3) {
                            Player misery4 = miseryPlayerList.get(3);
                            if (misery4 != null) {
                                miseryId4 = misery4.getId();
                            }
                        } else if (miseryPlayerList.size() > 4) {
                            Player misery5 = miseryPlayerList.get(4);
                            if (misery5 != null) {
                                miseryId5 = misery5.getId();
                            }
                        }
                    }



                    int points = seekBarValueToPoints(((SeekBar) findViewById(R.id.points_seekBar)).getProgress());
                    if (pass) {
                        newGame = gameDAO.createPass(currentParty.getId(), dealerId);
                    } else {
                        MySQLiteGame dbHelper = new MySQLiteGame(getApplicationContext());
                        //dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);
                        newGame = gameDAO.createGame(currentParty.getId(), dealerId, bid.getMultiplicant(), oudlersAmount, points, attackerId, calledId, handfulPointsAttack, handfulPointsDefense, chelemPoints, chelemTeam, oneAtEnd, miseryId1, miseryId2, miseryId3, miseryId4, miseryId5);
                    }

                    //test.add(newGame);
                    addGames(test);
                }
            }
        });

    }

    protected long findPlayerIdInLayout(LinearLayout layout) {
        LinearLayout firstLayout = (LinearLayout) layout.getChildAt(0);
        long dealerId = 0;
        for (int i = 0; i < firstLayout.getChildCount(); i++) {
            if (firstLayout.getChildAt(i) instanceof ToggleButton) {
                ToggleButton buttonPlayer = (ToggleButton) firstLayout.getChildAt(i);
                if (buttonPlayer.isChecked()) {
                    dealerId = Long.parseLong(buttonPlayer.getContentDescription().toString());
                    break;
                }
            }
        }
        LinearLayout secondLayout = (LinearLayout) layout.getChildAt(1);
        for (int j = 0; j < secondLayout.getChildCount(); j++) {
            if (secondLayout.getChildAt(j) instanceof ToggleButton) {
                ToggleButton buttonPlayer = (ToggleButton) secondLayout.getChildAt(j);
                if (buttonPlayer.isChecked()) {
                    dealerId = Long.parseLong(buttonPlayer.getContentDescription().toString());
                    break;
                }
            }
        }
        return dealerId;
    }

    /**
     * method to add one Game in the normal way or several if we want to get a new party
     * @param gameArrayList
     */
    protected void addGames(ArrayList<Game> gameArrayList) {
        for (int i = 0; i < gameArrayList.size(); i++) {
            Game newGame = gameArrayList.get(i);
            columnNumber += 1;
            //we create the textview that shows the index of the game
            TextView gameIndex = createTextViewFirstColumn(columnNumber, true);
            //We add it at the left (0) of the new line (columnNumber + 1)
            addFrameLayoutInTable(gameIndex, 0, columnNumber + 1);

            Toast.makeText(getApplicationContext(), newGame.toString() , Toast.LENGTH_LONG).show();
        }
    }

    private Game createGameWithCurrentValues(boolean pass, long dealerId) {
        //We get the Bid
        Bid bid = Bid.PASS;

        //We get the dealer
        PlayerDAO playerDatabase = new PlayerDAO(getApplicationContext());
        playerDatabase.open();
        Player dealer = playerDatabase.getPlayerById(dealerId);

        /*
        GameDAO gameDatabase = new GameDAO(getApplicationContext());
        gameDatabase.open();
        Game newGame = gameDatabase.createPass(dealerId, dealerId, bid.getMultiplicant()); */

        Game newGame = new Game(dealerId, bid, dealer);
        ToggleButton passButton = findViewById(R.id.pass_button);

        if (pass) {
            return newGame;
        } else {
            return newGame;
        }
    }

    private int seekBarValueToPoints(int progress) {
        return Math.round((progress*scoreMax)/100);
    }

    private void initializeAnnounceButtons() {
        final Button handfulButton = findViewById(R.id.handful_button);
        final ConstraintLayout shadowLayout = findViewById(R.id.shadow_layout);
        //we initialize the button to set the handful(s)
        handfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadowLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.handful_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.game_test_layout).setVisibility(View.GONE);
            }
        });

        //We initialize the button for the validation of the handful
        Button handfulValidateButton = findViewById(R.id.validate_handful_button);
        handfulValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.handful_layout).setVisibility(View.GONE);
                shadowLayout.setVisibility(View.GONE);
                findViewById(R.id.game_test_layout).setVisibility(View.VISIBLE);

                if (((ToggleButton) findViewById(R.id.simple_attack)).isChecked()) {
                    handfulAttack = Handful.SIMPLE_HANDFUL;
                } else if (((ToggleButton) findViewById(R.id.double_attack)).isChecked()) {
                    handfulAttack = Handful.DOUBLE_HANDFUL;
                } else if (((ToggleButton) findViewById(R.id.triple_attack)).isChecked()) {
                    handfulAttack = Handful.TRIPLE_HANDFUL;
                } else {
                    handfulAttack = null;
                }

                if (((ToggleButton) findViewById(R.id.simple_defense)).isChecked()) {
                    handfulDefense = Handful.SIMPLE_HANDFUL;
                } else if (((ToggleButton) findViewById(R.id.double_defense)).isChecked()) {
                    handfulDefense = Handful.DOUBLE_HANDFUL;
                } else if (((ToggleButton) findViewById(R.id.triple_defense)).isChecked()) {
                    handfulDefense = Handful.TRIPLE_HANDFUL;
                } else {
                    handfulDefense = null;
                }

                if (handfulAttack != null) {
                    if (handfulDefense != null) {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            handfulButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            handfulButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        }

                    }
                } else {
                    if (handfulDefense != null) {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            handfulButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            handfulButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                        }
                    }
                }
            }
        });

        //We intialize the button for the small at end
        Button oneAtEndButton = findViewById(R.id.one_to_end_button);
        oneAtEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button oneButton = findViewById(R.id.one_to_end_button);
                Log.i("api_niveau", String.valueOf(Build.VERSION.SDK_INT));
                if (oneAtEnd == -1) {
                    if (Build.VERSION.SDK_INT >= 21 ) {
                        oneButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    }
                    oneAtEnd = 0;
                } else if (oneAtEnd == 0) {
                    if (Build.VERSION.SDK_INT >= 21 ) {
                        oneButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    }
                    oneAtEnd = 1;
                } else {
                    oneAtEnd = -1;
                    if (Build.VERSION.SDK_INT >= 21 ) {
                        oneButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                    }
                }
            }
        });

        //We initialize the button for the chelem
        final Button chelemButton = findViewById(R.id.chelem_button);
        chelemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadowLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.chelem_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.game_test_layout).setVisibility(View.GONE);
                findViewById(R.id.validate_chelem_button).setVisibility(View.VISIBLE);
            }
        });

        //We initialize the button for the chelem validation
        Button validateChelemButton = findViewById(R.id.validate_chelem_button);
        validateChelemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout chelemLayout = findViewById(R.id.chelem_layout);
                chelemLayout.setVisibility(View.GONE);
                shadowLayout.setVisibility(View.GONE);
                findViewById(R.id.game_test_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.validate_chelem_button).setVisibility(View.GONE);
                boolean isChecked = false;
                for (int i = 0; i < chelemLayout.getChildCount(); i++) {
                    ToggleButton button = (ToggleButton) chelemLayout.getChildAt(i);

                    if (button.isChecked()) {
                        isChecked = true;
                        break;
                    }
                }
                if (!isChecked) {
                    chelemPoints = -1;
                    chelemTeam = -1;
                    if (Build.VERSION.SDK_INT >= 21 ) {
                        chelemButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                    }
                } else {
                    if (chelemTeam == 0) {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            chelemButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                        }
                    } else if (chelemTeam == 1) {
                        if (Build.VERSION.SDK_INT >= 21 ) {
                            chelemButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        }
                    }
                }

            }
        });

        //We initialize the button for the misery
        final Button miseryButton = findViewById(R.id.misery_button);
        miseryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadowLayout.setVisibility(View.VISIBLE);
                LinearLayout miseryLayout = findViewById(R.id.misery_layout);
                miseryLayout.removeAllViews();
                miseryLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.game_test_layout).setVisibility(View.GONE);
                findViewById(R.id.validate_misery_button).setVisibility(View.VISIBLE);

                for (int i = 0; i < playersAmount; i++) {
                    String playerName = playersList.get(i).getName();
                    ToggleButton playerButton = new ToggleButton(getApplicationContext());
                    playerButton.setTextOff(playerName);
                    playerButton.setTextOn(playerName);
                    playerButton.setContentDescription(playerName);
                    miseryLayout.addView(playerButton);
                }
            }
        });

        //We initialize the button for the misery validation
        Button miseryValidationButton = findViewById(R.id.validate_misery_button);
        miseryValidationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout miseryLayout = findViewById(R.id.misery_layout);
                PlayerDAO playerDAO = new PlayerDAO(getApplicationContext());
                playerDAO.open();
                for (int i = 0; i < miseryLayout.getChildCount(); i++) {
                    ToggleButton playerButton = (ToggleButton) miseryLayout.getChildAt(i);
                    if (playerButton.isChecked()) {
                        String playerName = playerButton.getContentDescription().toString();
                        Player miseryPlayer = playerDAO.getPlayerByName(playerName);
                        miseryPlayerList.add(miseryPlayer);
                    }
                }

                shadowLayout.setVisibility(View.GONE);
                miseryLayout.setVisibility(View.GONE);
                findViewById(R.id.game_test_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.validate_misery_button).setVisibility(View.GONE);
            }
        });
    }

    private void initializeHandfulLayout() {
        ToggleButton simpleAttack = findViewById(R.id.simple_attack);
        ToggleButton doubleAttack = findViewById(R.id.double_attack);
        ToggleButton tripleAttack = findViewById(R.id.triple_attack);
        ToggleButton simpleDefense = findViewById(R.id.simple_defense);
        ToggleButton doubleDefense = findViewById(R.id.double_defense);
        ToggleButton tripleDefense = findViewById(R.id.triple_defense);

        simpleAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton doubleAttack = findViewById(R.id.double_attack);
                ToggleButton tripleAttack = findViewById(R.id.triple_attack);
                doubleAttack.setChecked(false);
                tripleAttack.setChecked(false);
            }
        });

        doubleAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton simpleAttack = findViewById(R.id.simple_attack);
                ToggleButton tripleAttack = findViewById(R.id.triple_attack);
                simpleAttack.setChecked(false);
                tripleAttack.setChecked(false);
            }
        });

        tripleAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton simpleAttack = findViewById(R.id.simple_attack);
                ToggleButton doubleAttack = findViewById(R.id.double_attack);
                simpleAttack.setChecked(false);
                doubleAttack.setChecked(false);
            }
        });
        simpleDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton doubleDefense = findViewById(R.id.double_defense);
                ToggleButton tripleDefense = findViewById(R.id.triple_defense);
                doubleDefense.setChecked(false);
                tripleDefense.setChecked(false);
            }
        });

        doubleDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton simpleDefense = findViewById(R.id.simple_defense);
                ToggleButton tripleDefense = findViewById(R.id.triple_defense);
                simpleDefense.setChecked(false);
                tripleDefense.setChecked(false);
            }
        });

        tripleDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ToggleButton) findViewById(R.id.simple_defense)).setChecked(false);
                ((ToggleButton) findViewById(R.id.double_defense)).setChecked(false);

            }
        });

    }

    private void initializeChelemLayout() {
        final LinearLayout chelemLayout = findViewById(R.id.chelem_layout);
        for (int i = 0; i < chelemLayout.getChildCount(); i++) {
            final ToggleButton chelemButton = (ToggleButton) chelemLayout.getChildAt(i);
            final int points = Integer.parseInt(chelemButton.getContentDescription().toString());
            chelemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < chelemLayout.getChildCount(); j++) {
                        if (points == -400) {
                            chelemPoints = 200;
                            chelemTeam = 0;
                        } else {
                            chelemPoints = points;
                            chelemTeam = 1;
                        }
                        ToggleButton otherButton = (ToggleButton) chelemLayout.getChildAt(j);
                        if (! (points == Integer.parseInt(otherButton.getContentDescription().toString()))) {
                            otherButton.setChecked(false);
                        }
                    }
                }
            });
        }
    }

    private void putPlayersInLayout(ArrayList<Player> players, LinearLayout layout) {
        int playersAmount = players.size();
        final LinearLayout firstChild = (LinearLayout) layout.getChildAt(0);
        final LinearLayout secondChild = (LinearLayout) layout.getChildAt(1);


        if (playersAmount != 4) {
            secondChild.getChildAt(0).setVisibility(View.GONE);
        } else {
            TextView tv = (TextView) secondChild.getChildAt(0);
            tv.setTextColor(000000);
        }

        for (int i = 0; i < playersAmount; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ToggleButton playerButton = new ToggleButton(getApplicationContext());
            String name = resizeName(players.get(i).getName(), NAME_SIZE_MAX);
            final String idString = String.valueOf(players.get(i).getId());
            if (playersAmount == 3) {
                 name = resizeName(players.get(i).getName(), NAME_SIZE_MAX - 2);
            }
            playerButton.setTextOff(name);
            playerButton.setTextOn(name);
            playerButton.setText(name);
            playerButton.setTextSize(12);
            playerButton.setContentDescription(idString);

            playerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int k = 0; k < firstChild.getChildCount(); k++) {
                        if (firstChild.getChildAt(k) instanceof ToggleButton) {
                            ToggleButton button = (ToggleButton) firstChild.getChildAt(k);
                            if (!idString.equals(button.getContentDescription()))
                            button.setChecked(false);
                        }
                    }
                    for (int k = 0; k < secondChild.getChildCount(); k++) {
                        if (secondChild.getChildAt(k) instanceof ToggleButton) {
                            ToggleButton button = (ToggleButton) secondChild.getChildAt(k);
                            if (!idString.equals(button.getContentDescription()))
                                button.setChecked(false);
                        }
                    }
                }
            });
            if (playersAmount == 3) {
                firstChild.addView(playerButton);
            } else if (playersAmount == 5 || playersAmount == 4) {
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Math.round((70*screenHeight)/1080));
                lp.setMargins(0,-8,0,-8);
                if (i < Math.round(playersAmount / 2)) {

                    firstChild.addView(playerButton);
                } else {
                    secondChild.addView(playerButton);
                }
            }
            lp.weight = 1;
            playerButton.setLayoutParams(lp);

        }
    }
}
