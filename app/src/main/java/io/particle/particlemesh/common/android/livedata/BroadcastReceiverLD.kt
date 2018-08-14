package io.particle.particlemesh.common.android.livedata

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.particle.particlemesh.common.android.filterFromAction
import io.particle.particlemesh.meshsetup.utils.checkIsThisTheMainThread


open class BroadcastReceiverLD<T>(
        context: Context,
        private val broadcastAction: String,
        private val intentValueTransformer: (Intent) -> T
) : MutableLiveData<T?>() {

    protected val appCtx = context.applicationContext

    private val innerReceiver = Receiver()

    override fun onActive() {
        super.onActive()
        checkIsThisTheMainThread()
        appCtx.registerReceiver(innerReceiver, innerReceiver.filter)
    }

    override fun onInactive() {
        super.onInactive()
        checkIsThisTheMainThread()
        appCtx.unregisterReceiver(innerReceiver)
    }

    private fun onBroadcastReceived(intent: Intent) {
        value = intentValueTransformer(intent)
    }

    private inner class Receiver : BroadcastReceiver() {
        val filter = filterFromAction(broadcastAction)
        override fun onReceive(context: Context, intent: Intent) = onBroadcastReceived(intent)
    }
}