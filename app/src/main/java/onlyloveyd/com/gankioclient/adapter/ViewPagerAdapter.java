package onlyloveyd.com.gankioclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import onlyloveyd.com.gankioclient.fragment.AboutFragment;
import onlyloveyd.com.gankioclient.fragment.BonusFragment;
import onlyloveyd.com.gankioclient.fragment.DailyFragment;
import onlyloveyd.com.gankioclient.fragment.SortFragment;

/**
 * Copyright 2017 yidong
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    //public static String[] titles = {"最新","分类阅读","福利","闲读","关于"};
    public static String[] titles = {"每日干货", "分类阅读", "福利", "关于"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        System.err.println("yidong -- position = " + position);
        switch (position) {
            case 0:
                return DailyFragment.newInstance();
            case 1:
                return AboutFragment.newInstance();
            case 2:
                return BonusFragment.newInstance();
            case 3:
                return SortFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}