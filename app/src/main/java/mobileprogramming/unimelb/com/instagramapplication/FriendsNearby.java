package mobileprogramming.unimelb.com.instagramapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import mobileprogramming.unimelb.com.instagramapplication.Share.ShareActivity;
import mobileprogramming.unimelb.com.instagramapplication.adapter.BluetoothDeviceAdapter;

public class FriendsNearby extends AppCompatActivity {
    private static final String TAG = "FriendsNearby";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private ArrayList<String> devices = new ArrayList<>();
    private HashMap<String, String> deviceDetails = new HashMap<>();
    private ArrayList<String> instagramUsersDevice = new ArrayList<>();
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private RecyclerView bluetoothDevList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceDetails.clear();
        devices.clear();

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

        Button bluetoothSearch = (Button) findViewById(R.id.bluetoothTrigger);

        //bluetoothDevices
        RecyclerView bluetoothDevList = (RecyclerView) findViewById(R.id.bluetoothDevices);
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
                String deviceMAC = devices.get(position);
                db.collection("Users")
                        .whereEqualTo("MAC", deviceMAC)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String[] inrange = new String[1];
                        inrange[0] = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Intent intent = new Intent(FriendsNearby.this, ShareActivity.class);
                        intent.putExtra("type", "inrange");
                        intent.putExtra("users", inrange);
                        startActivity(intent);
                    }
                });
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

        bluetoothSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();


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

                //Get a list of all InstaUsers Devices
                CollectionReference usersMAC = db.collection("Users");
                Query query = usersMAC.orderBy("username").limit(100);
                Log.d(TAG, "onClick: Device List Size " + deviceDetails.size());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (task.isSuccessful()) {
                                    String mac;
                                    if (document.contains("MAC")) {
                                        mac = document.getData().get("MAC").toString();
                                        Log.d(TAG, "onComplete: Filtering mac addresses " + mac);

                                        if (deviceDetails.containsKey(mac)) {
                                            devices.add(mac);
                                            Log.d(TAG, "onComplete: Inside if condition. Adding mac: " + mac);
                                        }
                                    }



                                }
                            }
                            bluetoothDeviceAdapter.notifyDataSetChanged();
                        }

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
                deviceDetails.put(device.getAddress(), device.getName());
                Log.d(TAG, device.getName() + "\n" + device.getAddress());
            }
        }
    };

}
