package android.libs.demo.shadow

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sing.shadow.ShadowView

class MyAdapter(var shadowCardView: ShadowView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(SeekItem.values()[position]){
            SeekItem.FOREGROUND_COLOR,SeekItem.BACKGROUND_COLOR,SeekItem.SHADOW_COLOR -> R.layout.list_item_color_select
            else -> R.layout.list_item_seek_select
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
         if (viewType == R.layout.list_item_color_select) {
             return ShadowViewColorItemHolder(inflater.inflate(R.layout.list_item_color_select, parent, false))
        }
        return ShadowViewSeekItemHolder(inflater.inflate(R.layout.list_item_seek_select, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ShadowViewSeekItemHolder) {
            holder.bind(SeekItem.values()[position], shadowCardView)
        }else if(holder is ShadowViewColorItemHolder){
            holder.bind(SeekItem.values()[position], shadowCardView)
            holder.onClickColor = View.OnClickListener { notifyItemChanged(position) }
        }
    }

    override fun getItemCount(): Int {
        return SeekItem.values().size
    }

    class ShadowViewSeekItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var tvTitle: TextView? = null
        private var tvValue: TextView? = null
        private var seekBar: SeekBar? = null
        init {
            tvTitle = itemView.findViewById<View>(R.id.text_title) as TextView
            tvValue = itemView.findViewById<View>(R.id.text_value) as TextView
            seekBar = itemView.findViewById<View>(R.id.seek_bar) as SeekBar
        }

        fun bind(seekItem: SeekItem, shadowCardView: ShadowView) {
            tvTitle!!.text = seekItem.title
            seekBar!!.setOnSeekBarChangeListener(null)
            when(seekItem){
                SeekItem.SHADOW_RADIUS -> {
                    tvValue!!.text = "${shadowCardView.getShadowRadius().toInt()}"
                    seekBar!!.progress = shadowCardView.getShadowRadius().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object : OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowRadius(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.PADDING -> {
                    tvValue!!.text = "${shadowCardView.paddingLeft}"
                    seekBar!!.progress = shadowCardView.paddingLeft
                    seekBar!!.setOnSeekBarChangeListener(object : OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setPadding(progress, progress, progress, progress)
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.SHADOW_MARGIN -> {
                    tvValue!!.text = "${shadowCardView.getShadowMarginLeft()}"
                    seekBar!!.progress = shadowCardView.getShadowMarginLeft()
                    seekBar!!.setOnSeekBarChangeListener(object : OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowMargin(progress, progress, progress, progress)
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.SHADOW_MARGIN_LEFT -> {
                    tvValue!!.text = "${shadowCardView.getShadowMarginLeft()}"
                    seekBar!!.progress = shadowCardView.getShadowMarginLeft()
                    seekBar!!.setOnSeekBarChangeListener(object : OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowMarginLeft(progress)
                            tvValue!!.text = "$progress"
                            shadowCardView.requestLayout()
                        }
                    })
                }
                SeekItem.SHADOW_MARGIN_TOP -> {
                    tvValue!!.text = "${shadowCardView.getShadowMarginTop()}"
                    seekBar!!.progress = shadowCardView.getShadowMarginTop()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowMarginTop(progress)
                            tvValue!!.text = "$progress"
                            shadowCardView.requestLayout()
                        }
                    })
                }
                SeekItem.SHADOW_MARGIN_RIGHT -> {
                    tvValue!!.text = "${shadowCardView.getShadowMarginRight()}"
                    seekBar!!.progress = shadowCardView.getShadowMarginRight()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowMarginRight(progress)
                            tvValue!!.text = "$progress"
                            shadowCardView.requestLayout()
                        }
                    })
                }
                SeekItem.SHADOW_MARGIN_BOTTOM -> {
                    tvValue!!.text = "${shadowCardView.getShadowMarginBottom()}"
                    seekBar!!.progress = shadowCardView.getShadowMarginBottom()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowMarginBottom(progress)
                            tvValue!!.text = "$progress"
                            shadowCardView.requestLayout()
                        }
                    })
                }
                SeekItem.CORNER_RADIUS -> {
                    tvValue!!.text = "${shadowCardView.getCornerRadiusTL().toInt()}"
                    seekBar!!.progress = shadowCardView.getCornerRadiusTL().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            val p = progress.toFloat()
                            shadowCardView.setCornerRadius(p, p, p, p)
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.CORNER_RADIUS_TOP_LEFT -> {
                    tvValue!!.text = "${shadowCardView.getCornerRadiusTL().toInt()}"
                    seekBar!!.progress = shadowCardView.getCornerRadiusTL().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setCornerRadiusTL(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.CORNER_RADIUS_TOP_RIGHT -> {
                    tvValue!!.text = "${shadowCardView.getCornerRadiusTR().toInt()}"
                    seekBar!!.progress = shadowCardView.getCornerRadiusTR().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setCornerRadiusTR(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.CORNER_RADIUS_BOTTOM_RIGHT -> {
                    tvValue!!.text = "${shadowCardView.getCornerRadiusBR().toInt()}"
                    seekBar!!.progress = shadowCardView.getCornerRadiusBR().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setCornerRadiusBR(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.CORNER_RADIUS_BOTTOM_LEFT -> {
                    tvValue!!.text = "${shadowCardView.getCornerRadiusBL().toInt()}"
                    seekBar!!.progress = shadowCardView.getCornerRadiusBL().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setCornerRadiusBL(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.SHADOW_DX -> {
                    tvValue!!.text = "${shadowCardView.getShadowDx().toInt()}"
                    seekBar!!.progress = shadowCardView.getShadowDx().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowDx(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
                SeekItem.SHADOW_DY -> {
                    tvValue!!.text = "${shadowCardView.getShadowDy().toInt()}"
                    seekBar!!.progress = shadowCardView.getShadowDy().toInt()
                    seekBar!!.setOnSeekBarChangeListener(object :OnSeekProgressChangeListener() {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            super.onProgressChanged(seekBar, progress, fromUser)
                            shadowCardView.setShadowDy(progress.toFloat())
                            tvValue!!.text = "$progress"
                        }
                    })
                }
            }
        }
    }

    class ShadowViewColorItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var tvColorTitle: TextView? = null
        private var tv1: TextView? = null
        private var tv2: TextView? = null
        private var tv3: TextView? = null
        private var tvList: MutableList<TextView> = arrayListOf()
        private var colorList: MutableList<Int> = arrayListOf()

        var onClickColor: View.OnClickListener? = null
        init {
            tvColorTitle = itemView.findViewById(R.id.text_color_title)
            tv1 = itemView.findViewById(R.id.tv1)
            tv2 = itemView.findViewById(R.id.tv2)
            tv3 = itemView.findViewById(R.id.tv3)

            tvList = arrayListOf(tv1!!,tv2!!,tv3!!)
            colorList = arrayListOf(Color.parseColor("#f44336"),Color.parseColor("#03a9f4"),Color.parseColor("#4caf50"))
            for (i in 0 until tvList.size){
                tvList[i].setBackgroundColor(colorList[i])
            }
        }

        fun bind(seekItem: SeekItem, shadowCardView: ShadowView) {
            tvColorTitle!!.text = seekItem.title
            when (seekItem) {
                SeekItem.SHADOW_COLOR -> {
                    for (i in 0 until tvList.size){
                        tvList[i].setOnClickListener {
                            shadowCardView.setShadowColor(colorList[i])
                            onClickColor?.onClick(tvList[i])
                        }
                    }
                }
                SeekItem.FOREGROUND_COLOR -> {
                    for (i in 0 until tvList.size){
                        tvList[i].setOnClickListener {
                            shadowCardView.setForegroundColor(colorList[i])
                            onClickColor?.onClick(tvList[i])
                        }
                    }
                }
                SeekItem.BACKGROUND_COLOR -> {
                    for (i in 0 until tvList.size){
                        tvList[i].setOnClickListener {
                            shadowCardView.setBackgroundColor(colorList[i])
                            onClickColor?.onClick(tvList[i])
                        }
                    }
                }
                else -> {
                }
            }
        }
    }
}
