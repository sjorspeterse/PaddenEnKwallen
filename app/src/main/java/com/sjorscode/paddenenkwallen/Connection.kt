package com.sjorscode.paddenenkwallen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class Connection(device: BluetoothDevice) {
    private val bluetoothSerialPortUuid = "00001101-0000-1000-8000-00805F9B34FB"
    private val myUUID = UUID.fromString(bluetoothSerialPortUuid)
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val TAG = "ConnectThread"
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(myUUID)
    }

    fun connect(): Boolean {
        Log.i(TAG, "Trying to connect to master...")
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()
        if (mmSocket == null) {
            Log.i(TAG, "mmSocket = null")
            return false
        }
        // Connect to the remote device through the socket. This call blocks
        // until it succeeds or throws an exception.
        if (mmSocket?.isConnected == true) {
            Log.i(TAG, "Already connected!")
           return true
        }
        try {
            mmSocket?.connect()
        } catch (e: IOException) {
            Log.w(TAG, "connect: " + e.message)
        }
        return if (mmSocket?.isConnected == true) {
            Log.i(TAG, "Connected!")
            true
        } else {
            Log.i(TAG, "not connected :(")
            false
        }
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    fun sendInt(value: Int) {
        if (mmSocket == null) {
            Log.i(TAG, "sending Int failed: mmSocket == null")
            return
        }
        if(mmSocket?.outputStream == null) {
            Log.i(TAG, "sending Int failed: outputStream == null")
            return
        }
        if(mmSocket?.isConnected != true) {
            Log.i(TAG, "sending Int failed: not connected")
            connect()
        }
        mmSocket!!.outputStream!!.write(value)
        Log.i(TAG, "sendInt: $value")
    }
}
