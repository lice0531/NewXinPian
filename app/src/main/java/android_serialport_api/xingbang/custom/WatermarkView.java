package android_serialport_api.xingbang.custom;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * 水印
 */
public class WatermarkView extends View {
    private Paint paint;
    private String watermarkText = "工业电子雷管通用型起爆器应用软件";
    private float textSize = 40f; // 字体大小，可以根据需要调整

    public WatermarkView(Context context) {
        super(context);
        init();
    }

    public WatermarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WatermarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.LTGRAY); // 浅灰色
        paint.setAlpha(128); // 透明度不低于50%
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置旋转矩阵
        Matrix matrix = new Matrix();
        matrix.setRotate(45, getWidth() / 2, getHeight() / 2); // 旋转角度45度
        canvas.setMatrix(matrix);

        // 绘制水印文本
        canvas.drawText(watermarkText, 50, getHeight() / 2, paint); // 水印位置可以根据需要调整
    }
}
