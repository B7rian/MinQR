package xyz.kuato.MinQR;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;

public class QRDecoder {
    public void Init(Activity activity)
	{
        mAnalyzer = image -> {
            if(stop) {
                Log.d("Bob", "Stopped");
                return;
            }

            mPerfCounter.inc();
            ImageProxy.PlaneProxy plane = image.getPlanes()[0]; // [0] is the luminance plane
            ByteBuffer buf = plane.getBuffer();
            byte[] bytes = new byte[buf.capacity()];
            buf.get(bytes);

            // The sequence below roughly matches zxing's DecodeHandler.java code
            LuminanceSource lum = new PlanarYUVLuminanceSource(
                    bytes,
                    image.getWidth(), image.getHeight(),
                    0, 0,
                    image.getWidth(), image.getHeight(),
                    false);
            HybridBinarizer bin = new HybridBinarizer(lum);
            BinaryBitmap binaryBitmap = new BinaryBitmap(bin);
            MultiFormatReader reader = new MultiFormatReader();
            try {
                Result scanResult = reader.decode(binaryBitmap);
                ResultHandler handler = ResultHandlerFactory.makeResultHandler(activity, scanResult);
                stop = true;
                handler.handleButtonPress(0);
                // Log.d("MinQR", "Hit");
            }
            catch(NotFoundException e) {
                // Log.d("MinQR", "Miss");
            }
            finally {
                image.close();  // Release the ImageProxy
                reader.reset(); // Maybe not necessary since we don't reuse reader
            }
        };
	}

	public void start() {
        stop = false;
    }

	public ImageAnalysis.Analyzer getAnalyzer()
    {
        return mAnalyzer;
    }

    private ImageAnalysis.Analyzer mAnalyzer;
    private boolean stop = false;
    private PerfCounter mPerfCounter = new PerfCounter("SPS");

}
