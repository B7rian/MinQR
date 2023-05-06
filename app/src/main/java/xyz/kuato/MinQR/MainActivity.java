package xyz.kuato.MinQR;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import xyz.kuato.MinQR.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private xyz.kuato.MinQR.databinding.ActivityMainBinding mBinding;
    private final Imager mImager = new Imager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);

        PreviewView mPreviewView = root.findViewById(R.id.previewView);

        int perm = ContextCompat.checkSelfPermission(this,
                "android.permission.CAMERA");
        if(perm == PackageManager.PERMISSION_DENIED) {
            askForPermission();
        }

        mImager.Init(mPreviewView,this);
        mImager.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check permissions in case user revoked them while app was paused
        int perm = ContextCompat.checkSelfPermission(this,
                "android.permission.CAMERA");
        if(perm == PackageManager.PERMISSION_DENIED) {
            askForPermission();
        }

        mImager.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    // Register the permissions callback, which handles the user's response
    // to the system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Log.d("MinQR", "Permission denied");
                        }
                    });

    void askForPermission() {
        requestPermissionLauncher.launch("android.permission.CAMERA");
    }
}

