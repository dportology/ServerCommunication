package com.example.root.reportlocation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.System.exit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class MainActivity extends ActionBarActivity {

    Button btnCreateUser;
    Button btnUpdateLocation;
    Button btnLogin;
    Button btnLogoff;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateUser = (Button) findViewById(R.id.create_user);
        btnUpdateLocation = (Button) findViewById(R.id.update_location);
        btnLogin = (Button) findViewById(R.id.login);
        btnLogoff = (Button) findViewById(R.id.logoff);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/create_user.php?pn=BarakObama&pw=password&id=1234&dt=1&tid=1");
            }
        });
        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MainActivity.this);
                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/update_location.php?id=1234&lon="+Double.toString(longitude)+"&lat="+Double.toString(latitude));
                } else {
                    gps.showSettingsAlert();
                    exit(1);
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/logon.php?id=1234");
            }
        });
        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/logout.php?id=1234");
            }
        });
    }
}

class RequestTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}