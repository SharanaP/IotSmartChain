/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sharan.iotsmartchain.BlueTooth;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class TargetGattAttributes {

    public static String TARGET_BLE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static String TARGET_BLE_CHARACTERISTIC = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String TEST_BLE_SERVICE ="edfec62e-9910-0bac-5241-d8bda6932b00";
    public static String TEST_BLE_CHARACTERISTIC = "edfec62e-9910-0bac-5241-d8bda6932b01";

    //TITO
    public static String TITO_BLE_SERVICE ="edfec62e-9910-0bac-5241-d8bda6932a2f";
    public static String TITO_BLE_READ_CHARACTERISTIC = "15005991-b131-3396-014c-664c9867b917";
    public static String TITO_BLE_WRITE_CHARACTERISTIC = "2d86686a-53dc-25b3-0c4a-f0e10c8dee20";

    public static String getTargetBleService() {
        return TARGET_BLE_SERVICE;
    }

    public static void setTargetBleService(String targetBleService) {
        TARGET_BLE_SERVICE = targetBleService;
    }

    public static String getTargetBleCharacteristic() {
        return TARGET_BLE_CHARACTERISTIC;
    }

    public static void setTargetBleCharacteristic(String targetBleCharacteristic) {
        TARGET_BLE_CHARACTERISTIC = targetBleCharacteristic;
    }

    public static String getTitoBleService() {
        return TITO_BLE_SERVICE;
    }

    public static void setTitoBleService(String titoBleService) {
        TITO_BLE_SERVICE = titoBleService;
    }

    public static String getTitoBleReadCharacteristic() {
        return TITO_BLE_READ_CHARACTERISTIC;
    }

    public static void setTitoBleReadCharacteristic(String titoBleReadCharacteristic) {
        TITO_BLE_READ_CHARACTERISTIC = titoBleReadCharacteristic;
    }

    public static String getTitoBleWriteCharacteristic() {
        return TITO_BLE_WRITE_CHARACTERISTIC;
    }

    public static void setTitoBleWriteCharacteristic(String titoBleWriteCharacteristic) {
        TITO_BLE_WRITE_CHARACTERISTIC = titoBleWriteCharacteristic;
    }
}
