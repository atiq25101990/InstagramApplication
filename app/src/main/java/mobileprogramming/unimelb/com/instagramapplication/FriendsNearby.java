package mobileprogramming.unimelb.com.instagramapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsNearby extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_nearby);
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

        Button bluetoothSearch = (Button) findViewById(R.id.bluetoothTrigger);
        bluetoothSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
                // BluetoothAdapter through BluetoothManager.
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                }

                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();

                // Checks if Bluetooth is supported on the device.
                if (mBluetoothAdapter == null) {
                    Toast.makeText(FriendsNearby.this, "Bluetooth not supported!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                /**Else list people in recycler view such that when they are selected.
                 * an entry is pushed to server with their uuid and timestamp.
                 * While fetching photos, implement a check to see if the uuid
                 * of uploaded is in the table of nearbyfriends and timestamp within last
                 * 15 minutes, show friend in range.
                 */

                /*
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                }
                else
                {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
                */


            }
        });
    }

}
