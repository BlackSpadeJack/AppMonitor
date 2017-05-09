package com.app.monitor.core;

import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.monitor.boommenu.Bar;
import com.app.monitor.boommenu.CircleButton;
import com.app.monitor.boommenu.Dot;
import com.app.monitor.boommenu.EndLocationsFactory;
import com.app.monitor.boommenu.HamButton;
import com.app.monitor.boommenu.InterpolatorFactory;
import com.app.monitor.boommenu.PlaceParamsFactory;
import com.app.monitor.boommenu.ShareLines;
import com.app.monitor.boommenu.Util;
import com.app.monitor.boommenu.Eases.EaseType;
import com.app.monitor.boommenu.Types.BoomType;
import com.app.monitor.boommenu.Types.ButtonType;
import com.app.monitor.boommenu.Types.ClickEffectType;
import com.app.monitor.boommenu.Types.DimType;
import com.app.monitor.boommenu.Types.OrderType;
import com.app.monitor.boommenu.Types.PlaceType;
import com.app.monitor.boommenu.Types.StateType;

@SuppressLint("NewApi")
public class PopupAnimationMenu implements CircleButton.OnCircleButtonClickListener {

    // This param is used to optimizize the memory used.
    // When this param is set to true,
    // all the sub buttons will be created when needed
    // and will not be stored.
    public static final boolean MEMORY_OPTIMIZATION = true;

    public static final int MIN_CIRCLE_BUTTON_NUMBER = 1;
    public static final int MAX_CIRCLE_BUTTON_NUMBER = 9;
    public static final int MIN_HAM_BUTTON_NUMBER = 1;
    public static final int MAX_HAM_BUTTON_NUMBER = 4;

    private ViewGroup animationLayout = null;

    // private ShadowLayout shadowLayout;
    // private FrameLayout frameLayout;
    // private View ripple;

    private int[][] originalLocations = new int[MAX_CIRCLE_BUTTON_NUMBER][2];
    private int[][] startLocations = new int[MAX_CIRCLE_BUTTON_NUMBER][2];
    private int[][] endLocations = new int[MAX_CIRCLE_BUTTON_NUMBER][2];

    private boolean animationPlaying = false;
    private StateType state = StateType.CLOSED;

    // Params about buttons
    private int buttonNum = 0;
    // this Array reference cannot never be null as initialized
    private CircleButton[] circleButtons = new CircleButton[MAX_CIRCLE_BUTTON_NUMBER];
    private Dot[] dots = new Dot[MAX_CIRCLE_BUTTON_NUMBER];
    private Bar[] bars = new Bar[MAX_HAM_BUTTON_NUMBER];
    private ShareLines shareLines = null;

    // Store the drawables of buttons
    private Drawable[] drawables = null;
    // Store the colors of buttons
    private int[][] colors = null;
    // Store the strings of buttons
    private String[] strings = null;

    // Is in action bar
    private boolean isInActionBar = false;
    // Is in list item
    private boolean isInList = false;
    // Boom button color
    private int boomButtonColor = 0;
    // Boom button pressed color
    private int boomButtonPressedColor = 0;

    // Frames of animations
    private int frames = 80;
    // Duration of animations
    private int duration = 800;
    // Delay
    private int delay = 100;
    // Show order type
    private OrderType showOrderType = OrderType.DEFAULT;
    // Hide order type
    private OrderType hideOrderType = OrderType.DEFAULT;
    // Button type
    private ButtonType buttonType = ButtonType.CIRCLE;
    // Boom type
    private BoomType boomType = BoomType.HORIZONTAL_THROW;
    // Place type
    private PlaceType placeType = PlaceType.CIRCLE_9_1;// CIRCLE_4_2
    // Default dot width
    private int dotWidth = (int) Util.getInstance().dp2px(8);
    // Default dot width
    private int dotHeight = (int) Util.getInstance().dp2px(8);
    // Default circle button width
    private int buttonWidth = (int) Util.getInstance().dp2px(88);
    // Default bar width
    private int barWidth = (int) Util.getInstance().dp2px(36);
    // Default bar height
    private int barHeight = (int) Util.getInstance().dp2px(6);
    // Default ham button width
    private int hamButtonWidth = 0;
    // Default ham button height
    private int hamButtonHeight = (int) Util.getInstance().dp2px(80);
    // Boom button radius
    private int boomButtonRadius = (int) Util.getInstance().dp2px(56);
    // Movement ease
    private EaseType showMoveEaseType = EaseType.EaseOutBack;
    private EaseType hideMoveEaseType = EaseType.EaseOutCirc;
    // Scale ease
    private EaseType showScaleEaseType = EaseType.EaseOutBack;
    private EaseType hideScaleEaseType = EaseType.EaseOutCirc;
    // Whether rotate
    private int rotateDegree = 720;
    // Rotate ease
    private EaseType showRotateEaseType = EaseType.EaseOutBack;
    private EaseType hideRotateEaseType = EaseType.Linear;
    // Auto dismiss
    private boolean autoDismiss = true;
    // Cancelable
    private boolean cancelable = true;
    // Dim value
    private DimType dimType = DimType.DIM_6;
    // Click effect
    private ClickEffectType clickEffectType = ClickEffectType.RIPPLE;
    // Sub buttons offsets of shadow
    private float subButtonsXOffsetOfShadow = 0;
    private float subButtonsYOffsetOfShadow = 0;
    private int subButtonTextColor = Color.WHITE;
    private ImageView.ScaleType subButtonImageScaleType = ImageView.ScaleType.CENTER;

    private OnClickListener onClickListener = null;
    // private AnimatorListener animatorListener = null;
    private OnSubButtonClickListener onSubButtonClickListener = null;

    private Context mContext;



    public PopupAnimationMenu(Context context) {
        mContext = context;
        this.windowManager =
                (WindowManager) context.getApplicationContext().getSystemService("window");
    }


    public void init(Drawable[] drawables, String[] strings, int[][] colors) {

        if (buttonType.equals(ButtonType.CIRCLE)) {
            // circle buttons
            // create buttons
            buttonNum = drawables.length;

            if (isInList || MEMORY_OPTIMIZATION) {
                // store the drawables, THEN we will build the buttons when create them
                this.drawables = drawables;
                this.colors = colors;
                this.strings = strings;
            } else {
                for (int i = 0; i < buttonNum; i++) {
                    circleButtons[i] = new CircleButton(mContext);
                    circleButtons[i].setOnCircleButtonClickListener(this, i);
                    circleButtons[i].setDrawable(drawables[i]);
                    if (strings != null) circleButtons[i].setText(strings[i]);
                    circleButtons[i].setColor(colors[i][0], colors[i][1]);
                }
            }

            // create dots
            for (int i = 0; i < buttonNum; i++) {
                dots[i] = new Dot(mContext);
                dots[i].setColor(colors[i][1]);
            }

            // place dots according to the number of them and the place type
            placeDots();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void placeDots() {
        createAnimLayout();
        animationLayout.removeAllViews();
        FrameLayout.LayoutParams[] ps =
                PlaceParamsFactory.getDotParams(placeType, animationLayout.getWidth(),
                        animationLayout.getHeight(), dotWidth, dotHeight);


        if (placeType.SHARE_3_1.v <= placeType.v && placeType.v <= PlaceType.SHARE_9_2.v) {
            shareLines = new ShareLines(mContext);
            float[][] locations = new float[3][2];
            locations[0][0] = ps[0].leftMargin + dotWidth / 2;
            locations[0][1] = ps[0].topMargin + dotHeight / 2;
            locations[1][0] = ps[1].leftMargin + dotWidth / 2;
            locations[1][1] = ps[1].topMargin + dotHeight / 2;
            locations[2][0] = ps[2].leftMargin + dotWidth / 2;
            locations[2][1] = ps[2].topMargin + dotHeight / 2;
            shareLines.setLocations(locations);
            shareLines.setOffset(1);

            // FrameLayout.LayoutParams p
            // = new FrameLayout.LayoutParams(frameLayout.getWidth(), frameLayout.getHeight());
            // frameLayout.addView(shareLines, p);
        }

        for (int i = 0; i < ps.length; i++) {
            animationLayout.addView(dots[i], ps[i]);
        }
    }

    /**
     * When the boom menu button is clicked.
     */
    public void shoot() {
        // if (buttonType.equals(ButtonType.CIRCLE)) {
        for (int i = 0; i < buttonNum; i++) {
            circleButtons[i] = new CircleButton(mContext);
            circleButtons[i].setOnCircleButtonClickListener(this, i);
            circleButtons[i].setDrawable(drawables[i]);
            if (strings != null) circleButtons[i].setText(strings[i]);
            circleButtons[i].setColor(colors[i][0], colors[i][1]);
            circleButtons[i].setShadowDx(subButtonsXOffsetOfShadow);
            circleButtons[i].setShadowDy(subButtonsYOffsetOfShadow);
            circleButtons[i].getTextView().setTextColor(subButtonTextColor);
            // TODO to find a way to apply multiple colors if set on
            // setTextViewColor(int[] colors)
            circleButtons[i].getImageView().setScaleType(subButtonImageScaleType);
            // circleButtons[i].setRipple(clickEffectType);
        }
        // }

        setRipple(clickEffectType);

        // listener
        // if (onClickListener != null) onClickListener.onClick();
        // wait for the before animations finished
        if (animationPlaying) return;
        animationPlaying = true;
        // dim the animation layout
        dimAnimationLayout();
        // start all animations
        startShowAnimations();
    }

    private void setRipple(ClickEffectType clickEffectType) {
        this.clickEffectType = clickEffectType;

    }

    private ViewGroup createAnimationLayout() {
        // ViewGroup rootView = (ViewGroup)
        // scanForActivity(mContext).getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(mContext);
        // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        // LinearLayout.LayoutParams.MATCH_PARENT,
        // LinearLayout.LayoutParams.MATCH_PARENT);
        // animLayout.setLayoutParams(layoutParams);
        animLayout.setBackgroundResource(android.R.color.transparent);
        // animLayout.setBackgroundColor(0xffcccccc);
        // rootView.addView(animLayout);

        // TextView tv = new TextView(mContext);
        // tv.setText("动画菜单");
        // tv.setTextColor(0xff888888);
        //
        // animLayout.addView(tv);

        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.format = 1;

        windowManager.addView(animLayout, wmParams);

        return animLayout;
    }

    private WindowManager windowManager = null;


    public void hide() {
        if (windowManager != null) {
            windowManager.removeView(animationLayout);
            // animationLayout = null;
            windowManager = null;
            mContext = null;
        }
    }


    public void createAnimLayout() {
        if (animationLayout == null) {
            animationLayout = createAnimationLayout();
            animationLayout.setVisibility(View.GONE);
            animationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (animationPlaying) return;
                    if (cancelable) {
                        startHideAnimations();

                    }
                }
            });
        }
    }

    /**
     * Dim the background layout.
     */
    private void dimAnimationLayout() {
        createAnimLayout();

        animationLayout.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofInt(animationLayout, "backgroundColor", DimType.DIM_0.value,
                        dimType.value).setDuration(duration + delay * (buttonNum - 1));
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // if (animatorListener != null)
                // animatorListener.toShow();
                state = StateType.OPENING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // if (animatorListener != null)
                // animatorListener.showed();
                state = StateType.OPEN;
            }
        });
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // if (animatorListener != null)
                // animatorListener.showing(animation
                // .getAnimatedFraction());
            }
        });
        objectAnimator.start();

        // share lines animation
        if (placeType.SHARE_3_1.v <= placeType.v && placeType.v <= PlaceType.SHARE_9_2.v) {
            ObjectAnimator shareLinesAnimator =
                    ObjectAnimator.ofFloat(shareLines, "offset", 1f, 0f).setDuration(
                            duration + delay * (buttonNum - 1));
            shareLinesAnimator.setStartDelay(0);
            shareLinesAnimator.start();
        }
    }



    /**
     * Get end location of all sub buttons.
     */
    private void getEndLocations() {
        int width = windowManager.getDefaultDisplay().getWidth();// Util.getInstance().getScreenWidth(mContext);
        int height = windowManager.getDefaultDisplay().getHeight();// Util.getInstance().getScreenHeight(mContext);
        if (buttonType.equals(ButtonType.CIRCLE)) {
            endLocations =
                    EndLocationsFactory.getEndLocations(placeType, width, height, buttonWidth,
                            buttonWidth);
        } else if (buttonType.equals(ButtonType.HAM)) {
            endLocations =
                    EndLocationsFactory.getEndLocations(placeType, width, height, hamButtonWidth,
                            hamButtonHeight);
        }
    }

    int mPositon[] = null;

    public void setPositon(int positon[]) {
        mPositon = positon;
    }


    /**
     * Start all animations about showing the boom menu button.
     */
    private void startShowAnimations() {
        if (animationLayout != null) animationLayout.removeAllViews();
        if (buttonType.equals(ButtonType.CIRCLE)) {
            getEndLocations();
            if (showOrderType.equals(OrderType.DEFAULT)) {
                for (int i = 0; i < buttonNum; i++) {
                    dots[i].getLocationOnScreen(startLocations[i]);
                    originalLocations[i] = startLocations[i];

                    startLocations[i][0] = mPositon[0];// (buttonWidth - dots[i].getWidth()) / 2;
                    startLocations[i][1] = mPositon[1];// (buttonWidth - dots[i].getHeight()) / 2;

                    setShowAnimation(dots[i], circleButtons[i], originalLocations[i],
                            startLocations[i], endLocations[i], i);
                }
            } else if (showOrderType.equals(OrderType.REVERSE)) {
                for (int i = 0; i < buttonNum; i++) {
                    dots[i].getLocationOnScreen(startLocations[i]);
                    startLocations[i][0] -= (buttonWidth - dots[i].getWidth()) / 2;
                    startLocations[i][1] -= (buttonWidth - dots[i].getHeight()) / 2;
                    setShowAnimation(dots[i], circleButtons[i], originalLocations[i],
                            startLocations[i], endLocations[i], buttonNum - i - 1);
                }
            } else if (showOrderType.equals(OrderType.RANDOM)) {
                Random random = new Random();
                boolean[] used = new boolean[buttonNum];
                for (int i = 0; i < buttonNum; i++)
                    used[i] = false;
                int count = 0;
                while (true) {
                    int i = random.nextInt(buttonNum);
                    if (!used[i]) {
                        used[i] = true;

                        dots[count].getLocationOnScreen(startLocations[count]);
                        startLocations[count][0] -= (buttonWidth - dots[count].getWidth()) / 2;
                        startLocations[count][1] -= (buttonWidth - dots[count].getHeight()) / 2;
                        setShowAnimation(dots[count], circleButtons[count],
                                originalLocations[count], startLocations[count],
                                endLocations[count], i);

                        count++;
                        if (count == buttonNum) break;
                    }
                }
            }
        }

    }

    @Override
    public void onClick(int index) {
        if (!state.equals(StateType.OPEN)) return;
        if (onSubButtonClickListener != null) onSubButtonClickListener.onClick(index);
        if (autoDismiss && !animationPlaying) startHideAnimations();
    }

    public void startHideAnimations() {
        animationPlaying = true;
        lightAnimationLayout();
        if (buttonType.equals(ButtonType.CIRCLE)) {
            if (hideOrderType.equals(OrderType.DEFAULT)) {
                for (int i = 0; i < buttonNum; i++) {
                    setHideAnimation(dots[i], circleButtons[i], endLocations[i], startLocations[i],
                            i);
                }
            } else if (hideOrderType.equals(OrderType.REVERSE)) {
                for (int i = 0; i < buttonNum; i++) {
                    setHideAnimation(dots[i], circleButtons[i], endLocations[i], startLocations[i],
                            buttonNum - i - 1);
                }
            } else if (hideOrderType.equals(OrderType.RANDOM)) {
                Random random = new Random();
                boolean[] used = new boolean[buttonNum];
                for (int i = 0; i < buttonNum; i++)
                    used[i] = false;
                int count = 0;
                while (true) {
                    int i = random.nextInt(buttonNum);
                    if (!used[i]) {
                        used[i] = true;

                        setHideAnimation(dots[count], circleButtons[count], endLocations[count],
                                startLocations[count], i);

                        count++;
                        if (count == buttonNum) break;
                    }
                }
            }
        }
    }

    /**
     * Set hide animation of each sub button.
     * 
     * @param dot The dot corresponding to the sub button.
     * @param button The sub button.
     * @param startLocation Start location of the animation.
     * @param endLocation End location of the animation.
     * @param index Index of the sub button in the array.
     */
    public void setHideAnimation(final View dot, final View button, int[] startLocation,
            int[] endLocation, final int index) {

        // position animation
        float[] sl = new float[2];
        float[] el = new float[2];
        sl[0] = startLocation[0] * 1.0f;
        sl[1] = startLocation[1] * 1.0f;
        el[0] = endLocation[0] * 1.0f;
        el[1] = endLocation[1] * 1.0f;

        float[] xs = new float[frames + 1];
        float[] ys = new float[frames + 1];
        getHideXY(sl, el, xs, ys);

        if (button != null) {
            ObjectAnimator xAnimator =
                    ObjectAnimator.ofFloat(button, "x", xs).setDuration(duration);
            xAnimator.setStartDelay(index * delay);
            xAnimator.setInterpolator(InterpolatorFactory.getInterpolator(hideMoveEaseType));
            xAnimator.start();

            ObjectAnimator yAnimator =
                    ObjectAnimator.ofFloat(button, "y", ys).setDuration(duration);
            yAnimator.setStartDelay(index * delay);
            yAnimator.setInterpolator(InterpolatorFactory.getInterpolator(hideMoveEaseType));
            yAnimator.start();
        }

        // scale animation
        float scaleW = 0;
        float scaleH = 0;
        if (buttonType.equals(ButtonType.CIRCLE)) {
            scaleW = dotWidth * 1.0f / buttonWidth;
            scaleH = dotWidth * 1.0f / buttonWidth;
        } else if (buttonType.equals(ButtonType.HAM)) {
            scaleW = barWidth * 1.0f / hamButtonWidth;
            scaleH = barHeight * 1.0f / hamButtonHeight;
        }

        if (button != null) {
            ObjectAnimator scaleXAnimator =
                    ObjectAnimator.ofFloat(button, "scaleX", 1f, scaleW).setDuration(duration);
            scaleXAnimator.setStartDelay(index * delay);
            scaleXAnimator.setInterpolator(InterpolatorFactory.getInterpolator(hideScaleEaseType));
            scaleXAnimator.start();

            ObjectAnimator scaleYAnimator =
                    ObjectAnimator.ofFloat(button, "scaleY", 1f, scaleH).setDuration(duration);
            scaleYAnimator.setStartDelay(index * delay);
            scaleYAnimator.setInterpolator(InterpolatorFactory.getInterpolator(hideScaleEaseType));
            scaleYAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dot.setVisibility(View.VISIBLE);
                    if (isInList || MEMORY_OPTIMIZATION) {
                        if (buttonType.equals(ButtonType.CIRCLE)) circleButtons[index] = null;

                    }
                }
            });
            scaleYAnimator.start();
        }

        // alpha animation
        View view1 = null;
        View view2 = null;
        if (button != null && button instanceof CircleButton) {
            view1 = ((CircleButton) button).getImageView();
            view2 = ((CircleButton) button).getTextView();
        } else if (button != null && button instanceof HamButton) {
            view1 = ((HamButton) button).getImageView();
            view2 = ((HamButton) button).getTextView();
        }

        if (view1 != null) {
            ObjectAnimator alphaAnimator1 =
                    ObjectAnimator.ofFloat(view1, "alpha", 1f, 0f).setDuration(duration);
            alphaAnimator1.setStartDelay(delay * index);
            alphaAnimator1.setInterpolator(InterpolatorFactory.getInterpolator(hideMoveEaseType));
            alphaAnimator1.start();
        }

        if (view2 != null) {
            ObjectAnimator alphaAnimator2 =
                    ObjectAnimator.ofFloat(view2, "alpha", 1f, 0f).setDuration(duration);
            alphaAnimator2.setStartDelay(delay * index);
            alphaAnimator2.setInterpolator(InterpolatorFactory.getInterpolator(hideMoveEaseType));
            alphaAnimator2.start();
        }

        // rotation animation
        if (button != null && button instanceof CircleButton) {
            ObjectAnimator rotateAnimator =
                    ObjectAnimator.ofFloat(((CircleButton) button).getFrameLayout(), "rotation", 0,
                            -rotateDegree).setDuration(duration);
            rotateAnimator.setStartDelay(index * delay);
            rotateAnimator.setInterpolator(InterpolatorFactory.getInterpolator(hideRotateEaseType));
            rotateAnimator.start();
        }

    }

    /**
     * Light the background, used when the boom menu button is to dismiss.
     */
    public void lightAnimationLayout() {
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofInt(animationLayout, "backgroundColor", dimType.value,
                        DimType.DIM_0.value).setDuration(duration + delay * (buttonNum - 1));
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // if (animatorListener != null)
                // animatorListener.toHide();
                state = StateType.CLOSING;
                // hide();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationLayout.removeAllViews();
                animationLayout.setVisibility(View.GONE);
                animationPlaying = false;
                // if (animatorListener != null)
                // animatorListener.hided();
                state = StateType.CLOSED;
                hide();
            }
        });
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // if (animatorListener != null)
                // animatorListener.hiding(animation
                // .getAnimatedFraction());
            }
        });
        objectAnimator.start();

        // share lines animation
        if (placeType.SHARE_3_1.v <= placeType.v && placeType.v <= PlaceType.SHARE_9_2.v) {
            ObjectAnimator shareLinesAnimator =
                    ObjectAnimator.ofFloat(shareLines, "offset", 0f, 1f).setDuration(
                            duration + delay * (buttonNum - 1));
            shareLinesAnimator.setStartDelay(0);
            shareLinesAnimator.start();
        }
    }

    /**
     * Set auto dismiss. If the boom menu button is auto dismiss, user can click one of the sub
     * buttons to dismiss the boom menu botton.
     * 
     * @param autoDismiss
     */
    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    /**
     * Set cancelable. If the boom menu button is cancelable, user can click the background to
     * dismiss it.
     * 
     * @param cancelable
     */
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * Set frames for animaitons.
     * 
     * @param frames
     */
    public void setFrames(int frames) {
        this.frames = frames;
    }

    /**
     * Set animation duration.
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Set start delay.
     * 
     * @param delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Set rotate degrees.
     * 
     * @param rotateDegree
     */
    public void setRotateDegree(int rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    /**
     * Set show order type.
     * 
     * @param showOrderType
     */
    public void setShowOrderType(OrderType showOrderType) {
        this.showOrderType = showOrderType;
    }

    /**
     * Set hide order type.
     * 
     * @param hideOrderType
     */
    public void setHideOrderType(OrderType hideOrderType) {
        this.hideOrderType = hideOrderType;
    }

    /**
     * Set OnClickListener.
     * 
     * @param onClickListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * @return The imagebuttons of sub buttons.
     */
    public ImageButton[] getImageButtons() {
        ImageButton[] imageButtons = new ImageButton[buttonNum];
        if (isInList || MEMORY_OPTIMIZATION) {
            // TODO to return a weak ImageButton[] instead of an empty
            // ImageButton[] if it can be useful
        } else {
            for (int i = 0; i < buttonNum; i++) {
                if (circleButtons[i] != null) {
                    imageButtons[i] = circleButtons[i].getImageButton();
                }
            }
        }
        return imageButtons;
    }

    /**
     * @return The imageviews of sub buttons.
     */
    public ImageView[] getImageViews() {
        ImageView[] imageViews = new ImageView[buttonNum];
        if (isInList || MEMORY_OPTIMIZATION) {
            // TODO to return a weak ImageView[] instead of an empty ImageView[]
            // if it can be useful
        } else {
            if (buttonType.equals(ButtonType.CIRCLE)) {
                for (int i = 0; i < buttonNum; i++)
                    if (circleButtons[i] != null) imageViews[i] = circleButtons[i].getImageView();
            }
        }
        return imageViews;
    }

    /**
     * @return The textviews of sub buttons.
     */
    public TextView[] getTextViews() {
        TextView[] textViews = new TextView[buttonNum];
        if (isInList || MEMORY_OPTIMIZATION) {
            // TODO to return a weak TextView[] instead of an empty TextView[]
            // if it can be useful
        } else {
            if (buttonType.equals(ButtonType.CIRCLE)) {
                for (int i = 0; i < buttonNum; i++)
                    if (circleButtons[i] != null) textViews[i] = circleButtons[i].getTextView();
            }
        }
        return textViews;
    }

    /**
     * Set OnSubButtonClickListener.
     * 
     * @param onSubButtonClickListener
     */
    public void setOnSubButtonClickListener(OnSubButtonClickListener onSubButtonClickListener) {
        this.onSubButtonClickListener = onSubButtonClickListener;
    }



    /**
     * Set the offset of the shadow layouts of sub buttons. If xOffset is 0 and yOffset is 0, then
     * the shadow layout is at the center.
     * 
     * @param xOffset In pixels.
     * @param yOffset In pixels.
     */
    public void setSubButtonShadowOffset(float xOffset, float yOffset) {
        for (int i = 0; i < buttonNum; i++) {
            if (buttonType.equals(ButtonType.CIRCLE)) {
                if (circleButtons[i] != null) {
                    circleButtons[i].setShadowDx(xOffset);
                    circleButtons[i].setShadowDy(yOffset);
                } else {
                    subButtonsXOffsetOfShadow = xOffset;
                    subButtonsYOffsetOfShadow = xOffset;
                }
            }
        }
    }

    /**
     * Set the dim type. Dim_0 for no dim. Max is Dim_9.
     * 
     * @param dimType
     */
    public void setDimType(DimType dimType) {
        this.dimType = dimType;
    }

    /**
     * Set the click effect.
     * 
     * @param clickEffectType
     */
    public void setClickEffectType(ClickEffectType clickEffectType) {
        setRipple(clickEffectType);
        for (int i = 0; i < buttonNum; i++) {
            if (buttonType.equals(ButtonType.CIRCLE)) {
                if (circleButtons[i] != null) {
                    circleButtons[i].setRipple(clickEffectType);
                } else {
                    // delaying apply on the fly in the shoot() function
                    this.clickEffectType = clickEffectType;
                }
            }
        }
    }



    private View setViewLocationInAnimationLayout(final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = null;
        if (buttonType.equals(ButtonType.CIRCLE)) {
            lp = new LinearLayout.LayoutParams(buttonWidth, buttonWidth);
        } else if (buttonType.equals(ButtonType.HAM)) {
            lp = new LinearLayout.LayoutParams(hamButtonWidth, hamButtonHeight);
        }
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setVisibility(View.INVISIBLE);
        animationLayout.addView(view, lp);
        return view;
    }



    public void setShowAnimation(final View dot, final View button, int[] originalLocation,
            int[] startLocation, int[] endLocation, final int index) {
        button.bringToFront();

        final View view = setViewLocationInAnimationLayout(button, originalLocation);

        float[] sl = new float[2];
        float[] el = new float[2];
        sl[0] = startLocation[0] * 1.0f;
        sl[1] = startLocation[1] * 1.0f;
        el[0] = endLocation[0] * 1.0f;
        el[1] = endLocation[1] * 1.0f;

        float[] xs = new float[frames + 1];
        float[] ys = new float[frames + 1];
        getShowXY(sl, el, xs, ys);

        if (view != null) {
            ObjectAnimator xAnimator = ObjectAnimator.ofFloat(view, "x", xs).setDuration(duration);
            xAnimator.setStartDelay(delay * index);
            xAnimator.setInterpolator(InterpolatorFactory.getInterpolator(showMoveEaseType));
            xAnimator.start();

            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(view, "y", ys).setDuration(duration);
            yAnimator.setStartDelay(delay * index);
            yAnimator.setInterpolator(InterpolatorFactory.getInterpolator(showMoveEaseType));
            yAnimator.start();
        }

        // scale animation
        float scaleW = 0;
        float scaleH = 0;
        if (buttonType.equals(ButtonType.CIRCLE)) {
            scaleW = dotWidth * 1.0f / buttonWidth;
            scaleH = dotWidth * 1.0f / buttonWidth;
        } else if (buttonType.equals(ButtonType.HAM)) {
            scaleW = barWidth * 1.0f / hamButtonWidth;
            scaleH = barHeight * 1.0f / hamButtonHeight;
        }

        if (view != null) {
            view.setScaleX(scaleW);

            ObjectAnimator scaleXAnimator =
                    ObjectAnimator.ofFloat(view, "scaleX", scaleW, 1f).setDuration(duration);
            scaleXAnimator.setStartDelay(delay * index);
            scaleXAnimator.setInterpolator(InterpolatorFactory.getInterpolator(showScaleEaseType));
            scaleXAnimator.start();

            view.setScaleY(scaleH);
            ObjectAnimator scaleYAnimator =
                    ObjectAnimator.ofFloat(view, "scaleY", scaleH, 1f).setDuration(duration);
            scaleYAnimator.setStartDelay(delay * index);
            scaleYAnimator.setInterpolator(InterpolatorFactory.getInterpolator(showScaleEaseType));
            scaleYAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    dot.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animationPlaying = false;
                }
            });
            scaleYAnimator.start();
        }

        // alpha animation
        View view1 = null;
        View view2 = null;
        if (button != null && button instanceof CircleButton) {
            view1 = ((CircleButton) button).getImageView();
            view2 = ((CircleButton) button).getTextView();
        } else if (button != null && button instanceof HamButton) {
            view1 = ((HamButton) button).getImageView();
            view2 = ((HamButton) button).getTextView();
        }

        if (view1 != null) {
            ObjectAnimator alphaAnimator1 =
                    ObjectAnimator.ofFloat(view1, "alpha", 0f, 1f).setDuration(duration);
            alphaAnimator1.setStartDelay(delay * index);
            alphaAnimator1.setInterpolator(InterpolatorFactory.getInterpolator(showMoveEaseType));
            alphaAnimator1.start();
        }

        if (view2 != null) {
            ObjectAnimator alphaAnimator2 =
                    ObjectAnimator.ofFloat(view2, "alpha", 0f, 1f).setDuration(duration);
            alphaAnimator2.setStartDelay(delay * index);
            alphaAnimator2.setInterpolator(InterpolatorFactory.getInterpolator(showMoveEaseType));
            alphaAnimator2.start();
        }

        // rotation animation
        if (view != null && view instanceof CircleButton) {
            ObjectAnimator rotateAnimator =
                    ObjectAnimator.ofFloat(((CircleButton) view).getFrameLayout(), "rotation", 0,
                            rotateDegree).setDuration(duration);
            rotateAnimator.setStartDelay(delay * index);
            rotateAnimator.setInterpolator(InterpolatorFactory.getInterpolator(showRotateEaseType));
            rotateAnimator.start();
        }
    }

    /**
     * Get the function of the road of the animation of showing. Then calculate each points to be
     * ready for the animation.
     * 
     * @param startPoint Start point of the animation.
     * @param endPoint End point of the animation.
     * @param xs The values on the x axis.
     * @param ys The values on the y axis.
     */
    private void getShowXY(float[] startPoint, float[] endPoint, float[] xs, float[] ys) {
        if (boomType.equals(BoomType.LINE)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float k = (y2 - y1) / (x2 - x1);
            float b = y1 - x1 * k;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = k * xs[i] + b;
            }
        } else if (boomType.equals(BoomType.PARABOLA)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float x3 = (startPoint[0] + endPoint[0]) / 2;
            float y3 = Math.min(startPoint[1], endPoint[1]) / 2;
            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.HORIZONTAL_THROW)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x3 = endPoint[0];
            float y3 = endPoint[1];
            float x2 = x3 * 2 - x1;
            float y2 = y1;

            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = x3 - x1;
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = x1 + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.PARABOLA_2)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float x3 = (startPoint[0] + endPoint[0]) / 2;
            float y3 = (windowManager.getDefaultDisplay().getHeight()// Util.getInstance().getScreenHeight(mContext)
                    - Math.max(startPoint[1], endPoint[1]))
                    / 2 + Math.max(startPoint[1], endPoint[1]);
            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = x2 - x1;
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = x1 + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.HORIZONTAL_THROW_2)) {
            float x1 = endPoint[0];
            float y1 = endPoint[1];
            float x3 = startPoint[0];
            float y3 = startPoint[1];
            float x2 = x3 * 2 - x1;
            float y2 = y1;

            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        }
    }

    /**
     * Get the function of the road of the animation of dismissing. Then calculate each points to be
     * ready for the animation.
     * 
     * @param startPoint Start point of the animation.
     * @param endPoint End point of the animation.
     * @param xs The values on the x axis.
     * @param ys The values on the y axis.
     */
    private void getHideXY(float[] startPoint, float[] endPoint, float[] xs, float[] ys) {
        if (boomType.equals(BoomType.LINE)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float k = (y2 - y1) / (x2 - x1);
            float b = y1 - x1 * k;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = k * xs[i] + b;
            }
        } else if (boomType.equals(BoomType.PARABOLA)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float x3 = (startPoint[0] + endPoint[0]) / 2;
            float y3 = Math.min(startPoint[1], endPoint[1]) / 2;
            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.HORIZONTAL_THROW)) {
            float x1 = endPoint[0];
            float y1 = endPoint[1];
            float x3 = startPoint[0];
            float y3 = startPoint[1];
            float x2 = x3 * 2 - x1;
            float y2 = y1;

            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.PARABOLA_2)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x2 = endPoint[0];
            float y2 = endPoint[1];
            float x3 = (startPoint[0] + endPoint[0]) / 2;
            float y3 = (windowManager.getDefaultDisplay().getHeight()// Util.getInstance().getScreenHeight(mContext)
                    - Math.max(startPoint[1], endPoint[1]))
                    / 2 + Math.max(startPoint[1], endPoint[1]);
            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = x2 - x1;
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = x1 + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        } else if (boomType.equals(BoomType.HORIZONTAL_THROW_2)) {
            float x1 = startPoint[0];
            float y1 = startPoint[1];
            float x3 = endPoint[0];
            float y3 = endPoint[1];
            float x2 = x3 * 2 - x1;
            float y2 = y1;

            float a, b, c;

            a =
                    (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                            / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
            b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
            c = y1 - (x1 * x1) * a - x1 * b;

            float per = 1f / frames;
            float xx = endPoint[0] - startPoint[0];
            for (int i = 0; i <= frames; i++) {
                float offset = i * per;
                xs[i] = startPoint[0] + offset * xx;
                ys[i] = a * xs[i] * xs[i] + b * xs[i] + c;
            }
        }

    }


    public interface OnSubButtonClickListener {
        void onClick(int buttonIndex);
    }
}
