package com.example.diaapp.utility;

import java.util.UUID;


/**
 * Этот класс включает небольшое подмножество стандартных атрибутов GATT.
 */

public class SampleGattAttributes {
    public static final UUID SERVICE_UUID=UUID.fromString("ffc2dedc-bf02-442e-a69d-b5caff5e9b21");
    public static final UUID CHARACTERISTIC_UUID=UUID.fromString("fbdca8dd-1b1d-4a4c-bc07-040e80c1cb63");
    public static final UUID BLE_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Experimental support for "Transmiter PL" from Marek Macner @FPV-UAV
    public static final UUID TRANSMITER_PL_RX_TX = UUID.fromString("c97433f0-be8f-4dc8-b6f0-5343e6100eb4");

    public static final UUID NRF_UART_TX = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public static final UUID NRF_UART_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");

}
