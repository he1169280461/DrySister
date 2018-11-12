package drysis.hc.com.drysis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;


public class PictureLoader {
    private ImageView  loadimageView;
    private String imgUri;
    private byte[] picByte;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0x123){
                /*
                字节组解码成bitmap对象
               */
                Bitmap bitmap=BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                loadimageView.setImageBitmap(bitmap);
            }
        }
    };
    public void load(ImageView imageView,String imgUri ){
        this.loadimageView=imageView;
        this.imgUri=imgUri;
        Drawable drawable=loadimageView.getDrawable();
        if(drawable!=null&&drawable instanceof BitmapDrawable){
            Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();
            if(bitmap!=null&&!bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try{
                URL url =new URL(imgUri);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if(conn.getResponseCode()==200){
                    InputStream inputStream=conn.getInputStream();
                    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                    byte[] bytes=new byte[1024];
                    int length=-1;
                    while ((length=inputStream.read(bytes))!=-1){
                        outputStream.write(bytes,0,length);
                    }
                    picByte=outputStream.toByteArray();
                    inputStream.close();
                    outputStream.close();
                    handler.sendEmptyMessage(0x123);
                }
            }catch (IOException E){
                E.printStackTrace();
            }
        }
    };
}