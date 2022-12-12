package android.libs.demo.shadow

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

open class OnSeekProgressChangeListener : OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}