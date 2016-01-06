package clwang.chunyu.me.wcl_like_anim_demo;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * 圆形视图
 * <p>
 * Created by wangchenlong on 16/1/5.
 */
public class CircleView extends View {

    private static final int START_COLOR = 0xFFFF5722;
    private static final int END_COLOR = 0xFFFFC107;

    private ArgbEvaluator mArgbEvaluator; // Argb估计器

    private Paint mCirclePaint; // 圆形视图
    private Paint mMaskPaint; // 掩盖视图

    private Canvas mTempCanvas; // 中间画布
    private Bitmap mTempBitmap; // 中间图画

    private int mMaxCircleSize; // 最大圆环大小

    private float mOuterCircleRadiusProgress; // 外圈圆
    private float mInnerCircleRadiusProgress; // 内圈圆

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // 初始化
    private void init() {
        mCirclePaint = new Paint();
        mMaskPaint = new Paint();
        mArgbEvaluator = new ArgbEvaluator();

        mCirclePaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxCircleSize = w / 2;
        mTempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mTempBitmap);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR); // 清除颜色
        mTempCanvas.drawCircle(getWidth() / 2, getHeight() / 2, mOuterCircleRadiusProgress * mMaxCircleSize, mCirclePaint);
        mTempCanvas.drawCircle(getWidth() / 2, getHeight() / 2, mInnerCircleRadiusProgress * mMaxCircleSize, mMaskPaint);
        canvas.drawBitmap(mTempBitmap, 0, 0, null);
    }

    public float getOuterCircleRadiusProgress() {
        return mOuterCircleRadiusProgress;
    }

    public void setOuterCircleRadiusProgress(float outerCircleRadiusProgress) {
        mOuterCircleRadiusProgress = outerCircleRadiusProgress;
        updateCircleColor();
        postInvalidate(); // 延迟重绘
    }

    // 更新圆圈
    private void updateCircleColor() {
        float colorProgress = (float) Utils.clamp(mOuterCircleRadiusProgress, 0.5, 1); // 最小是0.5

        // 0.5到1颜色渐变
        colorProgress = (float) Utils.mapValueFromRangeToRange(colorProgress, 0.5f, 1f, 0f, 1f);
        mCirclePaint.setColor((Integer) mArgbEvaluator.evaluate(colorProgress, START_COLOR, END_COLOR));
    }

    public float getInnerCircleRadiusProgress() {
        return mInnerCircleRadiusProgress;
    }

    public void setInnerCircleRadiusProgress(float innerCircleRadiusProgress) {
        mInnerCircleRadiusProgress = innerCircleRadiusProgress;
        postInvalidate(); // 延迟重绘
    }

    // 内部圆圈处理
    public static final Property<CircleView, Float> INNER_CIRCLE_RADIUS_PROGRESS =
            new Property<CircleView, Float>(Float.class, "innerCircleRadiusProgress") {
                @Override
                public Float get(CircleView object) {
                    return object.getInnerCircleRadiusProgress();
                }

                @Override
                public void set(CircleView object, Float value) {
                    object.setInnerCircleRadiusProgress(value);
                }
            };

    // 外部圆圈处理
    public static final Property<CircleView, Float> OUTER_CIRCLE_RADIUS_PROGRESS =
            new Property<CircleView, Float>(Float.class, "outerCircleRadiusProgress") {
                @Override
                public Float get(CircleView object) {
                    return object.getOuterCircleRadiusProgress();
                }

                @Override
                public void set(CircleView object, Float value) {
                    object.setOuterCircleRadiusProgress(value);
                }
            };
}
