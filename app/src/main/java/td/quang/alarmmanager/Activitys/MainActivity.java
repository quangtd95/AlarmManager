package td.quang.alarmmanager.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import td.quang.alarmmanager.Adapters.ViewPagerAdapter;
import td.quang.alarmmanager.Database.MyDatabase;
import td.quang.alarmmanager.Fragments.AlarmFragment;
import td.quang.alarmmanager.Fragments.MyFragment;
import td.quang.alarmmanager.Fragments.StopwatchFragment;
import td.quang.alarmmanager.Fragments.TimerFragment;
import td.quang.alarmmanager.R;
import td.quang.alarmmanager.Services.AlarmService;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<MyFragment> fragmentList;
    private MyDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,AlarmService.class));
        myDatabase = MyDatabase.getInstance(this);
        myDatabase.open();
        initFragments();
        addComponents();
        addEvents();


    }


    private void addEvents() {

    }
    private void initFragments(){
        fragmentList = new ArrayList<>();
        fragmentList.add(new AlarmFragment());
        fragmentList.add(new StopwatchFragment());
        fragmentList.add(new TimerFragment());
    }

    private void addComponents() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.mViewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        mTabLayout.setSelectedTabIndicatorHeight(2);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
