package HttpHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    public DownloadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        String imageURL = urls[0];
        Bitmap bitmap = null;

        try{
            InputStream inputStream = new java.net.URL(imageURL).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }

    public void onPostExecute(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }
}
