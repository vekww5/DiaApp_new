package com.example.diaapp.user_fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BluetoothScanFragment extends Fragment {

    public static String DEVICE_NAME = "DEVICE_NAME";
    public static String DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private Button scan;
    private ListView list;

    private final static String TAG = BluetoothScanFragment.class.getSimpleName();
    private static final long SCAN_PERIOD = 30000;
    private boolean is_scanning;

    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private ArrayList<BluetoothDevice> found_devices;
    private BluetoothAdapter bluetooth_adapter;
    private BluetoothLeScanner lollipopScanner;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private Map<String, byte[]> adverts = new HashMap<>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void run() {
                            if (device.getName() != null && device.getName().length() > 0) {
                                mLeDeviceListAdapter.addDevice(device);
                                if (scanRecord != null)
                                    adverts.put(device.getAddress(), scanRecord);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

    private ScanCallback mScanCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.scan_device_fragment, container, false);

        final BluetoothManager bluetooth_manager = (BluetoothManager) getActivity().
                getSystemService(Context.BLUETOOTH_SERVICE);

        bluetooth_adapter = bluetooth_manager.getAdapter();
        mHandler = new Handler();

        if (bluetooth_adapter == null) {
            Toast.makeText(getContext(), "bluetooth not supported", Toast.LENGTH_LONG).show();
            return view;
        }

        if (!bluetooth_manager.getAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        scan = (Button) view.findViewById(R.id.btn_scanning);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doScan();
            }
        });

        //scan for Bluetooth devices
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        list = (ListView) view.findViewById(R.id.list_scan);
        list.setAdapter(mLeDeviceListAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onListItemClick(i, l);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initializeScannerCallback();
        }

        return view;
    }


    @SuppressLint("MissingPermission")
    private void initializeScannerCallback() {
        Log.d(TAG, "initializeScannerCallback");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onBatchScanResults(final List<ScanResult> results) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ScanResult result : results) {
                                BluetoothDevice device = result.getDevice();
                                if (device.getName() != null && device.getName().length() > 0) {
                                    mLeDeviceListAdapter.addDevice(device);
                                    try {
                                        if (result.getScanRecord() != null)
                                            adverts.put(device.getAddress(), result.getScanRecord().getBytes());
                                    } catch (NullPointerException e) {
                                        //
                                    }
                                }
                            }
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    final BluetoothDevice device = result.getDevice();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String deviceName = device.getName();
                            if (deviceName != null && deviceName.length() > 0) {
                                mLeDeviceListAdapter.addDevice(device);
                                try {
                                    if (result.getScanRecord() != null)
                                        adverts.put(device.getAddress(), result.getScanRecord().getBytes());
                                } catch (NullPointerException e) {
                                    //
                                }
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        if (mLeDeviceListAdapter != null) {
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //doScan();
    }


    private boolean doScan() {

        BluetoothManager bluetooth_manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        Toast.makeText(getContext(), "scanning", Toast.LENGTH_LONG).show();

        if (bluetooth_manager == null) {
            Toast.makeText(getContext(), "This device does not seem to support bluetooth", Toast.LENGTH_LONG).show();
            return true;
        } else {
            if (!bluetooth_manager.getAdapter().isEnabled()) {
                Toast.makeText(getContext(), "Bluetooth is turned off on this device currently", Toast.LENGTH_LONG).show();
                return true;
            } else {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Toast.makeText(getContext(), "The android version of this device is not compatible with Bluetooth Low Energy", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanLeDeviceLollipop(true);
        } else {
            scanLeDevice(true);
        }
        return true;
    }


    @SuppressLint("MissingPermission")
    private synchronized void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Log.d(TAG, "Start scan 19");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    is_scanning = false;
                    try {
                        if ((bluetooth_adapter != null) && (mLeScanCallback != null)) {
                            bluetooth_adapter.stopLeScan(mLeScanCallback);
                        }
                    } catch (NullPointerException e) {
                        // concurrency pain
                    }
                }
            }, SCAN_PERIOD);

            is_scanning = true;
            if (bluetooth_adapter != null) bluetooth_adapter.startLeScan(mLeScanCallback);
        } else {
            is_scanning = false;
            if (bluetooth_adapter != null && bluetooth_adapter.isEnabled()) {
                try {
                    bluetooth_adapter.stopLeScan(mLeScanCallback);
                } catch (NullPointerException e) {
                    // concurrency related
                }
            }
        }
    }

    @TargetApi(21)
    @SuppressLint("MissingPermission")
    private synchronized void scanLeDeviceLollipop(final boolean enable) {
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lollipopScanner = bluetooth_adapter.getBluetoothLeScanner();
            }
            if (lollipopScanner != null) {
                Log.d(TAG, "Starting scanner 21");
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        is_scanning = false;
                        if (bluetooth_adapter != null && bluetooth_adapter.isEnabled()) {
                            try {
                                lollipopScanner.stopScan(mScanCallback);
                            } catch (IllegalStateException e) {
                            }
                            //TODO invalidateOptionsMenu();
                        }
                    }
                }, SCAN_PERIOD);
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                is_scanning = true;
                if (bluetooth_adapter != null && bluetooth_adapter.isEnabled()) {
                    lollipopScanner.startScan(null, settings, mScanCallback);
                }
            } else {
                try {
                    scanLeDevice(true);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to scan for ble device", e);
                }
            }
        } else {
            is_scanning = false;
            if (bluetooth_adapter != null && bluetooth_adapter.isEnabled()) {
                lollipopScanner.stopScan(mScanCallback);
            }
        }
        //TODO invalidateOptionsMenu();
    }


    // Обработка нажатий на лист
    @SuppressLint("MissingPermission")
    protected void onListItemClick(int position, long id) {
        Log.d(TAG, "Item Clicked");

        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);

        if (device == null || device.getName() == null) return;

        //final Intent intent = new Intent(this, DeviceControlActivity.class);
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        DEVICE_NAME = device.getName();
        DEVICE_ADDRESS =  device.getAddress();


        if (is_scanning) {
            //scanLeDevice(false);
            //TODO Првоерить надо это добавить или хватит code in Pause
            //bluetooth_adapter.stopLeScan(mLeScanCallback);
            is_scanning = false;
        }
        //startActivity(intent);










        //Toast.makeText(this, "connecting to device", Toast.LENGTH_LONG).show();
        //GATT BLE
        //BluetoothGatt mBluetoothGatt = device.connectGatt(getApplicationContext(), true, service.);
        //Log.i(TAG, "Trying to create a new connection.");




        //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //prefs.edit().putString("last_connected_device_address", device.getAddress()).apply();

        // automatically set or unset the option for "Transmiter" device
        /*
        boolean using_transmiter = false;
        try {
            if (device.getName().toLowerCase().contains("limitter")
                    && (adverts.containsKey(device.getAddress())
                    && ((new String(adverts.get(device.getAddress()), "UTF-8").contains("eLeR"))
                    || (new String(adverts.get(device.getAddress()), "UTF-8").contains("data"))))||
                    device.getName().toLowerCase().contains("limitterd")) {
                String msg = "Auto-detected transmiter_pl device!";
                Log.e(TAG, msg);
                //JoH.static_toast_long(msg);
                using_transmiter = true;
            }
            //prefs.edit().putBoolean("use_transmiter_pl_bluetooth", using_transmiter).apply();


        } catch (UnsupportedEncodingException | NullPointerException e) {
            Log.d(TAG, "Got exception in listitemclick: " + Arrays.toString(e.getStackTrace()));
        }*/
    }



    // Class LIST ADAPTER

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
            mInflator = BluetoothScanFragment.this.getLayoutInflater();
        }

        void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final BluetoothDevice device = mLeDevices.get(i);
            if (device != null) {
                //TODO MissingPermission
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                if (deviceName == null) {
                    deviceName = "";
                }
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getAddress());
            }
            return view;
        }
    }

}
