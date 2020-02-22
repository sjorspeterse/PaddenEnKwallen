package com.sjorscode.paddenenkwallen;

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED
import android.bluetooth.BluetoothAdapter.EXTRA_LOCAL_NAME
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class AcceptThread {
    private val TAG = "AcceptThread"
    private val NAME = "PaddenEnKwallen"
    private val bluetoothSerialPortUuid = "00001101-0000-1000-8000-00805F9B34FB"
    private val myUUID = UUID.fromString(bluetoothSerialPortUuid)
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, myUUID)
    }

    fun run() {
        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            Log.i(TAG, "Looping")
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                Log.i(TAG, "Somebody connected!")
                manageMyConnectedSocket(it)
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    private fun manageMyConnectedSocket(it: BluetoothSocket) {
        thread(start = true) {
            val inputStream = it.inputStream
            val reader0 = inputStream.reader()
            val reader1 = BufferedReader(reader0)
            Log.i(TAG, "manageMyConnectedSocket: isConnected = " + it.isConnected)
            while (it.isConnected) {
                if (reader1.ready()) {
                    Log.i(TAG, "manageMyConnectedSocket: something available: " + reader1.read())
                }
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}

class BluetoothNameReceiver : BroadcastReceiver() {
    private val TAG = "BluetoothNameReceiver"
    private lateinit var mainActivity: MainActivity

    fun setMainActivity(activity: MainActivity) {
        mainActivity = activity
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_LOCAL_NAME_CHANGED) return

        Log.i(
            TAG, "onReceive: Local Bluetooth name changed to " + intent.extras?.get(
                EXTRA_LOCAL_NAME
            )
        )
        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter.name = "Dungeon Master"
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        mainActivity.startActivity(discoverableIntent)
        val acceptThread = AcceptThread()
        thread(start = true) {
            Log.i(TAG, "starting thread...")
            acceptThread.run()
        }
    }
}
