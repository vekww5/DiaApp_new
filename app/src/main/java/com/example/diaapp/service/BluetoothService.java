package com.example.diaapp.service;

import android.annotation.SuppressLint;
import android.app.Service;

import android.content.Context;
import android.content.Intent;

import static android.content.ContentValues.TAG;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;


import android.util.Log;


import com.example.diaapp.utility.SampleGattAttributes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
Класс, который будет обрабатывать все события BLE.
Он должен реализовывать интерфейс BluetoothGattCallback.
В этом классе необходимо определить методы, которые будут вызываться при соединении, отключении, отправке и получении данных через BLE.
 */

public class BluetoothService extends Service {

    //STATE
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
    private static final int STATE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    private static final int STATE_CONNECTED = BluetoothProfile.STATE_CONNECTED;

    //ACTION
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String EXTRA_DATA = "EXTRA_DATA";

    //Bluetooth
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    //Айдишники

    private static final UUID GLUCOSE_SERVICE = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb"); // UUID основной службы глюкозы
    private static final UUID CURRENT_TIME_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb"); // UUID службы текущего времени
    private static final UUID DEVICE_INFO_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"); // UUID службы информации об устройстве
    private static final UUID CONTOUR_SERVICE = UUID.fromString("00000000-0002-11e2-9e96-0800200c9a66"); //  UUID профиль службы глюкозы
    @Override
    public void onCreate() {

        //foregroundServiceStarter = new ForegroundServiceStarter(getApplicationContext(), this);
        //foregroundServiceStarter.start();

        System.out.println("Мой сервис");
    }

    // реализ методов, которые будут вызываться при соединении, отключении, отправке и получении данных через BLE.
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        // вызывается, когда изменяется состояние соединения между устройствами
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange: Connected to GATT server. ");

                // успешно подключен к серверу GATT
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);

                int bondstate = gatt.getDevice().getBondState();
                // Обрабатываем bondState
                if (bondstate == BluetoothDevice.BOND_NONE || bondstate == BluetoothDevice.BOND_BONDED) {
                    // попытки обнаружения сервисов после успешного подключения.
                    mBluetoothGatt.discoverServices();
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange: State disconnected.");
                // отключен от сервера GATT
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }

        }

        // запрашивает информацию с BLE-устройства
        // функция вызывается, когда устройство сообщает о доступных сервисах.

        @SuppressLint("MissingPermission")
        @Override
        public void  onServicesDiscovered(BluetoothGatt gatt, int status) {

            Log.d(TAG, "Services discovered start");

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d(TAG, "onServicesDiscovered received: " + status);

                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic cha : service.getCharacteristics()) {
                        UUID c = cha.getUuid();
                        if (c != null) {
                            Log.d(TAG, "рус Found: " + c);


//                             BluetoothGattDescriptor descriptor = cha.getDescriptor(c);
//                             Log.i(TAG,"BluetoothGattDescriptor is::" + descriptor.getUuid());
//                             descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                             gatt.writeDescriptor(descriptor);
//                             gatt.setCharacteristicNotification(cha, true);
//                             cha = service.getCharacteristic(UUID.fromString(cha.getValue().toString()));

                            //final BluetoothGattDescriptor bdescriptor = gattCharacteristic.getDescriptor(UUID.fromString(HM10Attributes.CLIENT_CHARACTERISTIC_CONFIG));



                            //final BluetoothGattDescriptor bdescriptor = cha.getDescriptor(c);
                            //Log.i(TAG, "Bluetooth Notification Descriptor found: " + bdescriptor.getUuid());

                            //if (bdescriptor != null) {


                            //bdescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            //mBluetoothGatt.writeDescriptor(bdescriptor);

                            //   cha = service.getCharacteristic(UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb"));
                            //cha = service.getCharacteristic(UUID.fromString("8ec90003-f315-4f60-9fb8-838830daea50"));

                            //  gatt.setCharacteristicNotification(cha, true);
                            //   gatt.readCharacteristic(cha);
                            // if (cha != null) {
                            //byte[] bytes = cha.getValue();
                            //System.out.println(bytes.toString());
                            //Log.d(TAG, " рус Enable notification: " + Arrays.toString(bytes));
                            //   }
                            //

                            //}
                        }
                    }
                }

                /*
                BluetoothGattService gattService = gatt.getService(SampleGattAttributes.NRF_UART_TX);
                if (gattService != null) {
                     gattService.getCharacteristics();
                }

                gattService = gatt.getService(SampleGattAttributes.TRANSMITER_PL_RX_TX);
                if (gattService != null) {
                    gattService.getCharacteristics();
                }

                gattService = gatt.getService(SampleGattAttributes.NRF_UART_SERVICE);
                if (gattService != null) {
                    gattService.getCharacteristics();
                }*/


                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        // вызывается, когда удаленное устройство ответило на запрос на чтение характеристики.
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            Log.w(TAG, "Зашли в onCharacteristicRead: " + characteristic);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            Log.w(TAG, "Зашли в onCharacteristicChanged: " + characteristic);

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

    };

    // метод для отправки широковещательного сообщения о полученных данных через BLE
    // action - строку, представляющую действие, связанное с полученными данными (например, ACTION_DATA_AVAILABLE).
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    // метод для отправки широковещательного сообщения о полученных данных через BLE
    // action - строку, представляющую действие, связанное с полученными данными (например, ACTION_DATA_AVAILABLE).
    // characteristic - объект BluetoothGattCharacteristic, представляющий характеристику, связанную с полученными данными.
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // Это специальная обработка для профиля измерения сердечного ритма. Парсинг данных
        // выполняется согласно спецификациям профиля:

        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml

        if (SampleGattAttributes.CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
            final String value = new String(characteristic.getValue());
            Log.d(TAG, String.format("Received new value: " + new String(value)));
            intent.putExtra(EXTRA_DATA, String.valueOf(value));
        }

        final String value = new String(characteristic.getValue());
        intent.putExtra(EXTRA_DATA, String.valueOf(value));

            /* Пример

            if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                    Log.d(TAG, "Heart rate format UINT16.");
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    Log.d(TAG, "Heart rate format UINT8.");
                }
                final int heartRate = characteristic.getIntValue(format, 1);
                Log.d(TAG, String.format("Received heart rate: %d", heartRate));
                intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            } else {
                // For all other profiles, writes the data formatted in HEX.
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for(byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                            stringBuilder.toString());
                }
            }*/


        sendBroadcast(intent);
    }


    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        Log.w(TAG, "Зашли в readCharacteristic: " + characteristic);

        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new BluetoothService.LocalBinder();


    /**
     * Инициализирует ссылку на локальный адаптер Bluetooth.
     *
     * @return Возвращает true, если инициализация прошла успешно.
     */
    public boolean initialize() {
        // Для уровня API 18 и выше получить ссылку на BluetoothAdapter через менеджер Bluetooth.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Подключается к серверу GATT, размещенному на устройстве Bluetooth LE.
     *
     * @param address Адрес целевого устройства.
     *
     * @return Возвращает true, если соединение установлено успешно. Результат соединения
     * сообщается асинхронно через
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Ранее подключенное устройство. Попробуйте переподключиться.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // Если мы хотим подключиться к устройству напрямую, поэтому мы устанавливаем autoConnect параметр в false.
        // В большинстве случаев для параметра autoconnect должно быть установлено значение false.
        // Установка его в true вызовет неожиданное поведение

        mBluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Отключает существующее соединение или отменяет ожидающее соединение. Результат отключения
     * сообщается асинхронно через
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * После использования данного устройства BLE приложение должно вызвать этот метод, чтобы убедиться,
     * что ресурсы высвобождены должным образом.
     */
    @SuppressLint("MissingPermission")
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Включает или отключает уведомление о заданной характеристике.
     *
     * @param characteristic Характеристика, в соответствии с которой нужно действовать.
     * @param enabled Если значение true, включите уведомление.  False otherwise.
     */
    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        //Если вы хотите включить уведомления для своего периферийного устройства,
        //независимо от того, какого оно типа, вы должны это сделать.
        if (SampleGattAttributes.CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    SampleGattAttributes.BLE_NOTIFICATION);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Метод для получения списка поддерживаемых GATT-сервисов на устройстве
     * Извлекает список поддерживаемых служб GATT на подключенном устройстве. Это должно быть
     * вызвано только после успешного завершения {@code BluetoothGatt#discoverServices()}.
     *
     * @return A {@code List} из поддерживаемых сервисов.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public int getStatus(){
        return mConnectionState;
    }

    @Override
    public void onDestroy() {
        System.out.println("Закончили");
    }
}

