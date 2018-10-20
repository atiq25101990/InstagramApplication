package mobileprogramming.unimelb.com.instagramapplication;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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

import java.util.ArrayList;

import butterknife.BindView;
import mobileprogramming.unimelb.com.instagramapplication.adapter.BluetoothDeviceAdapter;
import mobileprogramming.unimelb.com.instagramapplication.adapter.UsersAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsers;

public class FriendsNearby extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private ArrayList<String> devices = new ArrayList<>();
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private RecyclerView bluetoothDevList;

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

                if (mBluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Toast.makeText(FriendsNearby.this, "Bluetooth not supported!", Toast.LENGTH_SHORT).show();
                }


                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                mBluetoothAdapter.startDiscovery();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

                // Checks if Bluetooth is supported on the device.
                /*
                if (mBluetoothAdapter == null) {
                    Toast.makeText(FriendsNearby.this, "Bluetooth not supported!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                */

                /**Else list people in recycler view such that when they are selected.
                 * an entry is pushed to server with their uuid and timestamp.
                 * While fetching photos, implement a check to see if the uuid
                 * of uploaded is in the table of nearbyfriends and timestamp within last
                 * 15 minutes, show friend in range.
                 */

                //bluetoothDevices

                bluetoothDevList = (RecyclerView) findViewById(R.id.bluetoothDevices);
                bluetoothDevList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendsNearby.this, OrientationHelper.VERTICAL, false);
                bluetoothDevList.setLayoutManager(linearLayoutManager);
                //bluetoothDevList.add("");
                //devices.add("Test1");
                //devices.add("Test2");
                bluetoothDeviceAdapter = new BluetoothDeviceAdapter(FriendsNearby.this, devices);
                bluetoothDevList.setAdapter(bluetoothDeviceAdapter);

                final SwipeAnimation swipeController = new SwipeAnimation(new SwipeAnimationCallbacks() {
                    @Override
                    public void onRightClicked(int position) {
                        devices.remove(position);
                        bluetoothDeviceAdapter.notifyItemRemoved(position);
                        bluetoothDeviceAdapter.notifyItemRangeChanged(position, bluetoothDeviceAdapter.getItemCount());
                    }
                });


                ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                itemTouchhelper.attachToRecyclerView(bluetoothDevList);

                bluetoothDevList.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                        swipeController.onDraw(c);
                    }
                });





            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device.getName() + "\n" + device.getAddress());
                Log.d("BT1", device.getName() + "\n" + device.getAddress());
                //listView.setAdapter(new ArrayAdapter<String>(context,
                        //android.R.layout.simple_list_item_1, mDeviceList));
                bluetoothDeviceAdapter.notifyDataSetChanged();
            }
        }
    };

}
