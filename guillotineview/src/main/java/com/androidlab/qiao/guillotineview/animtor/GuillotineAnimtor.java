package com.androidlab.qiao.guillotineview.animtor;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;

import com.androidlab.qiao.guillotineview.util.ActionBarInterpolator;
import com.androidlab.qiao.guillotineview.util.GuillotineInterpolator;

/**
 * GuillotineAnimtor
 *
 * @author: 乔云瑞
 * @time: 2016/1/27 12:49
 */
public class GuillotineAnimtor {

    private static final String ROTATION = "rotation";
    private static final float GUILLOTINE_CLOSED_ANGLE = -90f;  //铡刀关闭时的角度
    private static final float GUILLOTINE_OPENED_ANGLE = 0f;    //铡刀开启时的角度
    private static final int DEFAULT_DURATION = 625;
    private static final float ACTION_BAR_ROTATION_ANGLE = 3f;  //actionBar的角旋转角度，只需要轻微旋转

    private View mGuillotineView;   //铡刀视图
    private View mActionBar;    //ActionBar,不在铡刀视图上
    private View mOpenButton;    //开启按钮，不在铡刀视图上
    private View mCloseButton;   //关闭动画，在铡刀视图上

    private long mDuration;   //动画持续时间
    private long mDelayTime;    //动画延迟时间
    private ObjectAnimator mOpeningAnimation;   //铡刀开启动画
    private ObjectAnimator mClosingAnimation;   //铡刀关闭动画

    private boolean isOpening;  //是否正在开启
    private boolean isClosing;  //是否正在关闭

    private boolean isOpenOnStart = false; //是否开启铡刀，默认不开启

    public GuillotineAnimtor(Builder builder) {

        this.mGuillotineView = builder.guillotineView;
        this.mActionBar = builder.actionbar;
        this.mOpenButton = builder.openButton;
        this.mCloseButton = builder.closeButton;

        this.mDelayTime = builder.delayTime > 0 ? builder.delayTime : 0;
        this.mDuration = builder.duration > 0 ? builder.duration : DEFAULT_DURATION;

        setUpOpengingView();
        setUpClosingView();
        this.mOpeningAnimation = buildOpeningAnimation();
        this.mClosingAnimation = buildClosingAnimation();
    }

    /**
     * 创建打开动画
     *
     * @return
     */
    private ObjectAnimator buildOpeningAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mGuillotineView, ROTATION, GUILLOTINE_CLOSED_ANGLE, GUILLOTINE_OPENED_ANGLE);
        animator.setDuration(mDuration);
        animator.setStartDelay(mDelayTime);
        animator.setInterpolator(new GuillotineInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mGuillotineView.setVisibility(View.VISIBLE);    //设置铡刀可见
                isOpening = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOpening = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }

    private ObjectAnimator buildClosingAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mGuillotineView, ROTATION, GUILLOTINE_OPENED_ANGLE, GUILLOTINE_CLOSED_ANGLE);
        animator.setDuration((long) (mDuration * GuillotineInterpolator.ROTATION_TIME));
        animator.setStartDelay(mDelayTime);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isClosing = true;
                mGuillotineView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isClosing = false;
                mGuillotineView.setVisibility(View.INVISIBLE);
                startActionBarAnimation();  //播放actionBar动画
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }

    /**
     * 播放actionBar动画
     */
    private void startActionBarAnimation() {
        ObjectAnimator actionBarAnimatior = ObjectAnimator.ofFloat(mActionBar, ROTATION, GUILLOTINE_OPENED_ANGLE, ACTION_BAR_ROTATION_ANGLE);
        actionBarAnimatior.setInterpolator(new ActionBarInterpolator());
        actionBarAnimatior.start();
    }

    /**
     * 设置开启铡刀支点以及其他
     */
    private void setUpOpengingView() {
        if (mGuillotineView != null) {
            mGuillotineView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mGuillotineView.getViewTreeObserver().removeOnGlobalLayoutListener(this);   //移除监听器
                    mGuillotineView.setPivotX(calculatePivotX(mOpenButton));   //为铡刀视图设置支点
                    mGuillotineView.setPivotY(calculatePivotY(mOpenButton));
                }
            });
        }
        //为开启按钮设置监听事件
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
    }

    /**
     * 设置关闭铡刀支点及其他
     */
    private void setUpClosingView() {
        if (mGuillotineView != null) {
            mGuillotineView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mGuillotineView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mGuillotineView.setPivotX(calculatePivotX(mCloseButton));
                    mGuillotineView.setPivotY(calculatePivotY(mCloseButton));
                    mActionBar.setPivotX(calculatePivotX(mCloseButton));
                    mActionBar.setPivotY(calculatePivotY(mCloseButton));
                }
            });
        }
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }


    /**
     * 计算视图的中心位置X
     *
     * @param burger
     * @return
     */
    private float calculatePivotX(View burger) {
        return burger.getLeft() + burger.getWidth() / 2;
    }

    /**
     * 计算视图的中心位置Y
     *
     * @param burger
     * @return
     */
    private float calculatePivotY(View burger) {
        return burger.getTop() + burger.getHeight() / 2;
    }

    /**
     * 播放开启动画
     */
    public void open() {
        if (!isOpening) {
            mOpeningAnimation.start();
        }
    }

    /**
     * 播放关闭动画
     */
    public void close() {
        if (!isClosing) {
            mClosingAnimation.start();
        }
    }

    public static class Builder {

        private View guillotineView;
        private View actionbar;
        private View openButton;
        private View closeButton;

        private long duration;
        private long delayTime;

        public void Builder() {

        }

        public Builder setGuillotineView(View guillotineView) {
            this.guillotineView = guillotineView;
            return this;
        }

        public Builder setActionbar(View actionbar) {
            this.actionbar = actionbar;
            return this;
        }

        public Builder setOpenButton(View openButton) {
            this.openButton = openButton;
            return this;
        }

        public Builder setCloseButton(View closeButton) {
            this.closeButton = closeButton;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setDelayTime(long delayTime) {
            this.delayTime = delayTime;
            return this;
        }

        public GuillotineAnimtor build() {
            return new GuillotineAnimtor(this);
        }

    }

}
