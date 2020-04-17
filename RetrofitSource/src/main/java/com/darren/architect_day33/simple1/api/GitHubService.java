package com.darren.architect_day33.simple1.api;

import com.darren.architect_day33.simple1.bean.Repo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
  @GET("users/{user}/repos")
  Observable<List<Repo>> listReposObservable(@Path("user") String user);
  @GET("users/{user}/repos")
  Call<ResponseBody> listReposResponseBody(@Path("user") String user);
}