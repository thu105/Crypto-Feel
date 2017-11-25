package heinmoethu.cryptofeel.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.R;
import heinmoethu.cryptofeel.fragments.CryptoListFragment;

public class CryptoListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
        return new CryptoListFragment();
    }
}
