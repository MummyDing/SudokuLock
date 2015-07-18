package com.example.mummyding.sudokulock;

import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Type;

/**
 * Created by mummyding on 15-7-17.
 */
public class SudokuView extends View {

    private static final int COUNT = 3;
    Cell [] cell;
    int [] selectedCell;
    int RADIUS ,OFFSET;
    int ScreenWidth,ScreenHeight;
    int startX,startY,selectedCount,lastX,lastY;
    boolean drawFinish ;

    Paint mPaint ;


    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);
       init(context);

    }

    private void initCell(){
        //初始化各点
        for(int i = 0 ; i < COUNT ; i++ )
            for (int j = 0 ; j < COUNT ; j++) {
                cell[i * COUNT + j].setIsSelected(false);
                cell[i * COUNT + j].setX(startX + OFFSET * j - RADIUS/2);
                cell[i * COUNT + j].setY(startY + OFFSET * i - RADIUS/2);
            }
    }
    private void init(Context context){

        cell = new Cell[COUNT * COUNT];
        selectedCell = new int[COUNT*COUNT];
        mPaint = new Paint();
        //获取屏幕的宽度和高度
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);

        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;


        this.setMinimumWidth(ScreenWidth);
        this.setMinimumHeight(ScreenHeight);

        drawFinish = false; //是否绘制完成
        selectedCount = 0; //已经选中的点个数
        RADIUS = ScreenWidth / 12; //半径
        OFFSET = ScreenWidth / 4 ; //点之间的间距
        startX = OFFSET; //起始点横坐标
        startY = (ScreenHeight - OFFSET * 2) / 2; //起始点纵坐标

        for(int i = 0 ; i < COUNT*COUNT ; i++){
            cell[i] = new Cell();
        }
        initCell();
    }

    int inWhichCircle(int x, int y){
        for(int i = 0 ; i < COUNT*COUNT ; i++){
            if(cell[i].isSelected() == false){
                if((Math.abs(x - cell[i].getX())<RADIUS) && Math.abs(y - cell[i].getY()) < RADIUS){
                    return i;
                }
            }
        }
        return -1;
    }

    void drawCell(Canvas canvas){
       for(int i = 0 ; i < COUNT*COUNT ; i++){
            //选择画笔&&画圆
            if(cell[i].isSelected()){
                mPaint.setColor(Color.GREEN);
                mPaint.setStrokeWidth(10);
                //画圆
                canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                mPaint.setStrokeWidth(20);
                //画点
                canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            } else {
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth(5);
                //画圆
                canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                //画点
                canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            }

        }
    }

    void drawLine(Canvas canvas) {
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        for(int i = 1 ; i < selectedCount ; i++){
            Cell lastCell = cell[selectedCell[i-1]],thisCell = cell[selectedCell[i]];
            canvas.drawLine(lastCell.getX(), lastCell.getY(), thisCell.getX(), thisCell.getY(), mPaint);
        }
        if(selectedCount !=0 &&(lastX !=0 || lastY != 0)){
            canvas.drawLine(cell[selectedCell[selectedCount - 1]].getX(), cell[selectedCell[selectedCount - 1]].getY(), lastX, lastY, mPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);;
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        drawCell(canvas);
        drawLine(canvas);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int tmpIndex;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawFinish = false;
                if((tmpIndex = inWhichCircle((int)event.getX(),(int)event.getY())) != -1){
                    cell[tmpIndex].setIsSelected(true);
                    selectedCell[selectedCount++] = tmpIndex;
                    this.postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                  if(drawFinish == false){
                    if((tmpIndex = inWhichCircle((int)event.getX(),(int)event.getY())) != -1){
                        cell[tmpIndex].setIsSelected(true);
                        selectedCell[selectedCount++] = tmpIndex;
                    }
                }
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:

                drawFinish = true;
                lastX = lastY = 0;
                selectedCount = 0;
                initCell();
                this.postInvalidate();
                break;

        }
        return true;
    }
}
