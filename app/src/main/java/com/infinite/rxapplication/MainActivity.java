package com.infinite.rxapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btEmpty).setOnClickListener(this);
        findViewById(R.id.btDefer).setOnClickListener(this);
        findViewById(R.id.btBuffer).setOnClickListener(this);
        findViewById(R.id.btMerge).setOnClickListener(this);
        findViewById(R.id.btScan).setOnClickListener(this);
        findViewById(R.id.btFilter).setOnClickListener(this);
        findViewById(R.id.btTake).setOnClickListener(this);
        findViewById(R.id.btElementAt).setOnClickListener(this);
        findViewById(R.id.btCache).setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                PublishSubject<Integer> publishSubject = PublishSubject.create();  //PublishSubject发送订阅后的事件给所有的observer
//                ReplaySubject<Integer> publishSubject = ReplaySubject.create();  //ReplaySubject会缓存所有的事件给所有的observer
//                ReplaySubject<Integer> publishSubject = ReplaySubject.createWithSize(2);  //ReplaySubject会缓存所有的事件给所有的observer，这里缓存两个
                BehaviorSubject<Integer> publishSubject = BehaviorSubject.create();  //BehaviorSubject只发送订阅后的最后一个事件给所有的observer
                publishSubject.onNext(1);
                publishSubject.onNext(2);
                publishSubject.onNext(3);
                publishSubject.onNext(4);
                publishSubject.onNext(5);

                publishSubject.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept 1: " + integer);
                    }
                });
                publishSubject.onNext(6);
                publishSubject.onNext(7);
                publishSubject.onNext(8);
                publishSubject.onNext(9);

                publishSubject.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept 2: " + integer);
                    }
                });
                publishSubject.onNext(10);
                publishSubject.onNext(11);
                publishSubject.onNext(12);
            }
        }, 4000);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btCache:
                //模拟三级缓存的数据
                final String[] data = new String[]{"", "", ""};
                Observable<String> memery = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        String d = data[0];
                        Log.i(TAG, "正在从内存获取: ");
                        if (!TextUtils.isEmpty(d)) {
                            e.onNext(d);
                        }
                        e.onComplete();
                    }
                });

                Observable<String> local = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        String d = data[1];
                        Log.i(TAG, "正在从本地获取: ");
                        if (!TextUtils.isEmpty(d)) {
                            e.onNext(d);
                        }
                        e.onComplete();
                    }
                });

                Observable<String> network = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        String d = data[2];
                        Log.i(TAG, "正在从网络获取: ");
                        if (!TextUtils.isEmpty(d)) {
                            e.onNext(d);
                        }
                        e.onComplete();
                    }
                });

                Observable
                        .concat(memery, local, network)
                        .firstOrError()  //总是获取第一个，如果都没有就onError
                        .subscribe(new SingleObserver<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                Log.i(TAG, "onSubscribe: ");
                            }

                            @Override
                            public void onSuccess(@NonNull String s) {
                                Log.i(TAG, "onSuccess: " + s);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.i(TAG, "onError: ");
                            }
                        });
                break;
            case R.id.btElementAt:
                Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        .elementAt(6)  //取第7个值
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Log.i(TAG, "elementAt onSubscribe: " + integer);
                            }
                        });
                break;
            case R.id.btTake:
                Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//                        .take(3)    //取前3个值
//                        .skip(2)        //跳过开始的2个
                        .skipLast(1)    //跳过最后1个
//                        .takeLast(0)   //取最后0个值
//                        .first(200)     //取第1个值，如果没有则发送200
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Log.i(TAG, "take onSubscribe: " + integer);
                            }
                        });
                break;
            case R.id.btFilter:
                Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(@NonNull Integer integer) throws Exception {
                                return integer % 3 == 0;  //过滤出3的倍数
                            }
                        })
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Log.i(TAG, "filter onSubscribe: " + integer);
                            }
                        });
                break;
            case R.id.btScan:
                Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        .scan(new BiFunction<Integer, Integer, Integer>() {
                            @Override
                            public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
//                                return integer + integer2;  //这里是累加
                                return integer2 - integer;     //这里是算差值
                            }
                        })
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Log.i(TAG, "scan onSubscribe: " + integer);
                            }
                        });
                break;
            case R.id.btMerge:
                Observable<Integer> observable1 = Observable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                Observable<String> observable2 = Observable.fromArray("a", "b", "c", "d", "e", "f");
                //merge会按照顺序发射observable1的所有值，然后按顺序发射observable2的值
                Observable
                        .merge(observable1, observable2)
                        .subscribe(new Consumer<Serializable>() {
                            @Override
                            public void accept(@NonNull Serializable serializable) throws Exception {
                                Log.i(TAG, "merge onSubscribe: " + serializable);
                            }
                        });
                //zip是同时发射observable1和observable2的值，如果出现了数据项不等的情况，合并的数据项以最小数据队列为准
                Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                        return "The result is:" + integer + "__" + s;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "zip onSubscribe: " + s);
                    }
                });
                break;
            case R.id.btBuffer:
                //buffer操作符在存储了5个Observable后一起发射
                Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS).buffer(5).subscribe(new Observer<List<Long>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "observable1 onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<Long> longs) {
                        for (Long aLong : longs) {
                            Log.i(TAG, "observable1 onSubscribe: " + aLong);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "observable1 onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "observable1 onComplete: ");
                    }
                });
                break;
            case R.id.btDefer:
                //每个Observer自订阅开始都能收到Observable从头开始发送的数据
                final Observable<Long> observable = Observable.defer(new Callable<ObservableSource<? extends Long>>() {
                    @Override
                    public ObservableSource<? extends Long> call() throws Exception {
                        return Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS);
                    }
                });

                observable.subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "observable1 onSubscribe: ");
                    }

                    @Override
                    public void onNext(Long o) {
                        Log.i(TAG, "observable1 onNext: " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "observable1 onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "observable1 onComplete: ");
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        observable.subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.i(TAG, "observable2 onSubscribe: ");
                            }

                            @Override
                            public void onNext(Long o) {
                                Log.i(TAG, "observable2 onNext: " + o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "observable2 onError: ");
                            }

                            @Override
                            public void onComplete() {
                                Log.i(TAG, "observable2 onComplete: ");
                            }
                        });
                    }
                }, 2000);

                break;
            case R.id.btEmpty:
                //Observable只会发射onComplete事件，不会走onNext事件
                Observable.empty().subscribe(new Observer<Object>() {
                    //Observable.never().subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.i(TAG, "onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
                break;

        }
    }
}
