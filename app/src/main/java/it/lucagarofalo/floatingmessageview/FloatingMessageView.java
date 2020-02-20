package it.lucagarofalo.floatingmessageview;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 *      September 17, 2019
 *      @author Luca Garofalo
 */
public class FloatingMessageView extends FrameLayout
{
    /**    Default animation duration */
    private final long ANIM_DURATION = 300;
    public static final long DURATION_SHORT = 2000;
    public static final long DURATION_LONG = 5000;
    public static final long DURATION_INDEFINITE = -1;

    /*    Directions */
    private static final int DIRECTION_VARIABLE = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;

    /*  UI Components */
    private CardView messageCardView;
    private TextView messageTextView;
    private ImageView arrowView;

    /**   View into self-attach  */
    private View targetView;
    private int displayWidth = 0, displayHeight = 0;
    private int MAX_MESSAGE_WIDTH;
    private long duration = DURATION_SHORT;
    /**   Direction of view, default is DIRECTION_VARIABLE.  */
    private int direction = DIRECTION_VARIABLE;
    /**   View will be shown/hidden with animations.  */
    private boolean animationEnabled = true;
    /**   Animation to play when this view is shown */
    private Animation animationIn;
    /**   Animation to play when this view is hidden */
    private Animation animationOut;

    public FloatingMessageView(Context context)
    {
        super(context);
    }

    public FloatingMessageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FloatingMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingMessageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private FloatingMessageView(@NonNull View target)
    {
        super(target.getContext());
        targetView = target;
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(inflate(target.getContext(), R.layout.floating_message_view_layout, null));

        //  Init UI components
        messageCardView = findViewById(R.id.messageCardView);
        messageTextView = findViewById(R.id.messageTextView);
        arrowView = findViewById(R.id.arrowView);

        /*
                Setup
         */

        if(getContext() instanceof Activity)
        {
            displayWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
            displayHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
        }

        MAX_MESSAGE_WIDTH = displayWidth / 2;

        //  Set max height
        messageCardView.getViewTreeObserver().addOnGlobalLayoutListener(() ->
        {
            if(getWidth() > MAX_MESSAGE_WIDTH)
            {
                setLayoutParams(new LayoutParams(MAX_MESSAGE_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
                invalidate();
                arrowView.post(() -> arrowView.setX( (getWidth() / 2) - ( arrowView.getWidth() / 2) ));
            }

        });

        arrowView.post(() -> arrowView.setX( (getWidth() / 2) - ( arrowView.getWidth() / 2) ));
    }

    public static class Builder
    {
        private FloatingMessageView floatingMessageView;

        public Builder(@NonNull View target)
        {
            floatingMessageView = new FloatingMessageView(target);
        }

        /**
         *      Set message to show in the view.
         *      @param message   Message to show
         */
        public Builder setMessage(String message)
        {
            floatingMessageView.messageTextView.setText(message);
            return this;
        }

        /**
         *      Set message to show in the view.
         *      @param message   Message to show
         */
        public Builder setMessage(int message)
        {
            String mex = floatingMessageView.getContext().getResources().getString(message);
            floatingMessageView.messageTextView.setText(mex);
            return this;
        }

        /**
         *     Set if play animation on view entering.
         */
        public Builder setAnimationEnabled(boolean value)
        {
            floatingMessageView.animationEnabled = value;
            return this;
        }

        /**
         *      Set animation to play on view entering.
         *      @param animation animation to play
         */
        public Builder setAnimationIn(Animation animation)
        {
            floatingMessageView.animationIn = animation;
            return this;
        }

        /**
         *     Set Direction Up avoiding calculation of it.
         */
        public Builder setEverUp()
        {
            floatingMessageView.direction = DIRECTION_UP;
            return this;
        }

        /**
         *     Set Direction Down avoiding calculation of it.
         */
        public Builder setEverDown()
        {
            floatingMessageView.direction = DIRECTION_DOWN;
            return this;
        }

        /**
         *      Set animation to play when view goes out.
         *      @param animation Animation to play.
         */
        public Builder setAnimationOut(Animation animation)
        {
            floatingMessageView.animationOut = animation;
            return this;
        }

        public Builder setBackgroundColor(int color)
        {
            floatingMessageView.messageCardView.setCardBackgroundColor(color);
            floatingMessageView.arrowView.getDrawable().setTint(color);
            return this;
        }

        public Builder setTextColor(int color)
        {
            floatingMessageView.messageTextView.setTextColor(color);
            return this;
        }

        /**
         *      Set the image to show as 'pointer' of the view.
         *      @param drawable Image to show
         */
        public Builder setArrowDrawable(Drawable drawable)
        {
            floatingMessageView.arrowView.setImageDrawable(drawable);
            return this;
        }

        /**
         *      Set corner radius of the inner view
         *      @param radius Radius of the corners
         */
        public Builder setContentViewCornerRadius(float radius)
        {
            floatingMessageView.messageCardView.setRadius(radius);
            return this;
        }

        /**
         *      Set duration of the message before vanish.
         *      @param ms Time expressed in milliseconds.
         */
        public Builder setDuration(long ms)
        {
            floatingMessageView.duration = ms;
            if(ms < 0)
                floatingMessageView.duration = DURATION_INDEFINITE;
            return this;
        }

        /**
         *      Set a view to show inside this one.
         *      @param view View to show.
         */
        public Builder setView(View view)
        {
            if(view != null)
            {
                ViewGroup layout = floatingMessageView.findViewById(R.id.customView);
                layout.removeAllViews();

                if(view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);

                layout.addView(view);
            }

            return this;
        }

        public FloatingMessageView build()
        {
            //  Operation dangerous if TextView changes text next.
            if(floatingMessageView.messageTextView.getText().toString().trim().isEmpty())
                floatingMessageView.messageTextView.setVisibility(GONE);

            if(floatingMessageView.animationOut != null)
            {
                floatingMessageView.animationOut.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        floatingMessageView.setVisibility(GONE);
                        floatingMessageView.detach();
                    }
                    @Override public void onAnimationRepeat(Animation animation) { }
                });
            }

            return floatingMessageView;
        }
    }

    /**
     *      @return View where is set to be attached.
     */
    public View getTargetView()
    {
        return targetView;
    }

    private void attach()
    {
        if(getParent() == null)
        {
            ViewGroup root = (ViewGroup) targetView.getRootView();
            root.addView(this);
            calculatePosition();
        }
    }

    private void detach()
    {
        if(getParent() != null)
            ((ViewGroup) getParent()).removeView(this);
    }

    private boolean isAttached() { return getParent() != null; }

    /**
     *      Calculate position to assign to this view.
     *      It's executed on post.
     */
    private void calculatePosition()
    {
        int visibility = getVisibility();
        setVisibility(INVISIBLE);
        post(() ->
        {
            setVisibility(visibility);

            int[] position = new int[2];
            targetView.getLocationInWindow(position);

            int targetX = position[0];
            int targetY = position[1];
            int centerTargetX = targetX + (targetView.getWidth()/2);
            int centerTargetY = targetY + (targetView.getHeight()/2);
            int myWidth = getWidth() > MAX_MESSAGE_WIDTH ? MAX_MESSAGE_WIDTH : getWidth();
            int boundRight = displayWidth - myWidth;
            float xpos, ypos;

            /*
                    Calculation of X position
             */
            xpos = centerTargetX - (myWidth / 2f);

            if(xpos > boundRight)
            {
                xpos = boundRight;
                if(centerTargetX > (xpos + (myWidth / 2f)))
                    arrowView.post(() -> arrowView.setX( centerTargetX - getX() - (arrowView.getWidth()/2f) ));
            }
            else if(xpos < 0)
            {
                xpos = 0;
                if(centerTargetX < (myWidth / 2f))
                    arrowView.post(() -> arrowView.setX( centerTargetX - (arrowView.getWidth()/2f) ));
            }

            /*
                    Calculation of Y position
             */
            if(direction == DIRECTION_VARIABLE)
            {
                if(centerTargetY > (displayHeight/2f))
                {
                    //  Display on top of view target
                    ypos = targetY - getHeight();

                    //  Set below of message
                    ViewGroup parent = (ViewGroup) arrowView.getParent();
                    parent.removeView(arrowView);
                    parent.addView(arrowView);

                    arrowView.setRotation(0);
                }
                else
                {
                    //      Display under view target
                    ypos = targetY + targetView.getHeight();

                    //  Set above of message
                    ViewGroup parent = (ViewGroup) arrowView.getParent();
                    parent.removeView(arrowView);
                    parent.addView(arrowView, 0);

                    arrowView.setRotation(180);
                }
            }
            else
            {
                switch (direction)
                {
                    default:
                    case DIRECTION_UP:
                        //  Display on top of view target
                        ypos = targetY - getHeight();

                        //  Set below of message
                        ViewGroup parent = (ViewGroup) arrowView.getParent();
                        parent.removeView(arrowView);
                        parent.addView(arrowView);

                        arrowView.setRotation(0);
                        break;

                    case DIRECTION_DOWN:
                        //      Display under view target
                        ypos = targetY + targetView.getHeight();

                        //  Set above of message
                        parent = (ViewGroup) arrowView.getParent();
                        parent.removeView(arrowView);
                        parent.addView(arrowView, 0);

                        arrowView.setRotation(180);
                        break;
                }
            }

            setX( xpos );
            setY( ypos );
        });
    }

    /**
     *      Set this view as visible, and attach it if it isn't.
     */
    public void show()
    {
        if( ! isAttached())
            attach();
        else
            calculatePosition();

        if(animationEnabled)
        {
            post(() ->
            {
                setVisibility(VISIBLE);

                if(animationIn != null)
                    startAnimation(animationIn);
                else
                {
                    float currentAlpha = getAlpha();
                    setY(getY() + 200);
                    setAlpha(0f);
                    animate().yBy(-200).alpha(currentAlpha)
                            .setDuration(ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
                }
            });
        }
        else
            setVisibility(VISIBLE);

        if( duration != DURATION_INDEFINITE)
            new Handler().postDelayed(this::hide, duration);
    }

    /**
     *      Set this view as not visible, and detach it from its parent.
     */
    public void hide()
    {
        if(isAttached())
        {
            if(animationEnabled)
            {
                if(animationOut != null)
                    startAnimation(animationOut);
                else
                {
                    float oldY = getY();
                    float oldAlpha = getAlpha();
                    animate().yBy(200).alpha(0f)
                            .setDuration(ANIM_DURATION)
                            .setInterpolator(new AccelerateInterpolator())
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animator) { }
                                @Override
                                public void onAnimationEnd(Animator animator)
                                {
                                    setY(oldY);
                                    setAlpha(oldAlpha);
                                    setVisibility(GONE);
                                    detach();
                                }
                                @Override
                                public void onAnimationCancel(Animator animator) { }
                                @Override
                                public void onAnimationRepeat(Animator animator) { }
                            })
                            .start();
                }
            }
            else
                setVisibility(GONE);
        }
    }

}
