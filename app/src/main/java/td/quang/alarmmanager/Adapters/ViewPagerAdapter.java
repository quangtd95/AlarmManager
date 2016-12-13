package td.quang.alarmmanager.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import td.quang.alarmmanager.Fragments.MyFragment;

/**
 * Created by Quang_TD on 12/10/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<MyFragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, List<MyFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getName();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return (fragments == null)? 0:fragments.size();
    }
}
