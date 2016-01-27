package com.androidlab.qiao.qiao_widget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.androidlab.qiao.guillotineview.animtor.GuillotineAnimtor;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout guillotineView; //铡刀视图
    private ImageButton openButton; //开启按钮
    private ImageButton closeButton; //关闭按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        new GuillotineAnimtor.Builder()
            .setActionbar(toolbar)
                .setCloseButton(closeButton)
                .setOpenButton(openButton)
                .setGuillotineView(guillotineView)
                .build();

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        guillotineView = (FrameLayout) findViewById(R.id.guillotine_view);
        openButton = (ImageButton) findViewById(R.id.hamburger);
        closeButton = (ImageButton) findViewById(R.id.guillotine_hamburger);
    }
}
