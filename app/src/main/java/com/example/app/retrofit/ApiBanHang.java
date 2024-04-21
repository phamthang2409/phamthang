package com.example.app.retrofit;

import io.reactivex.rxjava3.core.Observable;
import com.example.app.model.LoaiSpModel;
import com.example.app.model.SanPhamMoiModel;

import retrofit2.http.GET;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();
    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();
}
