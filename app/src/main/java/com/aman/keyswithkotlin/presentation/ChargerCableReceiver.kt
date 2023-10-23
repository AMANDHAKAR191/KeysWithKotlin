package com.aman.keyswithkotlin.presentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.BatteryManager
import android.widget.Toast

//class ChargerCableReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val action = intent.action
//        if (Intent.ACTION_POWER_CONNECTED == action) {
//            Toast.makeText(context, "check charger", Toast.LENGTH_SHORT).show()
//            BatteryManager.BATTERY_PLUGGED_USB
//            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
//            val usbDevice: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
//            if (usbDevice != null) {
//                // Check the USB device properties to determine its functionality
//                val productName = usbDevice.productName
//
//                if (productName != null && productName.contains("Charger")) {
//                    Toast.makeText(context, "check: productName: $productName", Toast.LENGTH_SHORT).show()
//                    // The connected device appears to be a charger
//                    // You can take appropriate actions here
//                }
//            }
//        }
//    }
//}

class ChargerCableReceiver(
    val updateState:((Boolean)->Unit)? = null
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get the battery status and the charge plug type from the intent
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        // Check if the device is charging or not
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Check if the charge plug is USB or not
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB

        // If the device is charging and the charge plug is USB, then disable data transfer
        if (isCharging && usbCharge) {
            Toast.makeText(context, "check charger", Toast.LENGTH_SHORT).show()
            updateState?.let {
                it(true)
            }
            // TODO: Write your code to disable data transfer here
        }else{
            updateState?.let {
                it(false)
            }
        }
    }
}


