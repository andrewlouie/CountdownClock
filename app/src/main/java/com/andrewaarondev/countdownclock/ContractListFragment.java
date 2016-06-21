package com.andrewaarondev.countdownclock;

import android.app.Activity;
import android.app.ListFragment;

public class ContractListFragment<T> extends ListFragment {
    private T contract;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            contract = (T) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(activity.getClass()
                    .getSimpleName()
                    + " does not implement contract interface for "
                    + getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        contract = null;
    }

    public final T getContract() {
        return (contract);
    }
}
