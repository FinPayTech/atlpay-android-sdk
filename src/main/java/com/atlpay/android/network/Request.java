package com.atlpay.android.network;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.atlpay.android.ATLPay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
public class Request extends AsyncTask<Void, Void, String>{
    public Context mContext;
    NetworkRequest networkRequest;
    public String endPoint;
    public int method = 0;
    public HashMap<String, String> payloadParams;
    int responseCode;
    public String responseBody;
    public class Method{
        public final static int POST   =   1;
        public final static int GET   =   0;
    }
    public Request(Context mContext){
        this.mContext   =   mContext;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url =   new URL(this.endPoint);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            if(this.method == Method.POST) {
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
            }else if(this.method == Method.GET){
                urlConnection.setRequestMethod("GET");
            }else{
                throw new Exception("Method Not Supported");
            }
            urlConnection.setRequestProperty("X-API-Key", ATLPay.getSecretKey());
            if (this.payloadParams != null && this.method == Method.POST) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                String postData =   "";
                for (Map.Entry<String, String> entry  : this.payloadParams.entrySet()){
                    if(postData == ""){
                        postData    +=  entry.getKey()+"="+entry.getValue();
                    }else{
                        postData    +=  "&"+entry.getKey()+"="+entry.getValue();
                    }
                }
                Log.d("AP Request", postData);
                writer.write(postData);
                writer.flush();
            }
            this.responseCode = urlConnection.getResponseCode();
            try {
                if(this.responseCode == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }else{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        catch(MalformedURLException e) {
            Log.d("URL is Malformed", e.getMessage());
            return null;
        }catch(IOException e) {
            Log.d("IO Exception", e.toString());
            e.printStackTrace();
            return null;
        }catch(Exception e) {
            Log.d("Other Exception", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.responseBody   =   s;
        if(this.responseCode != 200){
            ATLPayError apError =   new ATLPayError(this);
            this.networkRequest.onFailure(apError);
        }else{
            JSONObject responseObject   = null;
            try {
                responseObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.networkRequest.onSuccess(responseObject);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public void sendPayload(int method, String endPoint, HashMap<String, String> payloadParams, NetworkRequest networkRequest){
        this.networkRequest    =   networkRequest;
        this.endPoint          =   endPoint;
        this.method            =   method;
        this.payloadParams     =   payloadParams;
        this.execute();
    }

}
