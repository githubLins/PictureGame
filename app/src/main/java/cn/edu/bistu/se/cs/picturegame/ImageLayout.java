package cn.edu.bistu.se.cs.picturegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ImageLayout extends RelativeLayout implements View.OnClickListener{
    private static final String CONNECTOR="-";
    private static final int DURATION_TIME=500;
    private static final int TIME_BASE=60;

    int piece=3;
    //padding
    private int padding;
    //3dp
    private static final int MARGIN=3;
    private int margin;

    //picture
    private Bitmap image;
    //random list
    private List<ImagePiece> imagePieces;
    //ImageVIew List shown on the layout
    private ImageView[] imageViews;
    private int length;
    private int itemLength;
    //flag to mark if it is first set
    private boolean first=true;
    //first selected ImageView
    private ImageView firstSelectedView;
    //Secongd selected ImageView
    private ImageView secondSelectedView;
    //animaton layout
    private RelativeLayout animationLayout;
    //Animation flag,block user clicks when it is animation
    private boolean isInAnimation=false;

    private static final int NEXT_LEVEL=0x0000;
    private static final int STEP_CHANGE=0x0001;
    private static final int TIME_CHANGE=0x0002;
    private static final int GAME_OVER=0x0003;
    //callback listenner instance
    private ImageLayoutListener imageListener;
    public void setImageListener(ImageLayoutListener imageListener){
        this.imageListener=imageListener;
    }
    private int currentStep;
    private int currentTime=0;
    private boolean isGameSuccess;
    private boolean isGameOver;
    private boolean isGamePaused;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what) {
                case NEXT_LEVEL:
                    if (imageListener != null) {
                        imageListener.nextLevel();

                    }
                    break;
                case STEP_CHANGE:
                    if(imageListener!=null){
                        imageListener.stepChange(currentStep);
                    }
                    break;
                case TIME_CHANGE:
                    if (isGameSuccess || isGameOver || isGamePaused) {
                        return;
                    }
                    if (null != imageListener) {
                        imageListener.timeChange(currentTime);
                    }
                    if (0 == currentTime) {
                        isGameOver = true;
                        handler.sendEmptyMessage(GAME_OVER);
                        return;
                    }
                    currentTime--;
                    handler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
                    break;
                case GAME_OVER:
                    if (null != imageListener) {
                        imageListener.gameOver();
                    }
                    break;
                default:
                    break;

            }
        }
    };
    public ImageLayout(Context context){
        this(context,null);
    }
    public ImageLayout(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }
    public ImageLayout(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    private void init(){
        margin=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,MARGIN,getResources().getDisplayMetrics());
        // 设置Layout的内边距，四边一致，设置为四内边距中的最小值
        padding=Math.min(Math.min(getPaddingBottom(),getPaddingTop()),Math.min(getPaddingLeft(),getPaddingRight()));
        currentStep=0;
        initTime();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureStep){
        super.onMeasure(widthMeasureSpec,heightMeasureStep);
        // 获得游戏布局的边长
        length=Math.min(getMeasuredWidth(),getMeasuredHeight());
        if(first){
            isGameOver=false;
            isGameSuccess=false;
            first=false;
            initBitmap();
            initViews();
        }
        setMeasuredDimension(length,length);
    }
    //准备图片
    private void initBitmap() {
        if (image == null) {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.tupian1);
        }

        imagePieces = new ImageSpliter().splitImage(image, piece);
        Collections.sort(imagePieces,new Comparator<ImagePiece>(){
            @Override
                    public int compare(ImagePiece imagePiece1,ImagePiece imagePiece2){
                if(imagePiece1==imagePiece2){
                    return 0;
                }
                return Math.random()>0.5? -1:1;
            }
        });
    }
    private void initViews(){
        // 获得Item的宽度
         itemLength=(length-2*padding-(piece-1)*margin)/piece;
        imageViews=new ImageView[piece*piece];
        for (int i=0;i<imageViews.length;i++){
            ImageView item=new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(imagePieces.get(i).getBitmap());
            item.setId(i+1);
            item.setTag(i+CONNECTOR+imagePieces.get(i).getIndex());
            imageViews[i]=item;

            LayoutParams layoutParams=new LayoutParams(itemLength,itemLength);

            if (0 != i % piece) {
                layoutParams.leftMargin = margin;
                layoutParams.addRule(RelativeLayout.RIGHT_OF, imageViews[i - 1].getId());
            }

            if (i + 1 > piece) {
                layoutParams.topMargin = margin;
                layoutParams.addRule(RelativeLayout.BELOW, imageViews[i - piece].getId());

           }

            addView(item, layoutParams);

        }
    }
    private void initTime(){
        currentTime=(int)Math.pow(2,piece-3)*TIME_BASE;
        handler.sendEmptyMessage(TIME_CHANGE);
    }
    //两个成员变量来存储这两个Item，然后再去交换
    @Override
    public void onClick(View view) {
        if (isInAnimation) {
            return;
        }
        if (firstSelectedView == null) {
            //点击第一个Item
            firstSelectedView = (ImageView) view;
            firstSelectedView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            //点击第二个Item
            secondSelectedView = (ImageView) view;
            exchangeSelectImages();
        }
    }
    private void exchangeSelectImages(){
        currentStep++;
        handler.sendEmptyMessage(STEP_CHANGE);
        firstSelectedView.setColorFilter(null);
        setupAnimationLayout();
        ImageView firstAnimationView=createAnimationView(firstSelectedView);
        ImageView secondAnimationView=createAnimationView(secondSelectedView);
        animationLayout.addView(firstAnimationView);
        animationLayout.addView(secondAnimationView);

        Animation firstAnimation=createAnimation(firstSelectedView,secondSelectedView);
        final Animation secondAnimation=createAnimation(secondSelectedView,firstSelectedView);
        firstAnimationView.setAnimation(firstAnimation);
        secondAnimationView.setAnimation(secondAnimation);
        firstAnimation.setAnimationListener(new Animation.AnimationListener(){
            @Override
                    public void onAnimationStart(Animation animation){
                isInAnimation=true;
                firstSelectedView.setVisibility(View.INVISIBLE);
                secondSelectedView.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onAnimationEnd(Animation animation){
                String firstSelectedTag=(String)firstSelectedView.getTag();
                String secondSelectedTag=(String)secondSelectedView.getTag();

                Bitmap firstSelectBitmap=getBitmapByTag(firstSelectedTag);
                Bitmap secondSelectBitmap=getBitmapByTag(secondSelectedTag);

                firstSelectedView.setImageBitmap(secondSelectBitmap);
                secondSelectedView.setImageBitmap(firstSelectBitmap);

                firstSelectedView.setTag(secondSelectedTag);
                secondSelectedView.setTag(firstSelectedTag);

                firstSelectedView.setVisibility(View.VISIBLE);
                secondSelectedView.setVisibility(View.VISIBLE);
                firstSelectedView=null;
                secondSelectedView=null;
                animationLayout.removeAllViews();

                checkSuccess();
                isInAnimation=false;


            }
            @Override
            public void onAnimationRepeat(Animation animation){

            }
        });
    }
    private int getIndexInViewByTag(String tag){
        return Integer.parseInt(tag.split(CONNECTOR)[0]);
    }
    private int getIndexInImageByTag(String tag){
        return Integer.parseInt(tag.split(CONNECTOR)[1]);
    }
    private Bitmap getBitmapByTag(String tag) {
        int indexInView = getIndexInViewByTag(tag);
        return imagePieces.get(indexInView).getBitmap();
    }
    private void setupAnimationLayout() {
        if (null == animationLayout) {
            animationLayout = new RelativeLayout(getContext());
            addView(animationLayout);
        }
    }
    private ImageView createAnimationView(ImageView selectedView) {
        ImageView animationView = new ImageView(getContext());
        animationView.setImageBitmap(getBitmapByTag((String) selectedView.getTag()));
        LayoutParams params = new LayoutParams(itemLength, itemLength);
        params.leftMargin = selectedView.getLeft() - padding;
        params.topMargin = selectedView.getTop() - padding;
        animationView.setLayoutParams(params);
        return animationView;
    }
    private Animation createAnimation(ImageView first, ImageView second) {
        // 设置动画
        TranslateAnimation animation = new TranslateAnimation(0, second.getLeft() - first.getLeft(),
                0, second.getTop() - first.getTop());
        animation.setDuration(DURATION_TIME);
        animation.setFillAfter(true);
        return animation;
    }
    private void checkSuccess() {
        boolean success = true;
        for (int i = 0; i < imageViews.length; i++) {
            String tag = (String) imageViews[i].getTag();
            if (i != getIndexInImageByTag(tag)) {
                success = false;
                break;
            }
        }
        if (success) {
            isGameSuccess = true;
            handler.sendEmptyMessage(NEXT_LEVEL);
        }
    }
    public void nextLevel() {
        this.removeAllViews();
        animationLayout = null;
        piece++;
        isGameSuccess = false;
        isGameOver = false;
        isGamePaused = false;
        currentStep = 0;
        handler.sendEmptyMessage(STEP_CHANGE);
        initBitmap();
        initViews();
        initTime();
    }
    public int getLevel() {
        return piece - 2;
    }
    public void restart() {
        piece--;
        handler.removeMessages(TIME_CHANGE);
        nextLevel();
    }
    public void pause() {
        if (!isGamePaused) {
            isGamePaused = true;
            handler.removeMessages(TIME_CHANGE);
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[i].setColorFilter(Color.GRAY, PorterDuff.Mode.CLEAR);
            }
        }
    }
    public void reset() {
        piece = 2;
        handler.removeMessages(TIME_CHANGE);
        nextLevel();
    }
        public void resume() {
            if (isGamePaused) {
                isGamePaused = false;
                handler.sendEmptyMessage(TIME_CHANGE);
                for (int i = 0; i < imageViews.length; i++) {
                    imageViews[i].setColorFilter(null);
                }
            }
        }
    }



