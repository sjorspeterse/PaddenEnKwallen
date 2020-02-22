package com.sjorscode.paddenenkwallen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import android.util.Log
import java.io.BufferedWriter
import java.io.IOException
import java.util.*

class ConnectThread(device: BluetoothDevice) {
    private val bluetoothSerialPortUuid = "00001101-0000-1000-8000-00805F9B34FB"
    private val myUUID = UUID.fromString(bluetoothSerialPortUuid)
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val TAG = "ConnectThread"
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(myUUID)
    }

    fun connect(): Boolean {
        Log.i(TAG, "ConnectThread is running")
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()
        if (mmSocket == null) return false
//        mmSocket?.use { socket ->
        // Connect to the remote device through the socket. This call blocks
        // until it succeeds or throws an exception.
        Log.i(TAG, "connect: " + mmSocket?.isConnected)
        try {
            mmSocket?.connect()
        } catch (e: IOException) {
            Log.w(TAG, "connect: " + e.message)
        }
        if (mmSocket?.isConnected == true) {
            Log.i(TAG, "Connected!")
        } else {
            Log.i(TAG, "not connected :(")
            return false
        }
        sendInt()
        return true

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
//            manageMyConnectedSocket(socket)
//        }
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    private fun sendInt() {
        Log.i(TAG, "sendInt: 83")
        mmSocket?.outputStream?.write(83)
        SystemClock.sleep(500)
    }
}
