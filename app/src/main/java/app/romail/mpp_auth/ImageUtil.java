package app.romail.mpp_auth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.commons.io.IOUtils;

import com.gemalto.jp2.JP2Decoder;
import org.jnbis.internal.WsqDecoder;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    /**
     * Decodes an image from an InputStream based on its MIME type.
     *
     * @param context The Android context, useful if needed for certain types of image processing (unused here).
     * @param mimeType The MIME type of the image to decode.
     * @param inputStream The InputStream from which the image will be read.
     * @return The decoded Bitmap image.
     */
    public static Bitmap decodeImage(Context context, String mimeType, InputStream inputStream) throws IOException {
        if (mimeType.equalsIgnoreCase("image/jp2") || mimeType.equalsIgnoreCase("image/jpeg2000")) {
            JP2Decoder jp2Decoder = new JP2Decoder(inputStream);
            return jp2Decoder.decode();
        } else if (mimeType.equalsIgnoreCase("image/x-wsq")) {
            WsqDecoder wsqDecoder = new WsqDecoder();
            //input stream to byte array

            org.jnbis.api.model.Bitmap wsqBitmap = wsqDecoder.decode(IOUtils.toByteArray(inputStream));
            int[] intData = new int[wsqBitmap.getPixels().length];
            for (int j = 0; j < wsqBitmap.getPixels().length; j++) {
                byte pixel = wsqBitmap.getPixels()[j];
                intData[j] = (0xFF000000) |
                        ((pixel & 0xFF) << 16) |
                        ((pixel & 0xFF) << 8) |
                        (pixel & 0xFF);
            }
            return android.graphics.Bitmap.createBitmap(
                    intData,
                    0,
                    wsqBitmap.getWidth(),
                    wsqBitmap.getWidth(),
                    wsqBitmap.getHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
        } else {
            return BitmapFactory.decodeStream(inputStream);
        }
    }
}
