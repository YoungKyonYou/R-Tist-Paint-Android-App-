package org.techtown.last_prototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.techtown.last_prototype.view.PikassoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int tempColor=-1;
    private int tempWidth=-1;
    private int centerX,centerY;
    private Button acceptBtn;
    private Bitmap setBitmap;
    private Paint paintScreen;
    private Integer PICK_IMAGE = 10;
    private PikassoView pikassoView;
    private AlertDialog.Builder currentAlertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private AlertDialog colorDialog;
    private AlertDialog photoshopDialog;
    private View colorView;
    private int testX=-1, testY=-1;
    ImageButton ibBright, ibDark;
    static float   angle=0, color=1, satur=1, sepia=1, binary=1, invert=1,ab=1,ap=1;
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE2 = 5;
    private List<String> filesNameList;
    private ImageView iv;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton edit, photo, circle, rectangle, line;
    private myGraphicView graphicView;
    private Paint myGraphicViewPaint;
    private View gView;
    private LayoutInflater inflater;
    private Bitmap imgBit;
    private InputStream ins;
    LinearLayout pictureLayout;
    public Bitmap myGraphicBitmap;
    private int LineWidthBeforeErase=-1, LineColorBeforeErase=-1;
    public static int init_state=0;
    public View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("R-tist");
        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        edit = findViewById(R.id.floatingActionItemEdit);
        photo = findViewById(R.id.floatingActionItemPhoto);
        line = findViewById(R.id.floatingActionItemLine);
        circle=findViewById(R.id.floatingActionItemCirc);
        rectangle=findViewById(R.id.floatingActionItemRect);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paint pt;
                Toast.makeText(getApplicationContext(), "imageView", Toast.LENGTH_SHORT).show();
                if(tempWidth!=-1 || tempColor!=-1 || pikassoView.getDrawingColor()==Color.WHITE)
                {
                    if(tempColor==Color.WHITE)
                    {
                        pikassoView.init();
                    }
                    else {
                        pikassoView.setDrawingColor(tempColor);
                        pikassoView.setLineWidth(tempWidth);
                    }
                }
                pikassoView.draw();
                /*if(init_state==0)
                {
                    pikassoView.init();
                    init_state=1;
                }*/
                floatingActionMenu.close(false);

            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "photo", Toast.LENGTH_SHORT).show();

                pikassoView.draw();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
                floatingActionMenu.close(false);

            }
        });
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pikassoView.drawLine();
                pikassoView.eraseStateEnd();
                floatingActionMenu.close(false);
            }
        });
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pikassoView.drawCircle();
                pikassoView.eraseStateEnd();
                floatingActionMenu.close(false);
            }
        });
        rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pikassoView.drawRectangle();
                pikassoView.eraseStateEnd();
                floatingActionMenu.close(false);
            }
        });

        pikassoView = findViewById(R.id.vieww);//activity_main.xml에 보면 아이디를 view로 해놈

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imgUri;
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    imgUri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(imgUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String imgPath = cursor.getString(column_index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                try {
                    ins = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imgBit = BitmapFactory.decodeStream(ins);
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pikassoView.setImage(imgBit, ins);
            }
        }
        else if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearId:
                LineWidthBeforeErase=pikassoView.getLineWidth();
                LineColorBeforeErase=pikassoView.getDrawingColor();
                pikassoView.clear();
                pikassoView.setLineWidth(LineWidthBeforeErase);
                pikassoView.setDrawingColor(LineColorBeforeErase);
                break;
            case R.id.saveId:
                pikassoView.saveToInternalStorage();
                break;
            case R.id.colorId:
                showColorDialog();
                break;
            case R.id.lineWidth:
                pikassoView.setDrawingColor(Color.argb(255,0,0,0));
                showLineWidthDialog();
                break;
            case R.id.eraseId:
                LineColorBeforeErase=pikassoView.getDrawingColor();
                LineWidthBeforeErase=pikassoView.getLineWidth();
                pikassoView.setEraseColor(Color.argb(255,0,0,0));
                pikassoView.eraseState();
                pikassoView.draw();
                eraseCanvas();
                break;
            case R.id.openId:
                ab=1;
                ap=1;
                invert=1;
                binary=1;
                sepia=1;
                color=1;
                satur=1;
                getFileName();
                break;
        }
        if (item.getItemId() == R.id.clearId) {
            pikassoView.setDrawingColor(Color.BLACK);
            pikassoView.eraseStateEnd();
            pikassoView.clear();
        }
        return super.onOptionsItemSelected(item);
    }
    static int imageIndex=0;
    int signal=0;
    String str;
    void previousBtn()
    {
            imageIndex--;
        if(imageIndex<0)
        {
            str=filesNameList.get(filesNameList.size()-1);
            imageIndex=filesNameList.size()-1;
        }
        else if(imageIndex==0)
        {
            str=filesNameList.get(0);
            imageIndex=filesNameList.size();
        }
        else{
            str=filesNameList.get(imageIndex%filesNameList.size());
        }
        try {
               Uri uri = Uri.parse("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
               iv.setImageURI(uri);
            Bitmap bm = BitmapFactory.decodeFile("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
            graphicView.getBitmap(bm);
           }catch (Exception e){
               e.printStackTrace();
           }
    }
    void nextBtn()
    {
        imageIndex++;
        if(imageIndex<=0)
        {
            str=filesNameList.get(0);
            imageIndex=0;
        }
        else{
            str=filesNameList.get(imageIndex%filesNameList.size());
        }
        try {
            Uri uri = Uri.parse("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
            iv.setImageURI(uri);
            Bitmap bm = BitmapFactory.decodeFile("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
            graphicView.getBitmap(bm);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onButtonPClicked(View v)
    {
        previousBtn();
    }

    public void onButtonTClicked(View v)
    {
        imageIndex=0;
        if(signal==0)
        {
            try {
                Uri uri = Uri.parse("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
                iv.setImageURI(uri);
                Bitmap bm = BitmapFactory.decodeFile("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
                graphicView.getBitmap(bm);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(str==null)
                str=filesNameList.get(0);
            pikassoView.loadImageFromStorage("/data/user/0/org.techtown.last_prototype/app_imagedir/",str,1,myGraphicViewPaint);
        }
        signal=0;
        colorDialog.dismiss();
    }
    public void onButtonSClicked(View v)
    {
        if(signal==0)
        {
            str=filesNameList.get(imageIndex%filesNameList.size());
            try {
                Uri uri = Uri.parse("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
                iv.setImageURI(uri);
                Bitmap bm = BitmapFactory.decodeFile("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
                graphicView.getBitmap(bm);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("파일 이름: "+str);
            pikassoView.loadImageFromStorage("/data/user/0/org.techtown.last_prototype/app_imagedir/",str,0,myGraphicViewPaint);
            angle=0;
        }
        signal=0;
        imageIndex=0;
        colorDialog.dismiss();
    }
    public void onButtonNClicked(View v)
    {
        nextBtn();
    }
    void getFileName()
    {
         view = getLayoutInflater().inflate(R.layout.open_dialog, null);
        currentAlertDialog = new AlertDialog.Builder(this);
        ContextWrapper c=new ContextWrapper(this);
        String path=c.getDir("imagedir",MODE_PRIVATE)+"";
        File directory=new File(path);
        File[] files=directory.listFiles();
        filesNameList=new ArrayList<>();
        for(int i=0;i<files.length;i++)
            filesNameList.add(files[i].getName());
        for(int i=0;i<filesNameList.size();i++) {
            String d =filesNameList.get(i);
        }
        pictureLayout = view.findViewById(R.id.pictureLayout);
        graphicView = new myGraphicView(this);
        pictureLayout.addView(graphicView);
        iv=view.findViewById(R.id.imageView);

        ImageButton p=view.findViewById(R.id.previousId);
        ImageButton n=view.findViewById(R.id.nextId);
        ImageButton s=view.findViewById(R.id.selectId);
        ImageButton cbtn=view.findViewById(R.id.cBtn);

        Button b1, b2,b3,b4,b5,b6,b7;

        b1=view.findViewById(R.id.b1);
        b2=view.findViewById(R.id.b2);
        b3=view.findViewById(R.id.b3);
        b4=view.findViewById(R.id.b4);
        b5=view.findViewById(R.id.b5);
        b6=view.findViewById(R.id.b6);
        b7=view.findViewById(R.id.b7);

        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog.dismiss();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                satur=100;
                sepia=1;
                binary=1;
                color=1;
                graphicView.invalidate();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                satur=200;
                sepia=1;
                binary=1;
                color=1;
                graphicView.invalidate();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sepia=100;
                color=1;
                binary=1;
                satur=1;
                graphicView.invalidate();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab=1;
                ap=1;
                invert=1;
                binary=100;
                sepia=1;
                color=1;
                satur=1;
                graphicView.invalidate();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab=1;
                ap=1;
                invert=100;
                binary=1;
                sepia=1;
                color=1;
                satur=1;
                graphicView.invalidate();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab=100;
                ap=1;
                invert=1;
                binary=1;
                sepia=1;
                color=1;
                satur=1;
                graphicView.invalidate();
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab=1;
                ap=100;
                binary=1;
                sepia=1;
                color=1;
                satur=1;
                graphicView.invalidate();
            }
        });
        ibBright = view.findViewById(R.id.ibBright);
        ibBright.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                ab=1;
                ap=1;
                color += 0.2f;
                binary=1;
                sepia=1;
                satur=1;
                graphicView.invalidate();
            }
        });

        ibDark = view.findViewById(R.id.ibDark);
        ibDark.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                ab=1;
                ap=1;
                binary=1;
                sepia=1;
                satur=1;
                color -= 0.2f;
                graphicView.invalidate();
            }
        });


        if(filesNameList.isEmpty())
        {
            Toast.makeText( getApplicationContext(), "File is Empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String str=filesNameList.get(0);
            try {
                Uri uri = Uri.parse("/data/user/0/org.techtown.last_prototype/app_imagedir/"+ str);
                  iv.setImageURI(uri);
                graphicView.invalidate();
            }catch (Exception e){
                e.printStackTrace();
            }

            currentAlertDialog.setView(view);
            currentAlertDialog.setTitle("Choose Image");
            colorDialog = currentAlertDialog.create();
            colorDialog.show();
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getActionMasked();//event type; 액션인데 업인지 다운인지 정보 알려줌
        int actionIndex=event.getActionIndex();//pointer ( finger, mouse..) 손가락이 어딨나
            if(action==MotionEvent.ACTION_DOWN|| action== MotionEvent.ACTION_POINTER_UP)
            {
               testX=(int)event.getX(actionIndex);
               testY=(int)event.getY(actionIndex);
               System.out.println("x 좌표 값: "+testX+"  y 좌표 값: "+testY);
            }
            else if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP)
            {

            }
        return true;
    }

    void eraseCanvas()
    {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.eraser_dialog, null);
        final SeekBar widthSeekbar = view.findViewById(R.id.widthDseekBar);
        Button setLineWidthButton = view.findViewById(R.id.widthDialogButton);
        widthImageView=view.findViewById(R.id.imageViewId);

        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pikassoView.setLineWidth(widthSeekbar.getProgress()*2);//수치 반환
                pikassoView.tempSetEraserLineWidth(widthSeekbar.getProgress());
                dialogLineWidth.dismiss();
                currentAlertDialog=null;
                pikassoView.setDrawingColor(Color.argb(255,255,255,255));

            }
        });
        widthSeekbar.setOnSeekBarChangeListener(widthSeekEraserChange);//디폴트 글씨 크기 바꿀때 생기는 라인
        widthSeekbar.setProgress(pikassoView.tempGetEraserLineWidth());

        currentAlertDialog.setView(view);
        dialogLineWidth=currentAlertDialog.create();
        dialogLineWidth.setTitle("Set Eraser Width");
        dialogLineWidth.show();

    }
    void showColorDialog()
    {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        alphaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);


        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);


        int color = pikassoView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        pikassoView.setDrawingColor(Color.argb(
                        alphaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                ));
                        pikassoView.setDrawingColor_shape(Color.argb(
                                alphaSeekBar.getProgress(),
                                redSeekBar.getProgress(),
                                greenSeekBar.getProgress(),
                                blueSeekBar.getProgress()));
                        pikassoView.setDrawingColor(Color.argb(
                                alphaSeekBar.getProgress(),
                                redSeekBar.getProgress(),
                                greenSeekBar.getProgress(),
                                blueSeekBar.getProgress()));
                        tempColor=pikassoView.getDrawingColor();
                colorDialog.dismiss();

            }
        });

        currentAlertDialog.setView(view);
        currentAlertDialog.setTitle("Choose Color");
        colorDialog = currentAlertDialog.create();
        colorDialog.show();


    }

    void showLineWidthDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        final SeekBar widthSeekbar = view.findViewById(R.id.widthDseekBar);
        Button setLineWidthButton = view.findViewById(R.id.widthDialogButton);
        widthImageView=view.findViewById(R.id.imageViewId);
        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempColor==-1)
                    tempColor=Color.BLACK;
                pikassoView.setDrawingColor(tempColor);
                pikassoView.setLineWidth(widthSeekbar.getProgress());
                tempWidth=pikassoView.getLineWidth();
                pikassoView.setShapeWidth(widthSeekbar.getProgress());
                dialogLineWidth.dismiss();
                currentAlertDialog=null;
            }
        });

        widthSeekbar.setOnSeekBarChangeListener(widthSeekBarChange);
        widthSeekbar.setProgress(pikassoView.getLineWidth());


        currentAlertDialog.setView(view);
        dialogLineWidth=currentAlertDialog.create();
        dialogLineWidth.setTitle("Set Line Width");
        dialogLineWidth.show();

    }
    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            pikassoView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

           colorView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private SeekBar.OnSeekBarChangeListener widthSeekEraserChange=new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap=Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Paint p=new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.BLACK);
            p.setStrokeCap(Paint.Cap.ROUND);
            bitmap.eraseColor(Color.WHITE);
            canvas.drawCircle(220,50,progress,p);
            widthImageView.setImageBitmap(bitmap);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private SeekBar.OnSeekBarChangeListener widthSeekBarChange=new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap=Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Paint p=new Paint();
            p.setColor(pikassoView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);
            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30,50,370,50,p);
            widthImageView.setImageBitmap(bitmap);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    public class myGraphicView extends View {
         private Bitmap bitMap;
        public myGraphicView(Context context) {
            super(context);
            init();
        }
        public void init()
        {
            paintScreen=new Paint();
            this.invalidate();
        }
        void getBitmap(Bitmap bm)
        {
         bitMap=bm;
         this.invalidate();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            BitmapDrawable d = (BitmapDrawable)((ImageView)view.findViewById(R.id.imageView)).getDrawable();
            myGraphicBitmap=d.getBitmap();
            centerX = this.getWidth() / 2;
            centerY = this.getHeight() / 2;
            int picX = (this.getWidth() - myGraphicBitmap.getWidth()) / 2;
            int picY = (this.getHeight() - myGraphicBitmap.getHeight()) / 2;
            myGraphicViewPaint = new Paint();
            float[] array =   {     color,  0,      0,      0,      0,
                    0,      color,  0,      0,      0,
                    0,      0,      color,  0,      0,
                    0,      0,      0,      1,      0    };

            ColorMatrix cm = new ColorMatrix(array);
            myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter(cm));
            if (satur == 100) {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        1,  0,      0,      0,      0,
                        0,      1,  0,      0,      0,
                        0,      0,      1,  0,      0,
                        0,      0,      0,      1,      0 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            else if(satur==200)
            {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0, 0, 0, 1, 0 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));

            }
            else if(sepia==100)
            {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        1, 0,   0, 0, 0,
                        0, 1,   0, 0, 0,
                        0, 0, (float)0.8, 0, 0,
                        0, 0,   0, 1, 0 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            else if(binary==100)
            {
                float m = 255f;
                float t = -255*128f;
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        m, 0, 0, 1, t,
                        0, m, 0, 1, t,
                        0, 0, m, 1, t,
                        0, 0, 0, 1, 0 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            else if(invert==100)
            {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        -1,  0,  0,  0, 255,
                        0, -1,  0,  0, 255,
                        0,  0, -1,  0, 255,
                        0,  0,  0,  1,   0 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            else if(ab==100)
            {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        0,    0,    0, 0,   0,
                        0.3f,    0,    0, 0,  50,
                        0,    0,    0, 0, 255,
                        0.2f, 0.4f, 0.4f, 0, -30 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            else if(ap==100)
            {
                ColorMatrix cm2 = new ColorMatrix(new float[]{
                        0,    0,    0, 0, 255,
                        0,    0,    0, 0,   0,
                        0.2f,    0,    0, 0,  50,
                        0.2f, 0.2f, 0.2f, 0, -20 });
                myGraphicViewPaint.setColorFilter(new ColorMatrixColorFilter((cm2)));
            }
            binary=1;
            sepia=1;
            canvas.drawBitmap(myGraphicBitmap, picX, picY, myGraphicViewPaint);
        }
    }
}
