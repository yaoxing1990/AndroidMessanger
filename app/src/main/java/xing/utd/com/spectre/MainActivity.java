package xing.utd.com.spectre;


import android.content.Context;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;


import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetToken getToken = new GetToken("","");
        getToken.execute();

    }

    class GetToken extends AsyncTask<String, Void, String> {

        String email;
        String password;
        String response = "";


        public GetToken (String email, String password) {
            this.email = email;
            this.password = password;
        }

        protected String doInBackground(String... urls) {

            return response;
        }

        protected void onPostExecute(String feed) {
            final String id = "1088093377897988";
            final String token = "EAAGm0PX4ZCpsBADkapnyZC8z9zVvRutWFN7Ndi9RrAjoLaDa27EnpjdoJ1nUc8vBWaD9afryUkCpsXBRzKhYZAP17qZCnEVffBZBpIVTMp0UWuy9KNGDhK3KNsXW2Pwf9F8wQWfwteFsIR0LbZBZBZCaLoruM33TuCdti29g6DSfeAbgltaqnaLbS0U6uuPGvjZAChtyJ8DS9WefxomkP0KyKtF2FRKjoT3i2NwWh0rOIlgZDZD";
            Authentication auth = new Authentication(id, token);
            auth.execute();
        }

    }

    class Authentication extends AsyncTask<String, Void, Response> {

        String id;
        String token;
        Response response = null;

        public Authentication (String id, String token) {
            this.id = id;
            this.token = token;
        }

        protected Response doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = null;
            try {
                final MediaType JSON = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                json.put("facebook_token", token);
                json.put("facebook_id", id);
                String jsonString = json.toString();
                body = RequestBody.create(JSON, jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url("https://api.gotinder.com/auth")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("user-agent", "Tinder/7.5.3 (iPhone; iOS 10.3.2; Scale/2.00)")
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(Response feed) {
            parseAuth pj = new parseAuth(response);
            pj.execute();
        }

    }

    class parseAuth extends AsyncTask<String, Void, String> {

        Response res;
        String tar = null;
        String tinderToken = null;

        public parseAuth(Response res) {
            this.res = res;
        }

        protected String doInBackground(String... urls) {
            try {
                tar = res.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tar;
        }

        protected void onPostExecute(String feed) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject res = (JSONObject) parser.parse(tar);
                JSONObject user = (JSONObject) parser.parse(res.get("user").toString());
                tinderToken = user.get("api_token").toString();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.getMessage();
            }
            if(tinderToken != null) {
                GetUpdate getProfile = new GetUpdate(tinderToken);
                getProfile.execute();
            }
        }
    }

    class GetUpdate extends AsyncTask<String, Void, Response> {

        String tinderToken;
        Response response = null;

        public GetUpdate(String tinderToken) {
            this.tinderToken = tinderToken;
        }

        protected Response doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("last_activity_date", "")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.gotinder.com/updates")
                    .post(body)
                    .addHeader("X-Auth-Token", tinderToken)
                    .addHeader("content-type", "application/json")
                    .addHeader("user-agent", "Tinder/7.5.3 (iPhone; iOS 10.3.2; Scale/2.00)")
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(Response feed) {
            Parse parser = new Parse(response);
            parser.execute();
        }
    }

    class Parse extends AsyncTask<String, Void, String> {

        Response res;
        String tar = null;
        DialogsList dialogsListView;
        int dialogs;
        DialogsListAdapter dialogsListAdapter;

        public Parse(Response res) {
            this.res = res;
            dialogsListView = findViewById(R.id.dialogsList);
            dialogs = R.layout.dialog;
            dialogsListAdapter = new DialogsListAdapter<>(dialogs, new ImageLoader() {
                @Override
                public void loadImage(ImageView imageView, String url) {
                    //If you using another library - write here your way to load image
                    Picasso.with(MainActivity.this).load(url).into(imageView);
                }
            });
        }

        protected String doInBackground(String... urls) {
            try {
                tar = res.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tar;
        }

        protected void onPostExecute(String feed) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject res = (JSONObject) parser.parse(tar);
                org.json.simple.JSONArray results = (org.json.simple.JSONArray) (res.get("matches"));
                ArrayList<IUser> list = new ArrayList<>();
                for(int i = 0; i < results.size(); i++) {
                    JSONObject str = (JSONObject) parser.parse(results.get(i).toString());
                    final String id = str.get("_id").toString();
                    final String date = str.get("last_activity_date").toString();
                    final org.json.simple.JSONArray messages = (org.json.simple.JSONArray)str.get("messages");
                    JSONObject person = (JSONObject) parser.parse(str.get("person").toString());
                    String name = person.get("name").toString();
                    org.json.simple.JSONArray photos = (org.json.simple.JSONArray)person.get("photos");
                    JSONObject photo = (JSONObject)photos.get(0);
                    org.json.simple.JSONArray pics = (org.json.simple.JSONArray)photo.get("processedFiles");
                    JSONObject files = (JSONObject) pics.get(0);
                    String avatar = files.get("url").toString();
                    final Author author = new Author(id, name, avatar);
                    list.add(author);
                    IMessage message = new IMessage() {
                        @Override
                        public String getId() {
                            return id;
                        }

                        @Override
                        public String getText() {
                            if(messages.size() == 0) return "";
                            else {
                                JSONObject results = (JSONObject) messages.get(messages.size() - 1);
                                return results.get("message").toString();
                            }
                        }

                        @Override
                        public IUser getUser() {
                            return author;
                        }

                        @Override
                        public Date getCreatedAt() {
                            Date d = new Date();
                            try {
                                DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                d = format.parse(date.replace("T", " "));
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                                return d;
                            }
                            return d;
                        }
                    };
                    dialogsListAdapter.addItem(new DefaultDialog(id, avatar, name, list, message, 0));
                }
                dialogsListView.setAdapter(dialogsListAdapter);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.getMessage();
            }
        }
    }
}


