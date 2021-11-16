package org.techtown.last_prototype.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.last_prototype.MainActivity;
import org.techtown.last_prototype.R;

public class SplashActivity extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT = 5000;
    View first, second,third,fourth,fifth,sixth;
    TextView a, slogan;
    Animation topAnimantion,bottomAnimation,middleAnimation;

    Handler handler=new Handler();

    private View view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view=(View)getLayoutInflater().inflate(R.layout.splash,null);
        setContentView(view);

        first = view.findViewById(R.id.first_line);
        second = view.findViewById(R.id.second_line);
        third = view.findViewById(R.id.third_line);
        fourth = view.findViewById(R.id.fourth_line);
        fifth = view.findViewById(R.id.fifth_line);
        sixth = view.findViewById(R.id.sixth_line);
        a = view.findViewById(R.id.a);
        slogan = view.findViewById(R.id.tagLine);

        //Animation Calls
        topAnimantion = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_animation);
        middleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.middle_animation);

        first.setAnimation(topAnimantion);
        second.setAnimation(topAnimantion);
        third.setAnimation(topAnimantion);
        fourth.setAnimation(topAnimantion);
        fifth.setAnimation(topAnimantion);
        sixth.setAnimation(topAnimantion);
        a.setAnimation(middleAnimation);
        slogan.setAnimation(bottomAnimation);



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);
    }
}
