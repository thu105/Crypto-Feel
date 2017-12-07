package heinmoethu.cryptofeel.asyncTasks;


import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.joda.time.DateTime;

import java.util.concurrent.CountDownLatch;

public class Waiter extends AsyncTask<Void,Void,Void>{
    private CountDownLatch cdl;
    private SwipeRefreshLayout srl;
    private RecyclerView.Adapter adapter;

    Waiter(CountDownLatch cdl, SwipeRefreshLayout srl, RecyclerView.Adapter adapter) {
        this.cdl=cdl;
        this.srl=srl;
        this.adapter=adapter;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        srl.setRefreshing(false);
        adapter.notifyDataSetChanged();
        Log.e("TIME", DateTime.now().toString());
    }
}
