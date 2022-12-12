package sing.shadow

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.abs

/**
 * @Author:         Sing
 * @CreateDate:     2022/12/12 12:04
 * @Description:    自定义阴影
 *
 *        paint.setShadowLayer(float radius, float dx, float dy, int shadowColor);
 *        这个方法可以达到这样一个效果，在使用canvas画图时给视图顺带上一层阴影效果。
 *        简单介绍一下这几个参数：
 *               radius: 阴影半径，主要可以控制阴影的模糊效果以及阴影扩散出去的大小。
 *               dx：阴影在X轴方向上的偏移量
 *               dy: 阴影在Y轴方向上的偏移量
 *               shadowColor： 阴影颜色。
 *
 *        其中涉及到几个属性，阴影的宽度，view到ViewGroup的距离，如果视图和父布局一样大的话，那阴影就不好显示，
 *        如果要能够显示出来就必须设置clipChildren=false。
 *        大部分背景都是有圆角的，比如上图中的圆角，需要达到高度还原阴影的效果就是的阴影的圆角和背景保持一致。
 */
class ShadowLayout : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    /**
     * 阴影颜色
     */
    private var mShadowColor = 0

    /**
     * 阴影的扩散范围(也可以理解为扩散程度)
     */
    private var mShadowLimit = 0f

    /**
     * 阴影的圆角大小
     */
    private var mCornerRadius = 0f

    /**
     * x轴的偏移量
     */
    private var mDx = 0f

    /**
     * y轴的偏移量
     */
    private var mDy = 0f

    /**
     * 左边是否显示阴影
     */
    private var leftShow = true

    /**
     * 右边是否显示阴影
     */
    private var rightShow = true

    /**
     * 上边是否显示阴影
     */
    private var topShow = true

    /**
     * 下面是否显示阴影
     */
    private var bottomShow = true
    private var mInvalidateShadowOnSizeChanged = true
    private var mForceInvalidateShadow = false

    /**
     * 缓存
     */
    private val cache = HashMap<Key, Bitmap?>()

    override fun getSuggestedMinimumWidth(): Int {
        return 0
    }

    override fun getSuggestedMinimumHeight(): Int {
        return 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && (background == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false
            setBackgroundCompat(w, h)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false
            setBackgroundCompat(right - left, bottom - top)
        }
    }

    fun setInvalidateShadowOnSizeChanged(invalidateShadowOnSizeChanged: Boolean) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged
    }

    fun invalidateShadow() {
        mForceInvalidateShadow = true
        requestLayout()
        invalidate()
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        initAttributes(context, attrs)

        val xPadding = (mShadowLimit + abs(mDx)).toInt()
        val yPadding = (mShadowLimit + abs(mDy)).toInt()

        setPadding(
            if (leftShow) { xPadding }else 0,
            if (topShow) { yPadding } else 0,
            if (rightShow) { xPadding } else 0,
            if (bottomShow) { yPadding } else 0)
    }

    private fun setBackgroundCompat(w: Int, h: Int) {
        val bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowLimit, mDx, mDy, mShadowColor, Color.TRANSPARENT)
        val drawable = BitmapDrawable(resources, bitmap)
        background = drawable
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout, 0, 0)
        try {
            leftShow = attr.getBoolean(R.styleable.ShadowLayout_yc_leftShow, true)
            rightShow = attr.getBoolean(R.styleable.ShadowLayout_yc_rightShow, true)
            bottomShow = attr.getBoolean(R.styleable.ShadowLayout_yc_bottomShow, true)
            topShow = attr.getBoolean(R.styleable.ShadowLayout_yc_topShow, true)

            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_yc_cornerRadius, 0f)
            mShadowLimit = attr.getDimension(R.styleable.ShadowLayout_yc_shadowLimit, 0f)
            mDx = attr.getDimension(R.styleable.ShadowLayout_yc_dx, 0f)
            mDy = attr.getDimension(R.styleable.ShadowLayout_yc_dy, 0f)

            mShadowColor = attr.getColor(R.styleable.ShadowLayout_yc_shadowColor, ContextCompat.getColor(context,R.color.default_shadow_color))
        } finally {
            attr.recycle()
        }
    }


    private fun createShadowBitmap(shadowWidth: Int, shadowHeight: Int, cornerRadius: Float, shadowRadius: Float, dx: Float, dy: Float, shadowColor: Int, fillColor: Int): Bitmap {
        val key = Key("bitmap", shadowWidth, shadowHeight)
        var output = cache[key]
        if (output == null) {
            //根据宽高创建bitmap背景
            output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_4444)
            cache[key] = output
            // 直接创建对象，然后存入缓存之中
        } else {
            // 从缓存中取出对象
        }

        // 缓存数量 cache.size
        val canvas = Canvas(output!!)
        val shadowRect = RectF(shadowRadius, shadowRadius, shadowWidth - shadowRadius, shadowHeight - shadowRadius)
        if (dy > 0) {
            shadowRect.top += dy
            shadowRect.bottom -= dy
        } else if (dy < 0) {
            shadowRect.top += abs(dy)
            shadowRect.bottom -= abs(dy)
        }

        if (dx > 0) {
            shadowRect.left += dx
            shadowRect.right -= dx
        } else if (dx < 0) {
            shadowRect.left += abs(dx)
            shadowRect.right -= abs(dx)
        }

        //创建画笔，设置画笔的颜色，风格
        val shadowPaint = Paint()
        shadowPaint.isAntiAlias = true
        shadowPaint.color = fillColor
        shadowPaint.style = Paint.Style.FILL
        if (!isInEditMode) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor)
        }
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
        return output
    }

    inner class Key(private val name: String?, private val width: Int, private val height: Int) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            val key = other as Key
            if (width != key.width) {
                return false
            }
            if (height != key.height) {
                return false
            }

            return if (name != null){
                name == key.name
            } else {
                key.name == null
            }
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + width
            result = 31 * result + height
            return result
        }
    }
}
