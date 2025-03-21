package com.example.chair

import android.content.Context
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan.ALIGN_CENTER
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.text.inSpans
import com.example.chair.databinding.LayoutTranslateBinding

class TranslateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val layoutTranslate: LayoutTranslateBinding =
        LayoutTranslateBinding.inflate(LayoutInflater.from(context))
    private val tvTranslate = layoutTranslate.tvTranslate
    private val tvContent = layoutTranslate.tvContent
    private val constraintSet = ConstraintSet()
    private val iconWidth = 16f.dp.toInt()
    private val safeSpace = 12f.dp.toInt()
    private val expandIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_launcher_background)?.apply {
            setBounds(0, 0, safeSpace, safeSpace)
        }

    init {
        addView(layoutTranslate.root)
    }

    fun setupTranslate(data: TranslateData) {
        tvTranslate.text = data.tailContent
        tvContent.post {
            val maxWidth = tvContent.width
            val paint = tvContent.paint
            val staticLayout =
                StaticLayout.Builder.obtain(data.content, 0, data.content.length, paint, maxWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.0f) // 行间距
                    .setIncludePad(false)
                    .build()

            val lineCount = staticLayout.lineCount
            val maxLines = tvContent.maxLines
            val tailWidth = tvTranslate.paint.measureText(data.tailContent)
            val enoughSpace = if (!data.expand && lineCount > maxLines) {
                val lastLineWidth = staticLayout.getLineWidth(maxLines - 1)
                val ellipseWidth = paint.measureText(ELLIPSE)
                val spaceWidth = maxWidth - lastLineWidth - ellipseWidth - iconWidth
                val result = if (spaceWidth > 0) {
                    data.content + ELLIPSE
                } else {
                    val content =
                        data.content.substring(0, staticLayout.getLineEnd(maxLines - 1))
                    content.dropLast(ELLIPSE.length) + ELLIPSE
                }

                val spannableString = SpannableStringBuilder(result)

                expandIcon?.let {
                    spannableString.inSpans(ImageSpan(it, ALIGN_CENTER),object :ClickableSpan(){
                        override fun onClick(widget: View) {
                            tvContent.maxLines = Int.MAX_VALUE
                            setupTranslate(data.copy(expand = true))
                        }

                    }) {
                        append(SPACE.repeat(4))
                    }
                }

                tvContent.movementMethod = LinkMovementMethod.getInstance()
                tvContent.text = spannableString
                spaceWidth > tailWidth + safeSpace
            } else {
                tvContent.maxLines = Int.MAX_VALUE
                tvContent.text = data.content
                val lastLineWidth = staticLayout.getLineWidth(lineCount - 1)
                maxWidth - lastLineWidth > tailWidth + safeSpace
            }


            if (enoughSpace) {
                constraintSet.clone(layoutTranslate.root)
                constraintSet.connect(
                    R.id.tv_translate,
                    ConstraintSet.BOTTOM,
                    R.id.tv_content,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(layoutTranslate.root)
            } else {
                constraintSet.clone(layoutTranslate.root)
                constraintSet.connect(
                    R.id.tv_translate,
                    ConstraintSet.TOP,
                    R.id.tv_content,
                    ConstraintSet.BOTTOM,
                    12f.dp.toInt()
                )
                constraintSet.applyTo(layoutTranslate.root)
            }
        }
    }

    companion object {
        const val ELLIPSE = "... "
        const val SPACE = " "
    }
}