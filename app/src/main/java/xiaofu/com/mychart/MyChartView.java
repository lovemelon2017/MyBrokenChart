package xiaofu.com.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 折线图  可动态改变xy 坐标数值
 */

public class MyChartView extends View {
    private float viewWith;
    private float viewHeight;
    private float brokenLineWith = 0.5f;
    private int straightLineColor = 0xffe2e2e2;  //虚线
    private String blueColor = "#6196ff";  //当前圆圈内颜色
    private String blueColorOut = "#4d72a6ff";  //当前圆圈外颜色
    private String textGray = "#999999";
    private int maxScore = 4; // y轴最高
    private int minScore = 1; // y轴最低
    private String[] monthText = new String[]{"8/10", "11/11", "10/12", "现在"};
    private String[] typeText = new String[]{"偏油", "适中", "缺油", "严重缺油"};
    private int[] score = new int[]{1, 2, 3, 4};
    private int selectMonth = score.length;//选中的月份
    private List<Point> scorePoints;
    private int textSize = dipToPx(10); //字体大小
    private Paint brokenPaint;
    private Paint dottedPaint;
    private Paint textPaint;
    private Path brokenPath;

    public MyChartView(Context context) {
        super(context);
        init();
    }

    public MyChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        brokenPath = new Path();

        brokenPaint = new Paint();
        brokenPaint.setAntiAlias(true);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPaint.setStrokeWidth(dipToPx(1));
        brokenPaint.setStrokeCap(Paint.Cap.ROUND);

        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeWidth(brokenLineWith);
        dottedPaint.setColor((straightLineColor));
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor((Color.parseColor(textGray)));
        textPaint.setTextSize(sp2px(10));


    }

    private void initData() {
        scorePoints = new ArrayList<>();
        float maxScoreYCoordinate = viewHeight * 1 / 6f; //y轴最高
        float minScoreYCoordinate = viewHeight * 4 / 6f; //y轴最低
        float newWith = viewWith - (viewWith * 0.5f);    //x 轴宽
        int coordinateX;
        //画点
        for (int i = 0; i < score.length; i++) {
            Point point = new Point();
            coordinateX = (int) (newWith * ((float) (i) / (typeText.length - 1)) + (viewWith * 0.2f));
            point.x = coordinateX;
            if (score[i] > maxScore) {
                score[i] = maxScore;
            } else if (score[i] < minScore) {
                score[i] = minScore;
            }
            point.y = (int) (((float) (maxScore - score[i]) / (maxScore - minScore)) * (minScoreYCoordinate - maxScoreYCoordinate) + maxScoreYCoordinate);
            scorePoints.add(point);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWith = w;
        viewHeight = h;
        Log.e("han", "onSizeChanged");
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制xy 文字
        drawText(canvas);
        //x轴分断
        //画点
        drawPoint(canvas);
        //点之间连线
        drawBrokenLine(canvas);
    }

    //绘制折线穿过的点
    protected void drawPoint(Canvas canvas) {
        if (scorePoints == null) {
            return;
        }
        brokenPaint.setStrokeWidth(dipToPx(1));
        for (int i = 0; i < scorePoints.size(); i++) {
            brokenPaint.setColor(Color.parseColor(blueColor));
            brokenPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(5), brokenPaint);
            if (i == typeText.length - 1) {
                //默认虚线
                if (Math.abs(4 - score[i]) == 0) {
                    drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 1 / 6f, viewWith * 0.95f, viewHeight * 1 / 6f);
                } else if (Math.abs(4 - score[i]) == 1) {
                    drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 2 / 6f, viewWith * 0.95f, viewHeight * 2 / 6f);
                } else if (Math.abs(4 - score[i]) == 2) {
                    drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 3 / 6f, viewWith * 0.95f, viewHeight * 3 / 6f);
                } else if (Math.abs(4 - score[i]) == 3) {
                    drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 4 / 6f, viewWith * 0.95f, viewHeight * 4 / 6f);
                }
                brokenPaint.setColor(Color.parseColor(blueColorOut));
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(13), brokenPaint);
                brokenPaint.setColor(Color.parseColor(blueColor));
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(7), brokenPaint);
                //绘制浮动文本背景框
                drawFloatTextBackground(canvas, scorePoints.get(i).x + dipToPx(20), scorePoints.get(i).y);
                textPaint.setColor(0xffffffff);
                //绘制浮动文字
                canvas.drawText(String.valueOf(typeText[Math.abs(4 - score[i])]),
                        scorePoints.get(i).x + (dipToPx(50) - textSize) / 2 + dipToPx(28), scorePoints.get(i).y + textSize / 2, textPaint);

            }
        }
    }

    //绘制折线
    private void drawBrokenLine(Canvas canvas) {
        brokenPath.reset();
        brokenPaint.setColor(Color.parseColor(blueColor));
        brokenPaint.setStyle(Paint.Style.STROKE);
        if (score.length == 0) {
            return;
        }
        brokenPath.moveTo(scorePoints.get(0).x - 50, scorePoints.get(0).y);
        for (int i = 0; i < scorePoints.size(); i++) {
            brokenPath.lineTo(scorePoints.get(i).x, scorePoints.get(i).y);
        }
        canvas.drawPath(brokenPath, brokenPaint);
    }

    //绘制文本
    private void drawText(Canvas canvas) {
        textPaint.setTextSize(sp2px(10));
        textPaint.setColor(Color.parseColor(textGray));
        canvas.drawText(String.valueOf(typeText[0]), dipToPx(25), viewHeight * 1 / 6f + textSize * 0.25f, textPaint);
        canvas.drawText(String.valueOf(typeText[1]), dipToPx(25), viewHeight * 2 / 6f + textSize * 0.25f, textPaint);
        canvas.drawText(String.valueOf(typeText[2]), dipToPx(25), viewHeight * 3 / 6f + textSize * 0.25f, textPaint);
        canvas.drawText(String.valueOf(typeText[3]), dipToPx(25), viewHeight * 4 / 6f + textSize * 0.25f, textPaint);
        textPaint.setColor(Color.parseColor(textGray));
        float newWith = viewWith - (viewWith * 0.5f);
        float coordinateX;//分隔线X坐标
        textPaint.setTextSize(sp2px(10));
        textPaint.setStyle(Paint.Style.FILL);
        textSize = (int) textPaint.getTextSize();
        for (int i = 0; i < monthText.length; i++) {
            coordinateX = newWith * ((float) (i) / (typeText.length - 1)) + (viewWith * 0.2f);
            if (i == typeText.length - 1) {
                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setColor(Color.parseColor(blueColor));

            }
            //绘制y轴
            canvas.drawText(monthText[i], coordinateX, viewHeight * 5 / 6f + dipToPx(4) + textSize + dipToPx(5), textPaint);
        }
    }

    //绘制显示浮动文字的背景
    private void drawFloatTextBackground(Canvas canvas, int x, int y) {
        brokenPath.reset();
        brokenPaint.setColor(Color.parseColor(blueColor));
        brokenPaint.setStyle(Paint.Style.FILL);
        //绘制三角
        Point point = new Point(x, y);
        brokenPath.moveTo(point.x, point.y);
        point.x = point.x + dipToPx(7);
        point.y = point.y - dipToPx(3);
        brokenPath.lineTo(point.x, point.y);
        point.y = point.y + dipToPx(6);
        brokenPath.lineTo(point.x, point.y);
        brokenPath.lineTo(x, y);
        canvas.drawPath(brokenPath, brokenPaint);

        RectF rect1 = new RectF(x + dipToPx(7), y - dipToPx(10), x + dipToPx(50), y + dipToPx(10));
        // 绘制圆角矩形
        brokenPath.addRoundRect(rect1, 15, 15, Path.Direction.CCW);
        canvas.drawPath(brokenPath, brokenPaint);
    }

    /**
     * 画虚线
     *
     * @param canvas 画布
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终点X坐标
     * @param stopY  终点Y坐标
     */
    private void drawDottedLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 4));
        dottedPaint.setStrokeWidth(1);
        // 实例化路径
        Path mPath = new Path();
        mPath.reset();
        // 定义路径的起点
        mPath.moveTo(startX, startY);
        mPath.lineTo(stopX, stopY);
        canvas.drawPath(mPath, dottedPaint);

    }


    public void setScore(int[] score) {
        this.score = score;
        initData();
    }

    public void setMonthText(String[] monthText) {
        this.monthText = monthText;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public int sp2px(int spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
