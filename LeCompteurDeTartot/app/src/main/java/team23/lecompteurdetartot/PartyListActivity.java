package team23.lecompteurdetartot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import team23.lecompteurdetartot.database.PartyDAO;
import team23.lecompteurdetartot.java_object.Party;

public class PartyListActivity extends ListActivity {

    private PartyDAO datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);

        datasource = new PartyDAO(this);
        datasource.open();

        List<Party> values = datasource.getAllParties();
        /*
        List<FrameLayout> names = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            FrameLayout fl = new FrameLayout(getApplicationContext());
            TextView tv = new TextView(getApplicationContext());
            tv.setText(values.get(i).getName());
            Button deleteButton = new Button(getApplicationContext());
            fl.addView(deleteButton);
            fl.addView(tv);
            names.add(fl);
        }


        // utilisez SimpleCursorAdapter pour afficher les
        // éléments dans une ListView
        ArrayAdapter<FrameLayout> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        setListAdapter(adapter);
        */
        List<String> names = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            names.add(values.get(i).getPartyName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        setListAdapter(adapter);
    }

    // Sera appelée par l'attribut onClick
    // des boutons déclarés dans main.xml
    /*
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Player> adapter = (ArrayAdapter<Player>) getListAdapter();
        Player player;
        switch (view.getId()) {
            case R.id.add:
                String[] players = new String[] { "Cool", "Very nice", "Hate it" };
                int nextInt = new Random().nextInt(3);
                // enregistrer le nouveau commentaire dans la base de données
                player = datasource.createPlayer(comments[nextInt]);
                adapter.add(player);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    player = (Player) getListAdapter().getItem(0);
                    datasource.deletePlayer(player);
                    adapter.remove(player);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }*/

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
