package com.sjorscode.paddenenkwallen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {
    val toastNotifier = MutableLiveData<String>()
    private val TAG = "MainViewModel"
    private val master by lazy {
        connectToMaster()
    }

    fun toast(text: String) {
        toastNotifier.postValue(text)
    }

    private fun connectToMaster(): Connection? {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.w(TAG, "No bluetooth available")
        }

        val master = getMaster(bluetoothAdapter) ?: return null
        val connection = Connection(master)
        Log.i(TAG, "Connecting...")
        val connected = connection.connect()
        if (connected) {
            toast("Connected!")
        } else {
            toast("Failed connecting")
        }
        return connection
    }

    private fun getMaster(bluetoothAdapter: BluetoothAdapter?): BluetoothDevice? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            if (deviceName == "Dungeon Master") {
                toast("Found a master")
                return device
            }
        }
        toast("Effe pairen met de Dungeon Master nondeju")

        return null
    }

    fun sendInt(value: Int) {
        master?.sendInt(value)
    }
}