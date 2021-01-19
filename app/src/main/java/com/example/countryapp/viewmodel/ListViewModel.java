package com.example.countryapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.countryapp.model.CountriesService;
import com.example.countryapp.model.CountryModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends ViewModel {

    // 사용자에게 보여줄 국가 데이터
    public MutableLiveData<List<CountryModel>> countries = new MutableLiveData<>();
    // 국가 데이터를 가져오는 것에 성공했는지를 알려주는 데이터
    public MutableLiveData<Boolean> countryLoadError = new MutableLiveData<>();
    // 로딩 중인지를 나타내는 데이터
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public CountriesService countriesService= CountriesService.getInstance();

    // os에 의해 앱의 프로세스가 죽거는 등의 상황에서
    // Single 객체를 가로채기 위함
    private CompositeDisposable disposable = new CompositeDisposable();

    // 뷰에서 데이터를 가져오기 위해 호출하는 함수
    public void refresh(){
        fetchCountries();
    }

    private void fetchCountries(){

        // 서버로부터 데이터를 받아오는 동안에 로딩 스피너를 보여주기 위
        loading.setValue(true);
        disposable.add(
                // countriesService.getCountries()를 Unit Test에서 다루기 힘듬 -> 서비스를 새로운 스레드에서 구하기 때문
                // exedcutor rules
                // 백그라운드 스레드를 호툴할 때 즉시 안드로이드 스케줄를 위한 같은 것을 반환한다.
                countriesService.getCountries() // Single<List<CountryModel>>를 반환한다.
                        .subscribeOn(Schedulers.newThread()) // 새로운 스레드에서 통신한다.
                        .observeOn(AndroidSchedulers.mainThread()) // 응답 값을 가지고 ui update를 하기 위해 필요함, 메인스레드와 소통하기 위
                        .subscribeWith(new DisposableSingleObserver<List<CountryModel>>() {
                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull List<CountryModel> countryModels) {
                                countries.setValue(countryModels);
                                countryLoadError.setValue(false);
                                loading.setValue(false);

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                countryLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                            }
                        })
        );

    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // 앱이 통신 중에 프로세스가 종료될 경우(앱이 destory됨)
        // 메모리 손실을 최소화 하기 위해 백그라운드 스레드에서 통신 작업을 중단한다.
        disposable.clear();
    }
}
