package com.example.leon.delicacyexploration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//Created by leon on 2017/8/20.

public class HttpRequestAsyncTask extends AsyncTask<String, Void, String>
{
    private AsyncResponse _delegate;
    private Context _context;
    private HashMap<String, String> _postData = new HashMap<>();
    private String _httpMethod;
    private ProgressDialog _progressDialog;
    private final String LOADING_MESSAGE = "Loading data from server...";

    public HttpRequestAsyncTask(AsyncResponse delegate)
    {
        _delegate = delegate;
        _context = (Context) delegate;
        _httpMethod = "GET";
    }

    public HttpRequestAsyncTask(AsyncResponse delegate, HashMap<String, String> postData, String httpMethod)
    {
        _delegate = delegate;
        _context = (Context) delegate;
        _postData = postData;
        _httpMethod = httpMethod;
    }

    public HttpRequestAsyncTask(Fragment delegate)
    {
        _delegate = (AsyncResponse) delegate;
        _context = delegate.getActivity();
        _httpMethod = "GET";
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        _progressDialog = new ProgressDialog(_context);
        _progressDialog.setMessage(LOADING_MESSAGE);
        _progressDialog.show();
    }

    @Override
    protected String doInBackground(String... urls)
    {
        String result;

        if (_httpMethod.equals("GET"))
        {
            result = InvokeGet(urls[0]);
        }
        else
        {
            result = InvokePost(urls[0], _postData);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (_progressDialog.isShowing())
        {
            _progressDialog.dismiss();
        }

        result = result.trim();
        _delegate.FinishAsyncProcess(result);
    }

    private String InvokeGet(String requestURL)
    {
        URL url;
        String response = "";

        try
        {
            url = new URL(requestURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null)
                {
                    response += line;
                }
            }
            else
            {
                Toast.makeText(_context, GetResponseCodeErrorMessage(responseCode), Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return response;
    }

    private String InvokePost(String requestURL, HashMap<String, String> postDataParams)
    {
        URL url;
        String response = "";

        try
        {
            url = new URL(requestURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(GetPostDataString(postDataParams));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null)
                {
                    response += line;
                }
            }
            else
            {
                Toast.makeText(_context, GetResponseCodeErrorMessage(responseCode), Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return response;
    }

    private String GetPostDataString(HashMap<String, String> postData)
    {
        StringBuilder result = new StringBuilder();
        boolean isFirstElement = true;

        try
        {
            for (Map.Entry<String, String> entry : postData.entrySet())
            {
                if (isFirstElement)
                {
                    isFirstElement = false;
                }
                else
                {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException exception)
        {
            exception.printStackTrace();
        }

        return result.toString();
    }

    private String GetResponseCodeErrorMessage(int responseCode)
    {
        return "ResponseCode: " + responseCode + " in HttpRequestAsyncTask.java";
    }

    public AsyncResponse GetDelegate()
    {
        return _delegate;
    }

    public Context GetContext()
    {
        return _context;
    }

    public HashMap<String, String> GetPostData()
    {
        return _postData;
    }

    public void SetPostData(HashMap<String, String> postData)
    {
        _postData = postData;
    }
}
