package sing.shadow


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @Author:         Sing
 * @CreateDate:     2022/12/12 12:28
 * @Description:    可以自定义背景阴影效果的控件
 */
class ShadowView :ViewGroup {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
        initPaint()
    }

    companion object {
        private const val DEFAULT_CHILD_GRAVITY = Gravity.TOP or Gravity.START
        private const val SIZE_UNSET = -1
        private const val SIZE_DEFAULT = 0
    }

    private var foregroundDrawable: Drawable? = null
    private val selfBounds = Rect()
    private val overlayBounds = Rect()
    private var foregroundDrawGravity = Gravity.FILL
    private var foregroundDrawBoundsChanged = false
    private val bgPaint = Paint()
    private var shadowColor = 0
    private var foregroundColor = 0
    private var backgroundColor = 0
    private var shadowRadius = 0f
    private var shadowDx = 0f
    private var shadowDy = 1f
    private var cornerRadiusTL = 0f
    private var cornerRadiusTR = 0f
    private var cornerRadiusBL = 0f
    private var cornerRadiusBR = 0f
    private var shadowMarginTop = 0
    private var shadowMarginLeft = 0
    private var shadowMarginRight = 0
    private var shadowMarginBottom = 0

    /**
     * 初始化自定义的attr属性
     * @param context                       context
     * @param attrs                         attrs
     * @param defStyleAttr                  defStyleAttr
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyleAttr, 0)
        shadowColor = a.getColor(R.styleable.ShadowView_shadowColor, ContextCompat.getColor(context, R.color.shadow_view_default_shadow_color))
        foregroundColor = a.getColor(R.styleable.ShadowView_foregroundColor, ContextCompat.getColor(context, R.color.shadow_view_foreground_color_dark))
        backgroundColor = a.getColor(R.styleable.ShadowView_backgroundColor, Color.WHITE)
        shadowDx = a.getFloat(R.styleable.ShadowView_shadowDx, 0f)
        shadowDy = a.getFloat(R.styleable.ShadowView_shadowDy, 1f)
        shadowRadius = a.getDimensionPixelSize(R.styleable.ShadowView_shadowRadius, SIZE_DEFAULT).toFloat()
        val drawable = a.getDrawable(R.styleable.ShadowView_android_foreground)
        if (drawable != null) {
            setForeground(drawable)
        }
        val shadowMargin = a.getDimensionPixelSize(R.styleable.ShadowView_shadowMargin, SIZE_UNSET)
        if (shadowMargin >= 0) {
            shadowMarginTop = shadowMargin
            shadowMarginLeft = shadowMargin
            shadowMarginRight = shadowMargin
            shadowMarginBottom = shadowMargin
        } else {
            shadowMarginTop = a.getDimensionPixelSize(R.styleable.ShadowView_shadowMarginTop, SIZE_DEFAULT)
            shadowMarginLeft = a.getDimensionPixelSize(R.styleable.ShadowView_shadowMarginLeft, SIZE_DEFAULT)
            shadowMarginRight = a.getDimensionPixelSize(R.styleable.ShadowView_shadowMarginRight, SIZE_DEFAULT)
            shadowMarginBottom = a.getDimensionPixelSize(R.styleable.ShadowView_shadowMarginBottom, SIZE_DEFAULT)
        }
        val cornerRadius = a.getDimensionPixelSize(R.styleable.ShadowView_cornerRadius, SIZE_UNSET).toFloat()
        if (cornerRadius >= 0) {
            cornerRadiusTL = cornerRadius
            cornerRadiusTR = cornerRadius
            cornerRadiusBL = cornerRadius
            cornerRadiusBR = cornerRadius
        } else {
            cornerRadiusTL = a.getDimensionPixelSize(R.styleable.ShadowView_cornerRadiusTL, SIZE_DEFAULT).toFloat()
            cornerRadiusTR = a.getDimensionPixelSize(R.styleable.ShadowView_cornerRadiusTR, SIZE_DEFAULT).toFloat()
            cornerRadiusBL = a.getDimensionPixelSize(R.styleable.ShadowView_cornerRadiusBL, SIZE_DEFAULT).toFloat()
            cornerRadiusBR = a.getDimensionPixelSize(R.styleable.ShadowView_cornerRadiusBR, SIZE_DEFAULT).toFloat()
        }
        a.recycle()
    }

    /**
     * 初始化画笔操作
     */
    private fun initPaint() {
        bgPaint.color = backgroundColor
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
        ViewCompat.setBackground(this, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        val layoutParams = layoutParams
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec))
        val shadowMeasureWidthMatchParent = layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT
        val shadowMeasureHeightMatchParent = layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
        var widthSpec = widthMeasureSpec
        if (shadowMeasureWidthMatchParent) {
            val childWidthSize = measuredWidth - shadowMarginRight - shadowMarginLeft
            widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
        }
        var heightSpec = heightMeasureSpec
        if (shadowMeasureHeightMatchParent) {
            val childHeightSize = measuredHeight - shadowMarginTop - shadowMarginBottom
            heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY)
        }
        val child = getChildAt(0)
        if (child.visibility != GONE) {
            measureChildWithMargins(child, widthSpec, 0, heightSpec, 0)
            val lp = child.layoutParams as LayoutParams
            maxWidth = if (shadowMeasureWidthMatchParent) {
                max(maxWidth, child.measuredWidth + lp.leftMargin + lp.rightMargin)
            } else {
                max(maxWidth, child.measuredWidth + shadowMarginLeft + shadowMarginRight + lp.leftMargin + lp.rightMargin)
            }

            maxHeight = if (shadowMeasureHeightMatchParent) {
                max(maxHeight, child.measuredHeight + lp.topMargin + lp.bottomMargin)
            } else {
                max(maxHeight, child.measuredHeight + shadowMarginTop + shadowMarginBottom + lp.topMargin + lp.bottomMargin)
            }
            childState = combineMeasuredStates(childState, child.measuredState)
        }
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        maxHeight = max(maxHeight, suggestedMinimumHeight)
        maxWidth = max(maxWidth, suggestedMinimumWidth)
        val drawable = foreground
        if (drawable != null) {
            maxHeight = max(maxHeight, drawable.minimumHeight)
            maxWidth = max(maxWidth, drawable.minimumWidth)
        }
        setMeasuredDimension(
            resolveSizeAndState(
                maxWidth,
                if (shadowMeasureWidthMatchParent) widthMeasureSpec else widthSpec,
                childState
            ),
            resolveSizeAndState(
                maxHeight,
                if (shadowMeasureHeightMatchParent) heightMeasureSpec else heightSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom)
        if (changed) {
            foregroundDrawBoundsChanged = changed
        }
    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        val parentLeft = getPaddingLeftWithForeground()
        val parentRight = right - left - getPaddingRightWithForeground()
        val parentTop = getPaddingTopWithForeground()
        val parentBottom = bottom - top - getPaddingBottomWithForeground()
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val lp = child.layoutParams as LayoutParams
                val width = child.measuredWidth
                val height = child.measuredHeight
                var gravity = lp.gravity
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY
                }
                val layoutDirection = layoutDirection
                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
                val childLeft =
                    when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                        Gravity.CENTER_HORIZONTAL -> parentLeft + (parentRight - parentLeft - width) / 2 + lp.leftMargin - lp.rightMargin + shadowMarginLeft - shadowMarginRight
                        Gravity.END -> parentRight - width - lp.rightMargin - shadowMarginRight
                        Gravity.START -> parentLeft + lp.leftMargin + shadowMarginLeft
                        else -> parentLeft + lp.leftMargin + shadowMarginLeft
                    }
                val childTop = when (verticalGravity) {
                    Gravity.TOP -> parentTop + lp.topMargin + shadowMarginTop
                    Gravity.CENTER_VERTICAL -> parentTop + (parentBottom - parentTop - height) / 2 + lp.topMargin - lp.bottomMargin + shadowMarginTop - shadowMarginBottom
                    Gravity.BOTTOM -> parentBottom - height - lp.bottomMargin - shadowMarginBottom
                    else -> parentTop + lp.topMargin + shadowMarginTop
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = measuredWidth
        val h = measuredHeight
        val path: Path = roundedRect(
            shadowMarginLeft.toFloat(),
            shadowMarginTop.toFloat(),
            w - shadowMarginRight.toFloat(),
            h - shadowMarginBottom.toFloat(),
            cornerRadiusTL,
            cornerRadiusTR,
            cornerRadiusBR,
            cornerRadiusBL
        )
        canvas.drawPath(path, bgPaint)
        canvas.clipPath(path)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        val w = measuredWidth
        val h = measuredHeight
        val path: Path = roundedRect(
            shadowMarginLeft.toFloat(),
            shadowMarginTop.toFloat(),
            w - shadowMarginRight.toFloat(),
            h - shadowMarginBottom.toFloat(),
            cornerRadiusTL,
            cornerRadiusTR,
            cornerRadiusBR,
            cornerRadiusBL
        )
        canvas.clipPath(path)
        drawForeground(canvas)
        canvas.restore()
    }

    private fun updatePaintShadow() {
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    private fun updatePaintShadow(radius: Float, dx: Float, dy: Float, color: Int) {
        bgPaint.setShadowLayer(radius, dx, dy, color)
        invalidate()
    }

    private fun getShadowMarginMax(): Float {
        var max = 0f
        val margins = Arrays.asList(
            shadowMarginLeft, shadowMarginTop,
            shadowMarginRight, shadowMarginBottom
        )
        for (value in margins) {
            max = max(max, value.toFloat())
        }
        return max
    }

    private fun drawForeground(canvas: Canvas) {
        if (foregroundDrawable != null) {
            if (foregroundDrawBoundsChanged) {
                foregroundDrawBoundsChanged = false
                val w = right - left
                val h = bottom - top
                val foregroundDrawInPadding = true
                if (foregroundDrawInPadding) {
                    selfBounds.set(0, 0, w,h)
                } else {
                    selfBounds.set(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)
                }
                Gravity.apply(foregroundDrawGravity, foregroundDrawable!!.intrinsicWidth, foregroundDrawable!!.intrinsicHeight, selfBounds, overlayBounds)
                foregroundDrawable!!.bounds = overlayBounds
            }
            foregroundDrawable!!.draw(canvas)
        }
    }

    override fun getForeground(): Drawable? {
        return foregroundDrawable
    }

    override fun setForeground(foreground: Drawable) {
        if (foregroundDrawable != null) {
            foregroundDrawable!!.callback = null
            unscheduleDrawable(foregroundDrawable)
        }
        foregroundDrawable = foreground
        updateForegroundColor()
        setWillNotDraw(false)
        foreground.callback = this
        if (foreground.isStateful) {
            foreground.state = drawableState
        }
        if (foregroundDrawGravity == Gravity.FILL) {
            val padding = Rect()
            foreground.getPadding(padding)
        }
        requestLayout()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        foregroundDrawBoundsChanged = true
    }

    override fun getForegroundGravity(): Int {
        return foregroundDrawGravity
    }

    override fun setForegroundGravity(foregroundGravity: Int) {
        var foregroundGravity = foregroundGravity
        if (foregroundDrawGravity != foregroundGravity) {
            if (foregroundGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.START
            }
            if (foregroundGravity and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.TOP
            }
            foregroundDrawGravity = foregroundGravity
            if (foregroundDrawGravity == Gravity.FILL && foregroundDrawable != null) {
                val padding = Rect()
                foregroundDrawable!!.getPadding(padding)
            }
            requestLayout()
        }
    }

    override fun verifyDrawable(@NonNull who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foregroundDrawable
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foregroundDrawable != null) {
            foregroundDrawable!!.jumpToCurrentState()
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foregroundDrawable != null) {
            if (foregroundDrawable!!.isStateful) {
                foregroundDrawable!!.state = drawableState
            }
        }
    }

    private fun updateForegroundColor() {
        if (foregroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (foregroundDrawable is RippleDrawable) {
                    (foregroundDrawable as RippleDrawable).setColor(ColorStateList.valueOf(foregroundColor))
                }
            } else {
                foregroundDrawable!!.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (foregroundDrawable != null) {
                foregroundDrawable!!.setHotspot(x, y)
            }
        }
    }

    /**
     * 获取阴影的颜色
     * @return                          int
     */
    fun getShadowColor(): Int {
        return shadowColor
    }

    /**
     * 设置阴影的颜色
     * @param shadowColor               颜色，这里需要用注解限定一下
     */
    fun setShadowColor(@ColorInt shadowColor: Int) {
        this.shadowColor = shadowColor
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    /**
     * 获取foreground的颜色
     * @return
     */
    fun getForegroundColor(): Int {
        return foregroundColor
    }

    /**
     * 设置foreground颜色
     * @param foregroundColor           颜色
     */
    fun setForegroundColor(@ColorInt foregroundColor: Int) {
        this.foregroundColor = foregroundColor
        updateForegroundColor()
    }

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        invalidate()
    }

    fun getShadowRadius(): Float {
        return if (shadowRadius > getShadowMarginMax() && getShadowMarginMax() != 0f) {
            getShadowMarginMax()
        } else {
            shadowRadius
        }
    }

    fun setShadowRadius(shadowRadius: Float) {
        if (shadowRadius > getShadowMarginMax() && getShadowMarginMax() != 0f) {
            this.shadowRadius = getShadowMarginMax()
        }else{
            this.shadowRadius = shadowRadius
        }
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    fun getShadowDx(): Float {
        return shadowDx
    }

    fun setShadowDx(shadowDx: Float) {
        this.shadowDx = shadowDx
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    fun getShadowDy(): Float {
        return shadowDy
    }

    fun setShadowDy(shadowDy: Float) {
        this.shadowDy = shadowDy
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    fun getShadowMarginTop(): Int {
        return shadowMarginTop
    }

    fun setShadowMarginTop(shadowMarginTop: Int) {
        this.shadowMarginTop = shadowMarginTop
        updatePaintShadow()
    }

    fun getShadowMarginLeft(): Int {
        return shadowMarginLeft
    }

    fun setShadowMarginLeft(shadowMarginLeft: Int) {
        this.shadowMarginLeft = shadowMarginLeft
        updatePaintShadow()
    }

    fun getShadowMarginRight(): Int {
        return shadowMarginRight
    }

    fun setShadowMarginRight(shadowMarginRight: Int) {
        this.shadowMarginRight = shadowMarginRight
        updatePaintShadow()
    }

    fun getShadowMarginBottom(): Int {
        return shadowMarginBottom
    }

    fun setShadowMarginBottom(shadowMarginBottom: Int) {
        this.shadowMarginBottom = shadowMarginBottom
        updatePaintShadow()
    }

    fun setShadowMargin(left: Int, top: Int, right: Int, bottom: Int) {
        shadowMarginLeft = left
        shadowMarginTop = top
        shadowMarginRight = right
        shadowMarginBottom = bottom
        requestLayout()
        invalidate()
    }

    fun getCornerRadiusTL(): Float {
        return cornerRadiusTL
    }

    fun setCornerRadiusTL(cornerRadiusTL: Float) {
        this.cornerRadiusTL = cornerRadiusTL
        invalidate()
    }

    fun getCornerRadiusTR(): Float {
        return cornerRadiusTR
    }

    fun setCornerRadiusTR(cornerRadiusTR: Float) {
        this.cornerRadiusTR = cornerRadiusTR
        invalidate()
    }

    fun getCornerRadiusBL(): Float {
        return cornerRadiusBL
    }

    fun setCornerRadiusBL(cornerRadiusBL: Float) {
        this.cornerRadiusBL = cornerRadiusBL
        invalidate()
    }

    fun getCornerRadiusBR(): Float {
        return cornerRadiusBR
    }

    fun setCornerRadiusBR(cornerRadiusBR: Float) {
        this.cornerRadiusBR = cornerRadiusBR
        invalidate()
    }

    fun setCornerRadius(tl: Float, tr: Float, br: Float, bl: Float) {
        cornerRadiusTL = tl
        cornerRadiusTR = tr
        cornerRadiusBR = br
        cornerRadiusBL = bl
        invalidate()
    }

    private fun getPaddingLeftWithForeground(): Int{
        return paddingLeft
    }

    private fun getPaddingRightWithForeground(): Int {
        return paddingRight
    }

    private fun getPaddingTopWithForeground(): Int {
        return paddingTop
    }

    private fun getPaddingBottomWithForeground(): Int {
        return paddingBottom
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun getAccessibilityClassName(): CharSequence {
        return ShadowView::class.java.name
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    class LayoutParams : MarginLayoutParams {
        companion object {
            const val UNSPECIFIED_GRAVITY = -1
        }
        var gravity = UNSPECIFIED_GRAVITY

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.ShadowView_Layout)
            gravity = a.getInt(R.styleable.ShadowView_Layout_layout_gravity, UNSPECIFIED_GRAVITY)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height) {
            this.gravity = gravity
        }

        constructor(@NonNull source: ViewGroup.LayoutParams?) : super(source) {}
        constructor(@NonNull source: MarginLayoutParams?) : super(source) {}
        constructor(@NonNull source: LayoutParams) : super(source) {
            gravity = source.gravity
        }
    }


    fun roundedRect(left: Float, top: Float, right: Float, bottom: Float, tl: Float, tr: Float, br: Float, bl: Float): Path {
        var tl = tl
        var tr = tr
        var br = br
        var bl = bl
        val path = Path()
        if (tl < 0) {
            tl = 0f
        }
        if (tr < 0) {
            tr = 0f
        }
        if (br < 0) {
            br = 0f
        }
        if (bl < 0) {
            bl = 0f
        }
        val width = right - left
        val height = bottom - top
        val min = min(width, height)
        if (tl > min / 2) {
            tl = min / 2
        }
        if (tr > min / 2) {
            tr = min / 2
        }
        if (br > min / 2) {
            br = min / 2
        }
        if (bl > min / 2) {
            bl = min / 2
        }
        if (tl == tr && tr == br && br == bl && tl == min / 2) {
            val radius = min / 2f
            path.addCircle(left + radius, top + radius, radius, Path.Direction.CW)
            return path
        }
        path.moveTo(right, top + tr)
        if (tr > 0) {
            path.rQuadTo(0f, -tr, -tr, -tr) //top-right corner
        } else {
            path.rLineTo(0f, -tr)
            path.rLineTo(-tr, 0f)
        }
        path.rLineTo(-(width - tr - tl), 0f)
        if (tl > 0) {
            path.rQuadTo(-tl, 0f, -tl, tl) //top-left corner
        } else {
            path.rLineTo(-tl, 0f)
            path.rLineTo(0f, tl)
        }
        path.rLineTo(0f, height - tl - bl)
        if (bl > 0) {
            path.rQuadTo(0f, bl, bl, bl) //bottom-left corner
        } else {
            path.rLineTo(0f, bl)
            path.rLineTo(bl, 0f)
        }
        path.rLineTo(width - bl - br, 0f)
        if (br > 0) {
            path.rQuadTo(br, 0f, br, -br) //bottom-right corner
        } else {
            path.rLineTo(br, 0f)
            path.rLineTo(0f, -br)
        }
        path.rLineTo(0f, -(height - br - tr))
        path.close() //Given close, last line to can be removed.
        return path
    }
}