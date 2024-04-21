package com.example.app.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import android.widget.ViewFlipper;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.adapter.LoaiSpAdapter;
import com.example.app.adapter.SanPhamMoiAdapter;
import com.example.app.model.LoaiSp;
import com.example.app.model.LoaiSpModel;
import com.example.app.model.SanPhamMoi;
import com.example.app.model.SanPhamMoiModel;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.google.android.material.navigation.NavigationView;


import java.sql.Connection;
import java.time.Instant;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        Anhxa();
        ActionBar();

        if (isConnected(this)){
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
        }else {
            Toast.makeText(getApplicationContext(), "khong co internet", Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void getSpMoi() {
         compositeDisposable.add(apiBanHang.getSpMoi()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(
                         sanPhamMoiModel -> {
                             if (sanPhamMoiModel.isSuccess()){
                                 mangSpMoi = sanPhamMoiModel.getResult();
                                 spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                 recyclerViewManHinhChinh.setAdapter(spAdapter);

                             }
                         },
                         throwable -> {
                             Toast.makeText(getApplicationContext(), "Không kết nối được"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                         }
                 ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()){
                                mangloaisp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }


    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://www.fahasa.com/tan-viet?fhs_campaign=homepageslider3");
        mangquangcao.add("https://www.fahasa.com/zen-books?fhs_campaign=homepageslider4");
        mangquangcao.add("https://www.fahasa.com/do-choi?fhs_campaign=homepageslider5");
        for (int i = 0; i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void    Anhxa() {
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        // khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();

    }
    private boolean isConnected (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}