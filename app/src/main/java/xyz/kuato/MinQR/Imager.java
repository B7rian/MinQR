package xyz.kuato.MinQR;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Imager {
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private QRDecoder mDecoder;

    protected void Init(PreviewView previewView, AppCompatActivity activity)
	{
        Context context = activity.getBaseContext();
        Executor executor = ContextCompat.getMainExecutor(activity);

	    mDecoder = new QRDecoder();
	    mDecoder.Init(activity);

        mCameraProviderFuture = ProcessCameraProvider.getInstance(context);
        mCameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider =
                        mCameraProviderFuture.get();
                bindUseCases(activity, executor, cameraProvider, previewView, mDecoder.getAnalyzer());
            } catch (ExecutionException | InterruptedException e) {
				Log.d("MinQR", "Failed to get camera provider");
            }
        }, executor);
    }

    void bindUseCases(LifecycleOwner lifecycleOwner,
                      Executor executor,
                      @NonNull ProcessCameraProvider cameraProvider,
                      @NonNull PreviewView previewView,
                      @NonNull ImageAnalysis.Analyzer analyzer) {

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executor, analyzer);

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //ViewPort viewPort = new ViewPort.Builder(
        //        new Rational(mWidth, mHeight), Surface.ROTATION_0)
        //        .build();

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                //.setViewPort(viewPort)
                .build();

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup);
    }

    public void start() {
        mDecoder.start();
    }
}
