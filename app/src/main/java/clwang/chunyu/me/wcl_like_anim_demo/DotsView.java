package clwang.chunyu.me.wcl_like_anim_demo;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * 发散的点, 由大点小点组成, 两类点排列和颜色均错开, 速度先慢后快向外发射.
 * <p>
 * Created by wangchenlong on 16/1/6.
 */
public class DotsView extends View {
    private static final int DOTS_COUNT = 7; // 7个点阵
    private static final int OUTER_DOTS_POSITION_ANGLE = 51; // 每个原点51度

    private static final int COLOR_1 = 0xFFFFC107;
    private static final int COLOR_2 = 0xFFFF9800;
    private static final int COLOR_3 = 0xFFFF5722;
    private static final int COLOR_4 = 0xFFF44336;

    private final Paint[] mCirclePaints = new Paint[4]; // 4种类型的圆圈

    // 图像的中心位置
    private int mCenterX;
    private int mCenterY;

    private float mMaxOuterDotsRadius; // 最大外圈的半径
    private float mMaxInnerDotsRadius; // 最大内圈的半径
    private float mMaxDotSize; // 圆圈的最大尺寸

    private float mCurrentProgress = 0; // 当前进度, 核心参数

    private float mCurrentRadius1 = 0; // 外圈点的半径
    private float mCurrentDotSize1 = 0; // 外圈点的大小

    private float mCurrentDotSize2 = 0; // 内圈点的半径
    private float mCurrentRadius2 = 0; // 内圈点的大小

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    public DotsView(Context context) {
        super(context);
        init();
    }

    public DotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // 填充圆圈
        for (int i = 0; i < mCirclePaints.length; i++) {
            mCirclePaints[i] = new Paint();
            mCirclePaints[i].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mMaxDotSize = 20; // 点的大小
        mMaxOuterDotsRadius = w / 2 - mMaxDotSize * 2; // 最大外圈
        mMaxInnerDotsRadius = 0.8f * mMaxOuterDotsRadius; // 最大内圈
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOuterDotsFrame(canvas);
        drawInnerDotsFrame(canvas);
    }

    // 外圈点, 画若干点, 使用不同颜色, 中心位置CurrentRadius和点大小CurrentDotSize是变量.
    private void drawOuterDotsFrame(Canvas canvas) {
        for (int i = 0; i < DOTS_COUNT; i++) {
            int cX = (int) (mCenterX + mCurrentRadius1 * Math.cos(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180));
            int cY = (int) (mCenterY + mCurrentRadius1 * Math.sin(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180));
            canvas.drawCircle(cX, cY, mCurrentDotSize1, mCirclePaints[i % mCirclePaints.length]);
        }
    }

    // 内圈点, 与外圈点错开10, 颜色也与外圈点开, 中心位置CurrentRadius和点大小CurrentDotSize是变量.
    private void drawInnerDotsFrame(Canvas canvas) {
        for (int i = 0; i < DOTS_COUNT; i++) {
            int cX = (int) (mCenterX + mCurrentRadius2 * Math.cos((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180));
            int cY = (int) (mCenterY + mCurrentRadius2 * Math.sin((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180));

            // i+1确保颜色不同
            canvas.drawCircle(cX, cY, mCurrentDotSize2, mCirclePaints[(i + 1) % mCirclePaints.length]);
        }
    }

    // 设置当前进度, 会更新大小和轨迹
    public void setCurrentProgress(float currentProgress) {
        mCurrentProgress = currentProgress;

        // 更新位置
        updateInnerDotsPosition();
        updateOuterDotsPosition();

        updateDotsPaints(); // 更新颜色
        updateDotsAlpha(); // 更新透明度

        postInvalidate(); // 每次设置都会延迟重绘
    }

    public float getCurrentProgress() {
        return mCurrentProgress;
    }

    // 更新内部点
    private void updateInnerDotsPosition() {
        // 0.3以上不动
        if (mCurrentProgress < 0.3f) {
            this.mCurrentRadius2 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0, 0.3f, 0.f, mMaxInnerDotsRadius);
        } else {
            this.mCurrentRadius2 = mMaxInnerDotsRadius;
        }

        // 点的缩小速度
        if (mCurrentProgress < 0.2) {
            this.mCurrentDotSize2 = mMaxDotSize;
        } else if (mCurrentProgress < 0.5) {
            this.mCurrentDotSize2 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.2f, 0.5f, mMaxDotSize, 0.3 * mMaxDotSize);
        } else {
            this.mCurrentDotSize2 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.5f, 1f, mMaxDotSize * 0.3f, 0);
        }

    }

    // 变换外层点的位置
    private void updateOuterDotsPosition() {
        // 半径先走的快, 后走的慢
        if (mCurrentProgress < 0.3f) {
            mCurrentRadius1 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.0f, 0.3f, 0, mMaxOuterDotsRadius * 0.8f);
        } else {
            mCurrentRadius1 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.3f, 1f, 0.8f * mMaxOuterDotsRadius, mMaxOuterDotsRadius);
        }

        // 点的大小, 小于0.7是最大点, 大于0.7逐渐为0.
        if (mCurrentProgress < 0.7f) {
            mCurrentDotSize1 = mMaxDotSize;
        } else {
            mCurrentDotSize1 = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.7f, 1f, mMaxDotSize, 0);
        }
    }

    // 变化颜色
    private void updateDotsPaints() {
        if (mCurrentProgress < 0.5f) {
            float progress = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0f, 0.5f, 0, 1f);
            mCirclePaints[0].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
            mCirclePaints[1].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
            mCirclePaints[2].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_3, COLOR_4));
            mCirclePaints[3].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_4, COLOR_1));
        } else {
            float progress = (float) Utils.mapValueFromRangeToRange(mCurrentProgress, 0.5f, 1f, 0, 1f);
            mCirclePaints[0].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
            mCirclePaints[1].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_3, COLOR_4));
            mCirclePaints[2].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_4, COLOR_1));
            mCirclePaints[3].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
        }
    }

    // 变化透明度
    private void updateDotsAlpha() {
        float progress = (float) Utils.clamp(mCurrentProgress, 0.6f, 1f); // 最小0.6, 最大1
        int alpha = (int) Utils.mapValueFromRangeToRange(progress, 0.6f, 1f, 255, 0); // 直至消失
        mCirclePaints[0].setAlpha(alpha);
        mCirclePaints[1].setAlpha(alpha);
        mCirclePaints[2].setAlpha(alpha);
        mCirclePaints[3].setAlpha(alpha);
    }

    public static final Property<DotsView, Float> DOTS_PROGRESS = new Property<DotsView, Float>(Float.class, "dotsProgress") {
        @Override
        public Float get(DotsView object) {
            return object.getCurrentProgress();
        }

        @Override
        public void set(DotsView object, Float value) {
            object.setCurrentProgress(value);
        }
    };
}
