package org.techtown.last_prototype.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
public class PikassoView extends View {
    private int picX=-1, picY=-1, image_state=0;
    public static final float TOUCH_TOLERANCE=10;
    private int container_idx=0;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint ShapePaint;
    private Paint paintLine;
    private Paint erasePaintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    int startX = -1, startY = -1, stopX = -1, stopY = -1;
    private int shape_state_rect =0, shape_state_circle=0, shape_state_line=0, erase_state=0;
    final static int LINE = 3, CIRCLE = 2, RECTANGLE = 1;
    static int curShape = RECTANGLE;
    static int color = Color.BLACK;
    private Bitmap ImageBitmap;
    private HashMap<Integer, Container> shapeHash;
    private Integer hashID=0;
    Myshape currentShape = null;
    private Paint DefaultPaint;
    Container[] container;
    ArrayList<Myshape> MyshapeArrayList = new ArrayList<>();
    public PikassoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void init(){
        container=new Container[100000];
        paintScreen=new Paint();
        Rect rect[]=new Rect[5];
        paintLine=new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(7);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        erasePaintLine=new Paint();
        erasePaintLine.setAntiAlias(true);
        erasePaintLine.setColor(Color.BLACK);
        erasePaintLine.setStyle(Paint.Style.STROKE);
        erasePaintLine.setStrokeWidth(7);
        erasePaintLine.setStrokeCap(Paint.Cap.ROUND);

        ShapePaint = new Paint();
        ShapePaint.setAntiAlias(true);
        ShapePaint.setColor(Color.BLACK);
        ShapePaint.setStyle(Paint.Style.STROKE);
        ShapePaint.setStrokeWidth(7);
        ShapePaint.setStrokeCap(Paint.Cap.ROUND);
        shapeHash=new HashMap();
        pathMap=new HashMap<>();
        previousPointMap=new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas=new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }
    private void drawShape(Myshape currentShape, Canvas canvas, Paint paint) {
        switch (currentShape.shape) {
            case LINE:
                canvas.drawLine(currentShape.startX, currentShape.startY,
                        currentShape.stopX, currentShape.stopY, paint);
                break;
            case CIRCLE:
                int radius = (int) Math.sqrt(Math.pow(currentShape.stopX - currentShape.startX, 2) +
                        Math.pow(currentShape.stopY - currentShape.startY, 2));
                canvas.drawCircle(currentShape.startX, currentShape.startY, radius, paint);
                break;
            case RECTANGLE:
                Rect rect = new Rect(currentShape.startX, currentShape.startY,
                        currentShape.stopX, currentShape.stopY);
                canvas.drawRect(rect, paint);
                break;
        }
        for(Integer k: pathMap.keySet())
        {
            bitmapCanvas.drawPath(pathMap.get(k), paintLine);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap,0,0,paintScreen);

        for(Integer key:shapeHash.keySet())
        {
            drawShape(shapeHash.get(key).current_Shape,bitmapCanvas,shapeHash.get(key).shape_Paint);
            shapeHash.remove(key);
        }
            for(Integer key: pathMap.keySet())
            {
                bitmapCanvas.drawPath(pathMap.get(key), paintLine);
            }
        if (currentShape != null) {
            drawShape(currentShape, canvas, ShapePaint);
        }
    }
    private void startActivityForResult(Intent intent, int requestCode) {

    }
    public void setEraseColor(int col)
    {
        erasePaintLine.setColor(col);
    }
    public void eraseState()
    {
        erase_state=1;
    }

    public void eraseStateEnd()
    {
        erase_state=0;
    }
    public void setImage()
    {
        if(ImageBitmap!=null) {
            picX=picX-(ImageBitmap.getWidth()/2);
            picY=picY-(ImageBitmap.getHeight())/2;
            bitmapCanvas.drawBitmap(ImageBitmap,picX,picY,null);
        }
        ImageBitmap=null;
    }
    public void setImage(Bitmap img, Paint p)
    {
        int picX=(this.getWidth()-img.getWidth())/2;
        int picY=(this.getHeight()-img.getHeight())/2;
        bitmapCanvas.drawBitmap(img,picX,picY,p);
    }
    public void setImage(Bitmap img, InputStream in)
    {
        ImageBitmap=img;
        image_state=0;
    }
    public void setImage(Bitmap img, InputStream in, int x, int y)
    {

        int picX=x;
        int picY=y;
        bitmapCanvas.drawBitmap(img,picX,picY,null);
    }
    int idxp=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getActionMasked();
        int actionIndex=event.getActionIndex();

        if(shape_state_rect ==0 && shape_state_line==0 && shape_state_circle==0)
        {
            if(action==MotionEvent.ACTION_DOWN|| action== MotionEvent.ACTION_POINTER_UP)
            {
                if(image_state==0)
                {
                    picX=(int)event.getX(actionIndex);
                    picY=(int)event.getY(actionIndex);

                    image_state=1;
                    setImage();

                }
                    touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
            }
            else if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP)
            {
                    touchEnded(event.getPointerId(actionIndex));
            }
            else
            {
                touchMoved(event);
            }
        }
        if((shape_state_rect ==1 || shape_state_circle==1 || shape_state_line==1)&& erase_state==0)
        {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(image_state==0)
                    {
                        picX=(int)event.getX(actionIndex);
                        picY=(int)event.getY(actionIndex);

                        image_state=1;

                        setImage();
                    }
                        currentShape = new Myshape(curShape);
                        currentShape.color = color;
                        currentShape.startX = (int) event.getX();
                        currentShape.startY = (int) event.getY();

                    break;
                case MotionEvent.ACTION_MOVE:
                    currentShape.stopX = (int) event.getX();
                    currentShape.stopY = (int) event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    currentShape.stopX = (int) event.getX();
                    currentShape.stopY = (int) event.getY();
                    MyshapeArrayList.add(currentShape);
                    container[container_idx]=new Container(currentShape,ShapePaint,curShape);
                    shapeHash.put(hashID,container[container_idx]);
                    currentShape = null;
                    this.invalidate();
                    break;
            }
        }
    hashID++;
        container_idx++;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                stopX = (int) event.getX();
                stopY = (int) event.getY();
                this.invalidate();
                break;
        }
        return true;
    }
    private void touchMoved(MotionEvent event)
    {
        for(int i=0;i<event.getPointerCount();i++)
        {
            int pointerId=event.getPointerId(i);
            int pointerIndex=event.findPointerIndex(pointerId);
            if(pathMap.containsKey(pointerId))
            {
                float newX=event.getX(pointerIndex);
                float newY=event.getY(pointerIndex);

                Path path=pathMap.get(pointerId);
                Point point=previousPointMap.get(pointerId);
                float deltaX=Math.abs(newX-point.x);
                float deltaY=Math.abs(newY-point.y);
                if(deltaX>=TOUCH_TOLERANCE ||deltaY>=TOUCH_TOLERANCE)
                {
                    path.quadTo(point.x,point.y,(newX+point.x)/2,(newY+point.y)/2);
                    point.x=(int)newX;
                    point.y=(int)newY;
                }
            }
        }
    }
    public int tempWidth=20;
    public void setShapeWidth(int width)
    {
        ShapePaint.setStrokeWidth(width);
    }
    public void setDrawingColor(int col)
    {
        paintLine.setColor(col);
    }
    public void setDrawingColor_shape(int col)
    {
        ShapePaint.setColor(col);
    }
    public int getDrawingColor(){
        return paintLine.getColor();
    }
    public void setLineWidth(int width)
    {
        paintLine.setStrokeWidth(width);
    }
    public int getLineWidth()
    {
        return (int)paintLine.getStrokeWidth();
    }
    public void tempSetEraserLineWidth(int width){tempWidth=width;}
    public int tempGetEraserLineWidth()
    {
        return tempWidth;
    }
    public Paint getDefaultColor()
    {
        if(container[0]!=null)
            return container[0].shape_Paint;
        else
            return ShapePaint;
    }
    public void drawRectangle()
    {
        shape_state_rect =1;
        curShape=RECTANGLE;
    }
    public void drawCircle(){
        shape_state_circle=1;
        curShape=CIRCLE;
    }
    public void drawLine(){
        shape_state_line=1;
        curShape=LINE;
    }
    public void draw()
    {
            shape_state_rect =0;
            shape_state_circle=0;
            shape_state_line=0;
    }
    public void clear()
    {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        MyshapeArrayList.clear();
        shapeHash.clear();
        invalidate();
    }
    private void touchEnded(int pointerId)
    {
        Path path=pathMap.get(pointerId);//get the correspoding Path
        bitmapCanvas.drawPath(path, paintLine);//draw to bitmap Canvas
        path.reset();
    }
    private void touchStarted(float x, float y, int pointerId)
    {
        Path path;
        Point point;
        System.out.println("touchStarted 함수 내에서 pointerID: "+pointerId);
        if(pathMap.containsKey(pointerId) && shape_state_rect ==0)
        {
            path=pathMap.get(pointerId);
            point=previousPointMap.get(pointerId);
        }
        else
        {
            path=new Path();
            pathMap.put(pointerId,path);
            point=new Point();
            previousPointMap.put(pointerId,point);
        }
        if(shape_state_rect ==1)
        {
            point=new Point();
        }
        path.moveTo(x, y);
        point.x=(int)x;
        point.y=(int)y;
    }
    @SuppressLint("WrongThread")
    public void saveToInternalStorage(){
        ContextWrapper cw=new ContextWrapper(getContext());
        String filename="Pikasso"+System.currentTimeMillis();
        File directory=cw.getDir("imagedir",Context.MODE_PRIVATE);
        File mypath=new File(directory, filename+".jpg");
        FileOutputStream fos=null;
        Log.d("myPATH:", String.valueOf(mypath));
        try{
            fos=new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            try{
                fos.flush();
                fos.close();
                Toast message=Toast.makeText(getContext(),"Image Saved +"+directory.getAbsolutePath(),Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER,message.getXOffset()/2,message.getYOffset()/2);
                message.show();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
   public void loadImageFromStorage(String path,String child, int state,Paint paint)
    {
        try{
            if(state==0)
            {
                File f=new File(path, child);
                Bitmap b= BitmapFactory.decodeStream(new FileInputStream(f));
                int picX=(this.getWidth()-b.getWidth())/2;
                int picY=(this.getHeight()-b.getHeight())/2;
                bitmapCanvas.drawBitmap(b,picX,picY,paint);
                Toast.makeText(getContext(), "File Selected", Toast.LENGTH_SHORT).show();
            }
            else
            {
                File f=new File(path, child);
                f.delete();
                Toast.makeText(getContext(), "file deleted", Toast.LENGTH_SHORT).show();
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private static class Myshape {
            int shape, startX, startY, stopX, stopY, color;
        public Myshape(int shape) {
            this.shape = shape;
        }
    }
    static int class_id=0;
    class Container
    {
        public Myshape current_Shape;
        public Paint shape_Paint;
        public int id;
        public int shape;
        public Container(Myshape cs, Paint p, int s)
        {
            shape=s;
            id=class_id++;
            current_Shape=cs;
            shape_Paint=new Paint();
            shape_Paint.setAntiAlias(true);
            shape_Paint.setColor(p.getColor());
            shape_Paint.setStyle(p.getStyle());
            shape_Paint.setStrokeWidth(p.getStrokeWidth());
            shape_Paint.setStrokeCap(p.getStrokeCap());
        }
        public Paint getContainerPaint()
        {
            return shape_Paint;
        }
    }
}