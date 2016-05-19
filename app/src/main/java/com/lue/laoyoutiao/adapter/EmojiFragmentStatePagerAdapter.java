package com.lue.laoyoutiao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.lue.laoyoutiao.fragment.EmojiFragment;

/**
 * Created by Lue on 2016/5/16.
 */
public class EmojiFragmentStatePagerAdapter extends FragmentStatePagerAdapter
{
    private SparseArray<Fragment> mPages;

    private String[] mTitles;

    public EmojiFragmentStatePagerAdapter(FragmentManager fm, String[] mTitles) {
        super(fm);
        mPages = new SparseArray<Fragment>();
        this.mTitles = mTitles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 0:
                f = EmojiFragment.getInstance(73, 0);
                break;
            case 1:
                f = EmojiFragment.getInstance(42, 1);
                break;
            case 2:
                f = EmojiFragment.getInstance(25, 2);
                break;
            case 3:
                f = EmojiFragment.getInstance(59, 3);
                break;
        }
        mPages.put(position, f);
        return f;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (0 <= mPages.indexOfKey(position)) {
            mPages.remove(position);
        }
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
