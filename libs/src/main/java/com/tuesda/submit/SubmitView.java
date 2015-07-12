package com.tuesda.submit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhanglei on 15/7/11.
 */
public class SubmitView extends View {

    private static final int COLOR_BACK = 0xff00cd97;
    private static final int COLOR_GREY = 0xffb9b9b9;

    private int mColor = COLOR_BACK;

    //
    private Paint mBorderPaint;
    private Paint mTextPaint;
    private Paint mBackPaint;

    private int mWidth;
    private int mHeight;
    private int mRadius;
    private static final int PADDING = 5;

    private Rect mTextBounds;

    private boolean mShowText = true;
    private static final String TEXT_SUBMIT = "Submit";
    private String mText = TEXT_SUBMIT;
    private int mStrokeWidth;

    // indicate if view can click
    private boolean mCanClick = true;

    // indicate the animator state
    private AniState mAniState = AniState.INIT;

    enum AniState {
        INIT,
        FIRST_START, // start animation which change backcolor and text color
        FIRST_STOP,
        SECOND_START,  // second animation which change "submit" size for a while
        SECOND_STOP,
        THIRD_START,  // third animation which convert to circle and change back color
        THIRD_STOP,
        FOURTH_START,  // fourth animation which show the progress
        FOURTH_STOP,
        FIFTH_START,   // fifth animation which can narrow the back and border and show "correct" sign
        FIFTH_STOP;


        public boolean isPlaying() {
            return this==FIRST_START
                    || this==SECOND_START
                    || this == THIRD_START
                    || this == FOURTH_START
                    || this == FIFTH_START;
        }

        @Override
        public String toString() {
            switch (this) {
                case INIT:
                    return "***init***";
                case FIRST_START:
                    return "***first start***";
                case FIRST_STOP:
                    return "***first stop***";
                case SECOND_START:
                    return "***second start***";
                case SECOND_STOP:
                    return "***second stop***";
                case THIRD_START:
                    return "***third start***";
                case THIRD_STOP:
                    return "****third stop**";
                case FOURTH_START:
                    return "***fourth start***";
                case FOURTH_STOP:
                    return "***fourth stop***";
                case FIFTH_START:
                    return "***fifth start***";
                case FIFTH_STOP:
                    return "***fifth stop";
                default:
                    return "unknown state";
            }
        }
    }




    public SubmitView(Context context) {
        this(context, null, 0);
    }

    public SubmitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubmitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }


    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(5);
        mBorderPaint.setAntiAlias(true);


        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);


        mTextBounds = new Rect();
        mCanClick = true;





        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanClick) {
                    mCanClick = false;
                    if (mAniState == AniState.INIT) {
                        mAniState = AniState.FIRST_START;
                        firstAniStart();
                        invalidate();
                    }
                }
            }
        });

    }


    private void firstAniStart() {
        mFirstStartT = System.currentTimeMillis();
        mFirstStopT = mFirstStartT + FIRST_DURATION;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            int width = mWidth - (PADDING * 2);
            int height = mHeight - (PADDING * 2);
            mRadius = width/3 > height ? height/2 : width/6;
            mStrokeWidth = mRadius/12;
            mBorderPaint.setStrokeWidth(mStrokeWidth);
            // Log.i("SubmitView", "width=" + mWidth + ", height=" + mHeight + ", radius=" + mRadius + ", strokeWidth=" + mStrokeWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Log.i("submit", "cur state: " + mAniState); // open for debug

        switch (mAniState) {
            case INIT:
                mBorderPaint.setColor(mColor);
                mBackPaint.setColor(mColor);
                mTextPaint.setColor(mColor);
                canvas.drawRoundRect(new RectF(PADDING, mHeight / 2 - mRadius, mWidth - PADDING, mHeight / 2 + mRadius), mRadius, mRadius, mBorderPaint);
                mTextPaint.setTextSize(mRadius/2);
                mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
                canvas.drawText(mText, mWidth / 2 - mTextBounds.width() / 2, mHeight / 2 + mTextBounds.height() / 2, mTextPaint);
                break;
            case FIRST_START:
                canvas.drawRoundRect(new RectF(PADDING, mHeight / 2 - mRadius, mWidth - PADDING, mHeight / 2 + mRadius), mRadius, mRadius, mBorderPaint);
                mBackPaint.setColor(getFirstColor());
                canvas.drawRoundRect(new RectF(PADDING, mHeight / 2 - mRadius, mWidth - PADDING, mHeight / 2 + mRadius),
                        mRadius, mRadius, mBackPaint);
                mTextPaint.setColor(getFirstTextColor());
                canvas.drawText(mText, mWidth/2 - mTextBounds.width()/2, mHeight/2 + mTextBounds.height()/2, mTextPaint);
                break;
            case SECOND_START:
                canvas.drawRoundRect(new RectF(PADDING, mHeight / 2 - mRadius, mWidth - PADDING, mHeight / 2 + mRadius),
                        mRadius, mRadius, mBackPaint);
                mTextPaint.setTextSize(getSecondTextSize());
                mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
                canvas.drawText(mText, mWidth/2 - mTextBounds.width()/2, mHeight/2 + mTextBounds.height()/2, mTextPaint);
                break;
            case THIRD_START:
                int leftRect = mWidth/2 - getHorizonRadius();
                int rightRect = mWidth/2 + getHorizonRadius();
                mBackPaint.setColor(getThirdColor());
                canvas.drawRoundRect(new RectF(leftRect, mHeight / 2 - mRadius, rightRect, mHeight / 2 + mRadius),
                        mRadius, mRadius, mBackPaint);
                mBorderPaint.setColor(getThirdBorderColor());
                canvas.drawRoundRect(new RectF(leftRect, mHeight / 2 - mRadius, rightRect, mHeight / 2 + mRadius), mRadius, mRadius, mBorderPaint);
                break;
            case FOURTH_START:
                // Log.i("submit", "" + progress);
                if (progress>=1) {
                    if (mOnProgressDone!=null) {
                        mOnProgressDone.progressDone();
                    }
                    mAniState = AniState.FOURTH_STOP;
                    mAniState = AniState.FIFTH_START;
                    initFifthAni();
                }
                mBorderPaint.setColor(COLOR_GREY);
                canvas.drawCircle(mWidth/2, mHeight/2, mRadius, mBorderPaint);
                mBorderPaint.setColor(mColor);
                canvas.drawArc(new RectF(mWidth / 2 - mRadius, mHeight / 2 - mRadius, mWidth / 2 + mRadius, mHeight/2 + mRadius),
                        START_DEGREE, 360 * progress, false, mBorderPaint);
                progress += 0.005;  // for debug
                break;
            case FIFTH_START:
                int leftFifthR = mWidth/2 - getFifthHorizonR();
                int rightFifthR = mWidth/2 + getFifthHorizonR();
                canvas.drawRoundRect(new RectF(leftFifthR, mHeight/2-mRadius, rightFifthR, mHeight/2 + mRadius),
                        mRadius, mRadius, mBorderPaint);
                mBackPaint.setColor(getFifthColor());
                canvas.drawRoundRect(new RectF(leftFifthR, mHeight / 2 - mRadius, rightFifthR, mHeight / 2 + mRadius),
                        mRadius, mRadius, mBackPaint);
                drawCorrectSign(canvas, getFifthRatio());
                break;
            case FIFTH_STOP:
                canvas.drawRoundRect(new RectF(PADDING, mHeight/2 - mRadius, mWidth - PADDING, mHeight/2 + mRadius),
                    mRadius, mRadius, mBorderPaint);
                canvas.drawRoundRect(new RectF(PADDING, mHeight/2 - mRadius, mWidth - PADDING, mHeight/2 + mRadius),
                        mRadius, mRadius, mBackPaint);
                drawCorrectSign(canvas, 1);
                break;
        }



        if (mAniState.isPlaying()) {
            invalidate();
        }
    }

    // private int forDeubug = 0; // to count for debug



    // first animation
    private static final int FIRST_DURATION = 500;
    private long mFirstStartT;
    private long mFirstStopT;

    private float getFirstRatio() {
        long now = System.currentTimeMillis();
        if (now >= mFirstStopT) {
            mAniState = AniState.FIRST_STOP;
            mAniState = AniState.SECOND_START;
            initSecAni();
            return 1;
        }
        float ratio = (float)(now - mFirstStartT) / (float) FIRST_DURATION;

        return ratio > 1 ? 1 : ratio;

    }

    private int getFirstColor() {


        return Color.argb(getFirstRatio()==1 ? 0xff : (int)(getFirstRatio() * 0xff), Color.red(mColor), Color.green(mColor), Color.blue(mColor));
    }

    private int getFirstTextColor() {
        float ratio = getFirstRatio();
        if (ratio==1) {
            return 0xffffffff;
        }
        int startRed = Color.red(mColor);
        int red = startRed + (int) ((0xff - startRed) * ratio);

        int startGreen = Color.green(mColor);
        int green = startGreen + (int) ((0xff - startGreen) * ratio);

        int startBlue = Color.blue(mColor);
        int blue = startBlue + (int) ((0xff - startBlue) * ratio);

        return Color.argb(0xff, red, green, blue);
    }

    // second animation which resize the "submit" size
    private static final long SECOND_DURATION = 300;
    private long mSecStartT;
    private long mSecStopT;


    private void initSecAni() {
        mSecStartT = System.currentTimeMillis();
        mSecStopT = mSecStartT + SECOND_DURATION;
    }

    private float getSecondTextSize() {
        long now = System.currentTimeMillis();
        if (now >= mSecStopT) {
            mAniState = AniState.SECOND_STOP;
            mAniState = AniState.THIRD_START;
            initThirdAni();
            return mRadius/2;
        }

        float ratio = (now - (mSecStartT + SECOND_DURATION/2))/(float)(SECOND_DURATION/2);
        return mRadius*3/8 + mRadius/8 * Math.abs(ratio);
    }


    // third animation which change back color and change to a circle
    private long mThirdStartT;
    private long mThirdStopT;
    private long THIRD_DURATION = 400;



    private void initThirdAni() {
        mThirdStartT = System.currentTimeMillis();
        mThirdStopT = mThirdStartT + THIRD_DURATION;
    }

    private float getThirdRatio() {
        long now = System.currentTimeMillis();
        if (now >= mThirdStopT) {
            mAniState = AniState.THIRD_STOP;
            mAniState = AniState.FOURTH_START;
            return 1;
        }

        float ratio = (now - mThirdStartT)/(float)THIRD_DURATION;
        return ratio >= 1 ? 1 : ratio;
    }


    private int getHorizonRadius() {
        float ratio = getThirdRatio();
        int horizonRadius = mRadius + (int) ((1-ratio) * (mWidth/2 - PADDING - mRadius));
        return horizonRadius;
    }

    private int getThirdColor() {
        float ratio = getThirdRatio();
        int alpha = (int) ((1-ratio) * 0xff);
        return Color.argb(alpha, Color.red(mColor), Color.green(mColor), Color.blue(mColor));
    }

    private int getThirdBorderColor() {
        float ratio = getThirdRatio();
        int redStart = Color.red(mColor);
        int greenStart = Color.green(mColor);
        int blueStart = Color.blue(mColor);

        int curRed = redStart + (int) ((Color.red(COLOR_GREY) - redStart) * ratio);
        int curGreen = greenStart + (int) ((Color.green(COLOR_GREY) - greenStart) * ratio);
        int curBlue = blueStart + (int) ((Color.blue(COLOR_GREY) - blueStart) * ratio);

        return Color.argb(0xff, curRed, curGreen, curBlue);
    }

    // used for fourth animation but this not use time to calculate animation
    private static final int START_DEGREE = 270;
    private float progress = 0f;
    private OnProgressDone mOnProgressDone;

    public void setProgress(float progress) {
        this.progress = progress;
    }
    public void setOnProgressDone(OnProgressDone onProgressDone) {
        mOnProgressDone = onProgressDone;
    }
    public boolean isProgressDone() {
        return mAniState==AniState.FOURTH_STOP
                || mAniState == AniState.FIFTH_START
                || mAniState == AniState.FIFTH_STOP;
    }

    public void setText(String str) {
        if (str==null || TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("text can't be null or empty");
        }
        mText = str;
    }

    public void reset() {
        mAniState = AniState.INIT;
        invalidate();
    }

    public void setBackColor(int color) {
        mColor = color;
        invalidate();
    }


    // used for fifth animation by time

    private static final long FIFTH_DURATION = 600;
    private long mFifthStartT;
    private long mFifthStopT;

    private void initFifthAni() {
        mFifthStartT = System.currentTimeMillis();
        mFifthStopT = mFifthStartT + FIFTH_DURATION;

    }

    private float getFifthRatio() {
        long now = System.currentTimeMillis();
        if (now >= mFifthStopT) {
            mAniState = AniState.FIFTH_STOP;
            return 1;
        }
        float ratio = (now - mFifthStartT) / (float) FIFTH_DURATION;
        return ratio >= 1 ? 1 : ratio;
    }

    private int getFifthHorizonR() {
        float ratio = getFifthRatio();
        if (ratio==1) {
            return mWidth/2 - PADDING;
        }
        int horizonR = mRadius + (int) (((mWidth/2 - PADDING) - mRadius) * ratio);
        return horizonR;
    }

    private int getFifthColor() {
        float ratio = getFifthRatio();
        if (ratio==1) {
            return mColor;
        }
        int alpha = (int) (ratio * 0xff);
        return Color.argb(alpha, Color.red(mColor), Color.green(mColor), Color.blue(mColor));

    }

    // drawing "correct" sign
    private void drawCorrectSign(Canvas canvas, float ratio) {

        Paint correctPaint = new Paint();
        correctPaint.setAntiAlias(true);
        correctPaint.setStyle(Paint.Style.STROKE);
        correctPaint.setStrokeWidth(10f);
        correctPaint.setColor(0xffffffff);

        int centerX = mWidth/2;
        int centerY = mHeight/2 + mRadius/4;

        Path path = new Path();
        path.moveTo(centerX - (mRadius*ratio/4), centerY - (mRadius*ratio/4));
        path.lineTo(centerX, centerY);
        path.lineTo(centerX + (mRadius*ratio/2), centerY -(mRadius*ratio/2));

        canvas.drawPath(path, correctPaint);
    }

    public interface OnProgressDone {
        void progressDone();
    }
}
