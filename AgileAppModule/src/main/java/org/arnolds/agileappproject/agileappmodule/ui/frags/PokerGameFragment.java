package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.arnolds.agileappproject.agileappmodule.R;

public class PokerGameFragment extends ArnoldSupportFragment {

    public final static int DRAWER_POSITION = 6;
    private final static int AMOUNT_OF_POSSIBILITIES = 10;
    private ViewPager viewPager;

    public PokerGameFragment() {
        super(DRAWER_POSITION);
    }

    @Override
    public void onNewRepositorySelected() {
        //Do nothing
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (viewPager != null) {
            viewPager.setCurrentItem(AMOUNT_OF_POSSIBILITIES / 2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_planning_poker, container, false);

        viewPager = (ViewPager) ret.findViewById(R.id.poker_pager);

        viewPager
                .setAdapter(new ScreenSlidePagerAdapter(
                        getActivity().getSupportFragmentManager()));

        viewPager.setCurrentItem(AMOUNT_OF_POSSIBILITIES / 2);

        return ret;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private PokerCardSupportFragment[] cards =
                new PokerCardSupportFragment[AMOUNT_OF_POSSIBILITIES];

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (cards[position] == null) {
                Bundle args = new Bundle();
                args.putInt(PokerCardSupportFragment.KEY_VALUE, position);
                cards[position] = (PokerCardSupportFragment) PokerCardSupportFragment
                        .instantiate(getActivity().getApplicationContext(),
                                PokerCardSupportFragment.class.getName());
                cards[position].setArguments(args);
            }
            return cards[position];
        }

        @Override
        public int getCount() {
            return AMOUNT_OF_POSSIBILITIES;
        }
    }
}
