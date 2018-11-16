package drysis.hc.com.drysis.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import drysis.hc.com.drysis.bean.entity.Sister;

/*
 描述：网络请求处理相关类
 */
public class SisterApi {
    private static final String TAG="Network";
    private static final String BASE_URL="https://gank.io/api/data/福利/";
    /*
    查询妹子信息
     */
    public ArrayList<Sister> fetchSister(int count, int page){
        String fetchUrl=BASE_URL+count+"/"+page;
        ArrayList<Sister> sisters=new ArrayList<>();
        try{
            URL url=new URL(fetchUrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            int code =conn.getResponseCode();
            Log.d(TAG, "conn: "+conn);
            Log.d(TAG, "fetchUrl: "+fetchUrl);
            Log.d(TAG, "Server response: "+code);//响应code;
            if(code==200){
                InputStream in=conn.getInputStream();
                byte[] data =readFromStream(in);
                String result=new String(data,"UTF-8");//字符集UTF-8
                sisters=parseSister(result);
            }else{
                Log.e(TAG, "request failed:  "+code );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  sisters;
    }
    /*
    解析返回Json数据的方法
     */
    public ArrayList<Sister> parseSister(String content) throws Exception{
        ArrayList<Sister> sisters=new ArrayList<>();
        JSONObject object=new JSONObject(content);
        JSONArray array=object.getJSONArray("results");
        for(int i=0;i<array.length();i++){
            JSONObject results=(JSONObject)array.get(i);
            Sister sister=new Sister();
            sister.set_id(results.getString("_id"));
            sister.setCreateAt(results.getString("createdAt"));
            sister.setDesc(results.getString("desc"));
            sister.setSource(results.getString("source"));
            sister.setPublishedAt(results.getString("publishedAt"));
            sister.setType(results.getString("type"));
            sister.setUrl(results.getString("url"));
            sister.setUsed(results.getBoolean("used"));
            sister.setWho(results.getString("who"));
            sisters.add(sister);
        }
        return  sisters;
    }
    /*
    读流中数据的方法
     */
    public  byte[] readFromStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        byte[] buffer =new byte[1024];
        int len;
        while ((len=inputStream.read(buffer))!=-1) {
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        return  outputStream.toByteArray();
    }


}
