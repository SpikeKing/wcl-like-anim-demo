package clwang.chunyu.me.wcl_like_anim_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 喜欢按钮
 * <p>
 * Created by wangchenlong on 16/1/5.
 */
public class LikeButtonView extends FrameLayout {

    @Bind(R.id.like_button_cv_circle) CircleView mCvCircle; // 圆形
    @Bind(R.id.like_button_iv_star) ImageView mIvStar; // 星星
    @Bind(R.id.like_button_dv_dots) DotsView mDvDots;

    private DecelerateInterpolator mDecelerate; // 减速差值
    private OvershootInterpolator mOvershoot; // 超出差值
    private AccelerateDecelerateInterpolator mAccelerateDecelerate; // 加速度减速差值
    private AnimatorSet mAnimatorSet; // 动画集合

    private boolean mIsChecked; // 点击状态

    public LikeButtonView(Context context) {
        super(context);
        if (!isInEditMode())
            init();
    }

    public LikeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init();
    }

    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode())
            init();
    }

    // 初始化视图
    private void init() {
        isInEditMode();

        LayoutInflater.from(getContext()).inflate(R.layout.view_like_button, this, true);
        ButterKnife.bind(this);

        mDecelerate = new DecelerateInterpolator();
        mOvershoot = new OvershootInterpolator(4);
        mAccelerateDecelerate = new AccelerateDecelerateInterpolator();

        setOnClickListener(this::clickView);
    }

    // 点击视图
    private void clickView(View view) {
        mIsChecked = !mIsChecked;
        mIvStar.setImageResource(mIsChecked ? R.drawable.ic_star_rate_on : R.drawable.ic_star_rate_off);

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        if (mIsChecked) {
            mIvStar.animate().cancel();
            mIvStar.setScaleX(0);
            mIvStar.setScaleY(0);
            mCvCircle.setInnerCircleRadiusProgress(0);
            mCvCircle.setOuterCircleRadiusProgress(0);
            mDvDots.setCurrentProgress(0);

            mAnimatorSet = new AnimatorSet();

            ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(mCvCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            outerCircleAnimator.setDuration(250);
            outerCircleAnimator.setInterpolator(mDecelerate);

            ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(mCvCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            innerCircleAnimator.setDuration(200);
            innerCircleAnimator.setStartDelay(200);
            innerCircleAnimator.setInterpolator(mDecelerate);

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(mIvStar, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(mOvershoot);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(mIvStar, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(mOvershoot);

            ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(mDvDots, DotsView.DOTS_PROGRESS, 0, 1f);
            dotsAnimator.setDuration(900);
            dotsAnimator.setStartDelay(50);
            dotsAnimator.setInterpolator(mAccelerateDecelerate);

            mAnimatorSet.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            );


            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    mIvStar.setScaleX(1);
                    mIvStar.setScaleY(1);
                    mCvCircle.setInnerCircleRadiusProgress(0);
                    mCvCircle.setOuterCircleRadiusProgress(0);
                    mDvDots.setCurrentProgress(0);
                }
            });

            mAnimatorSet.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIvStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(mDecelerate);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                mIvStar.animate().scaleX(1).scaleY(1).setInterpolator(mDecelerate);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }
}
