package com.comp90018.comp90018;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.ui.map.MapFragment;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;

public class TestMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);
        setApiKeyForApp();

        // 加载 MapFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .commit();
        }
    }
    private void setApiKeyForApp(){
        ArcGISRuntimeEnvironment.setApiKey("AAPTxy8BH1VEsoebNVZXo8HurMNuEFG9BnB8x9eUigI-jROBRUDSC7Rge3VVF1_Wx_d8dJzODC6N34LMf0StT-Nul_eSACzKl2HTyhXGW3znx9abJrwZ111IeeDaWmQ1TOUXPdH5mhw-39PHcX6v0zNHVozSMc8G-k2yUjKkDAu86KYpGBzssSOzJK5NEJW7425N-CFG7I1jUFGZUnu2_ldnXz3IDKZEullpp1MDo4HefbQ.AT1_GKWtrzd2");
    }
}