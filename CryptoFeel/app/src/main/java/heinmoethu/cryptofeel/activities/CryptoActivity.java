package heinmoethu.cryptofeel.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import heinmoethu.cryptofeel.fragments.CryptoFragment;

public class CryptoActivity extends SingleFragmentActivity {
    public static final String EXTRA_CRYPTO_ID = "crypto_id";

    @Override
    protected Fragment getFragment() {
        Bundle extras = getIntent().getExtras();
        CryptoFragment cf= new CryptoFragment();
        cf.setArguments(extras);
        return cf;
    }
}
