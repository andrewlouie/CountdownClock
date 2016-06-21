package com.andrewaarondev.countdownclock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MyListFragment extends
        ContractListFragment<MyListFragment.Contract> {

    private static ArrayList<Countdown> countdowns = null;
    private AddStringTask task = null;

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        if (getResources().getBoolean(R.bool.widescreen) && MainActivity.selectedItem == -1)
            MainActivity.selectedItem = 0;
        EventBus.getDefault().registerSticky(this);
        DatabaseHelper db = DatabaseHelper.getInstance(this.getActivity().getBaseContext());
        db.getCountdowns();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().registerSticky(this);
        if (task == null) {
            task = new AddStringTask();
            task.execute();
        }
        super.onResume();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ListLoadedEvent event) {
        countdowns = event.getCountdowns();
        setListAdapter(new CustomAdapter());
        if (getResources().getBoolean(R.bool.widescreen))
            onListItemClick(getListView(), getView(), MainActivity.selectedItem, 0);
        else if (MainActivity.selectedItem != -1) {
            onListItemClick(getListView(), getView(), MainActivity.selectedItem, 0);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (getContract().isPersistentSelection()) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            l.setItemChecked(position, true);
        } else {
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
        }

        getContract().onItemSelected(countdowns.get(position), position);
    }


    public class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return countdowns.size();
        }

        @Override
        public Countdown getItem(int position) {
            return countdowns.get(position);
        }

        @Override
        public long getItemId(int position) {
            return countdowns.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder wrapper = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row, parent, false);
                wrapper = new MyViewHolder(convertView,getActivity().getBaseContext().getFilesDir().toString(),getActivity().getBaseContext());
                convertView.setTag(wrapper);
            } else {
                wrapper = (MyViewHolder) convertView.getTag();
            }

            wrapper.populateFrom(getItem(position));

            return (convertView);
        }
    }

    class AddStringTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            while (!isCancelled()) {
                if (countdowns != null) {
                    String[] newtimes = new String[countdowns.size()];
                    for (int i = 0; i < countdowns.size(); i++) {
                        newtimes[i] = Helpers.getDateDifference(Calendar.getInstance(), countdowns.get(i).getDate(),countdowns.get(i).isNoSpecificTime());
                    }
                    publishProgress(newtimes);
                }
                SystemClock.sleep(1000);
            }
            return (null);
        }

        @Override
        protected void onProgressUpdate(String... item) {
            if (!isCancelled()) {
                for (int i = 0; i < item.length; i++) {
                    if (getListView().getChildAt(i) != null) {
                        TextView here = (TextView) getListView().getChildAt(i).findViewById(R.id.timeleft);
                        here.setText(item[i]);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            task = null;
        }
    }

    interface Contract {
        void onItemSelected(Countdown c, int position);

        boolean isPersistentSelection();
    }
}
