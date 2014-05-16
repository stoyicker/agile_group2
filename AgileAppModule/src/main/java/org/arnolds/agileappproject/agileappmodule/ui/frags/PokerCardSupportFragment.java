package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

public class PokerCardSupportFragment extends Fragment {

    private int value;
    static final String KEY_VALUE = "VALUE";
    private final String CARD_PATTERN = "uno_card_";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        value = getArguments().getInt(KEY_VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.poker_card, container, false);

        ImageView card = (ImageView) ret.findViewById(R.id.poker_card);

        card.setImageResource(AgileAppModuleUtils.getDrawableAsId(CARD_PATTERN + value, -1));

        return ret;
    }
}
