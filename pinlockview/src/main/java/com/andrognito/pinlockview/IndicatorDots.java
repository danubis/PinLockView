package com.andrognito.pinlockview;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * It represents a set of indicator dots which when attached with {@link PinLockView}
 * can be used to indicate the current length of the input
 * <p>
 * Created by aritraroy on 01/06/16.
 */
public class IndicatorDots extends LinearLayout {

    @IntDef({IndicatorType.FIXED, IndicatorType.FILL, IndicatorType.FILL_WITH_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorType {
        int FIXED = 0;
        int FILL = 1;
        int FILL_WITH_ANIMATION = 2;
    }

    private static final int DEFAULT_PIN_LENGTH = 4;

    private int mDotDiameter;
    private int mDotSpacing;
    private int mPinLength = DEFAULT_PIN_LENGTH;
    private int mIndicatorType;
    private int mPrimaryColor;
    private GradientDrawable mFillDrawable, mEmptyDrawable;

    private int mPreviousLength;

    public IndicatorDots(Context context) {
        this(context, null);
    }

    public IndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorDots);

        try {
            mDotDiameter = (int) typedArray.getDimension(R.styleable.IndicatorDots_dotDiameter, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_diameter));
            mDotSpacing = (int) typedArray.getDimension(R.styleable.IndicatorDots_dotSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_spacing));
            int mFillDrawableRes = typedArray.getResourceId(R.styleable.IndicatorDots_dotFilledBackground,
                    R.drawable.dot_filled);
            int mEmptyDrawableRes = typedArray.getResourceId(R.styleable.IndicatorDots_dotEmptyBackground,
                    R.drawable.dot_empty);
            mFillDrawable = (GradientDrawable) ContextCompat.getDrawable(context, mFillDrawableRes);
            mEmptyDrawable = (GradientDrawable) ContextCompat.getDrawable(context, mEmptyDrawableRes);
//            mPinLength = typedArray.getInt(R.styleable.IndicatorDots_pinLength, DEFAULT_PIN_LENGTH);
            mIndicatorType = typedArray.getInt(R.styleable.IndicatorDots_indicatorType,
                    IndicatorType.FIXED);
            mPrimaryColor = ResourceUtils.getColor(getContext(), R.color.white);
        } finally {
            typedArray.recycle();
        }

        initView(context);
    }

    private void initView(Context context) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        if (mIndicatorType == 0) {
            for (int i = 0; i < mPinLength; i++) {
                ImageView dot = new ImageView(context);
                emptyDot(dot);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotDiameter,
                        mDotDiameter);
                params.setMargins(mDotSpacing, 0, mDotSpacing, 0);
                dot.setLayoutParams(params);

                addView(dot);
            }
        } else if (mIndicatorType == 2) {
            setLayoutTransition(new LayoutTransition());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // If the indicator type is not fixed
        if (mIndicatorType != 0) {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.height = mDotDiameter;
            requestLayout();
        }
    }

    void updateDot(int length) {
        if (mIndicatorType == 0) {
            if (length > 0) {
                if (length > mPreviousLength) {
                    fillDot((ImageView) getChildAt(length - 1));
                } else {
                    emptyDot((ImageView) getChildAt(length));
                }
                mPreviousLength = length;
            } else {
                // When {@code mPinLength} is 0, we need to reset all the views back to empty
                for (int i = 0; i < getChildCount(); i++) {
                    ImageView v = (ImageView) getChildAt(i);
                    emptyDot(v);
                }
                mPreviousLength = 0;
            }
        } else {
            if (length > 0) {
                if (length > mPreviousLength) {
                    ImageView dot = new ImageView(getContext());
                    fillDot(dot);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotDiameter,
                            mDotDiameter);
                    params.setMargins(mDotSpacing, 0, mDotSpacing, 0);
                    dot.setLayoutParams(params);

                    addView(dot, length - 1);
                } else {
                    removeViewAt(length);
                }
                mPreviousLength = length;
            } else {
                removeAllViews();
                mPreviousLength = 0;
            }
        }
    }

    private void emptyDot(ImageView dot) {
        dot.setImageDrawable(mEmptyDrawable);
    }

    private void fillDot(ImageView dot) {
        dot.setImageDrawable(mFillDrawable);
    }

    public int getPinLength() {
        return mPinLength;
    }

    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;
        removeAllViews();
        initView(getContext());
    }

    public
    @IndicatorType
    int getIndicatorType() {
        return mIndicatorType;
    }

    public void setIndicatorType(@IndicatorType int type) {
        this.mIndicatorType = type;
        removeAllViews();
        initView(getContext());
    }

    public void setPrimaryColor(int primaryColor) {
        mPrimaryColor = primaryColor;
        mEmptyDrawable.setStroke(3, primaryColor);
        mFillDrawable.setColor(primaryColor);

        removeAllViews();
        initView(getContext());
    }
}
