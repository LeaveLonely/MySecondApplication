package com.example.jiaw2.mysecondapplication.Activity;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;


import com.example.jiaw2.mysecondapplication.R;
import com.example.jiaw2.mysecondapplication.calender.CalendarItemAdapter;

import com.example.jiaw2.mysecondapplication.calender.CustomCalendarItemModel;
import com.example.jiaw2.mysecondapplication.calender.DayNewsListAdapter;
import com.example.jiaw2.mysecondapplication.calender.GitHubService;
import com.example.jiaw2.mysecondapplication.calender.NewsService;

import com.example.jiaw2.mysecondapplication.calender.retrofit.RetrofitProvider;
import com.kelin.calendarlistview.library.CalendarHelper;
import com.kelin.calendarlistview.library.CalendarListView;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.util.Random;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by jiaw2 on 2016/12/23.
 */
public class CalendarActivity extends Activity {
    public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy年MM月");

    private CalendarListView calendarListView;
    private DayNewsListAdapter dayNewsListAdapter;
    private CalendarItemAdapter calendarItemAdapter;
    //key:date "yyyy-mm-dd" format.
    private TreeMap<String, List<NewsService.News.StoriesBean>> listTreeMap = new TreeMap<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarListView = (CalendarListView) findViewById(R.id.calendar_listview);
        dayNewsListAdapter = new DayNewsListAdapter(this);
        calendarItemAdapter = new CalendarItemAdapter(this);
        calendarListView.setCalendarListViewAdapter(calendarItemAdapter, dayNewsListAdapter);

        // set start time,just for test.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -7);
//        loadNewsList(DAY_FORMAT.format(calendar.getTime()));
//
//        // deal with refresh and load more event.
//        calendarListView.setOnListPullListener(new CalendarListView.onListPullListener() {
//            @Override
//            public void onRefresh() {
//                String date = listTreeMap.firstKey();
//                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
//                calendar.add(Calendar.MONTH, -1);
//                calendar.set(Calendar.DAY_OF_MONTH, 1);
//                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
//            }
//
//            @Override
//            public void onLoadMore() {
//                String date = listTreeMap.lastKey();
//                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
//                calendar.add(Calendar.MONTH, 1);
//                calendar.set(Calendar.DAY_OF_MONTH, 1);
//                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
//            }
//        });
//
//        //
//        calendarListView.setOnMonthChangedListener(new CalendarListView.OnMonthChangedListener() {
//            @Override
//            public void onMonthChanged(String yearMonth) {
//                Calendar calendar = CalendarHelper.getCalendarByYearMonth(yearMonth);
//                loadCalendarData(yearMonth);
//                Toast.makeText(CalendarActivity.this, YEAR_MONTH_FORMAT.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        calendarListView.setOnCalendarViewItemClickListener(new CalendarListView.OnCalendarViewItemClickListener() {
//            @Override
//            public void onDateSelected(View View, String selectedDate, int listSection, SelectedDateRegion selectedDateRegion) {
//
//            }
//        });
    }

    public void Info() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/").build();
        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<GitHubService.Repo>> repos = service.listRepos("ocatt");


        //异步调用
        repos.enqueue(new Callback<List<GitHubService.Repo>>() {
            @Override
            public void onResponse(Call<List<GitHubService.Repo>> call, Response<List<GitHubService.Repo>> response) {
                List<GitHubService.Repo> data = response.body();
            }

            @Override
            public void onFailure(Call<List<GitHubService.Repo>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //this code is just for generate test date for ListView!
//    private void loadNewsList(String date) {
//        Calendar calendar = getCalendarByYearMonthDay(date);
//        String key = CalendarHelper.YEAR_MONTH_FORMAT.format(calendar.getTime());
//
//        // just not care about how data to create.
//        Random random = new Random();
//        final List<String> set = new ArrayList<>();
//        while (set.size() < 5) {
//            int i = random.nextInt(29);
//            if (i > 0) {
//                if (!set.contains(key + "-" + i)) {
//                    if (i < 10) {
//                        set.add(key + "-0" + i);
//                    } else {
//                        set.add(key + "-" + i);
//                    }
//                }
//            }
//        }
//
//        Observable<Notification<NewsService.News>> newsListOb =
//                RetrofitProvider.getInstance().create(NewsService.class)
//                        .getNewsList(date)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .compose(bindToLifecycle())
//                        .materialize().share();
//
//        newsListOb.filter(Notification::isOnNext)
//                .map(n -> n.getValue())
//                .filter(m -> !m.getStories().isEmpty())
//                .flatMap(m -> Observable.from(m.getStories()))
//                .doOnNext(i -> {
//                    int index = random.nextInt(5);
//                    String day = set.get(index);
//                    if (listTreeMap.get(day) != null) {
//                        List<NewsService.News.StoriesBean> list = listTreeMap.get(day);
//                        list.add(i);
//                    } else {
//                        List<NewsService.News.StoriesBean> list = new ArrayList<NewsService.News.StoriesBean>();
//                        list.add(i);
//                        listTreeMap.put(day, list);
//                    }
//
//                })
//                .toList()
//                .subscribe((l) -> {
//                    dayNewsListAdapter.setDateDataMap(listTreeMap);
//                    dayNewsListAdapter.notifyDataSetChanged();
//                    calendarItemAdapter.notifyDataSetChanged();
//                });
//    }
//
//    // date (yyyy-MM),load data for Calendar View by date,load one month data one times.
//    // generate test data for CalendarView,imitate to be a Network Requests. update "calendarItemAdapter.getDayModelList()"
//    //and notifyDataSetChanged will update CalendarView.
//    private void loadCalendarData(final String date) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    sleep(1000);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Random random = new Random();
//                            if (date.equals(calendarListView.getCurrentSelectedDate().substring(0, 7))) {
//                                for (String d : listTreeMap.keySet()) {
//                                    if (date.equals(d.substring(0, 7))) {
//                                        CustomCalendarItemModel customCalendarItemModel = calendarItemAdapter.getDayModelList().get(d);
//                                        if (customCalendarItemModel != null) {
//                                            customCalendarItemModel.setNewsCount(listTreeMap.get(d).size());
//                                            customCalendarItemModel.setFav(random.nextBoolean());
//                                        }
//
//                                    }
//                                }
//                                calendarItemAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }.start();
//
//    }

    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(DAY_FORMAT.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
