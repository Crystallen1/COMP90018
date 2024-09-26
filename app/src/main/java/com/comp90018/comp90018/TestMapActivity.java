package com.comp90018.comp90018;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.ui.map.MapFragment;

public class TestMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);

        // 加载 MapFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .commit();
        }
    }
}