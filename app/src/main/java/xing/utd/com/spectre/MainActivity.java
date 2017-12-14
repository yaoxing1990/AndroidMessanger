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
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        GetToken getToken = new GetToken("yaoxing1990@gmail.com","667GH@#jgye", myWebView);
        getToken.execute();

    }

    class GetToken extends AsyncTask<String, Void, String> {

        String email;
        String password;
        String response = "";
        WebView myWebview;


        public GetToken (String email, String password, WebView myWebView) {
            this.email = email;
            this.password = password;
            this.myWebview = myWebView;
        }

        protected String doInBackground(String... urls) {
            try {
                Connection.Response loginForm = Jsoup.connect("https://www.facebook.com/v2.6/dialog/oauth?redirect_uri=fb464891386855067%3A%2F%2Fauthorize%2F&display=touch&state=%7B%22challenge%22%3A%22IUUkEUqIGud332lfu%252BMJhxL4Wlc%253D%22%2C%220_auth_logger_id%22%3A%2230F06532-A1B9-4B10-BB28-B29956C71AB1%22%2C%22com.facebook.sdk_client_state%22%3Atrue%2C%223_method%22%3A%22sfvc_auth%22%7D&scope=user_birthday%2Cuser_photos%2Cuser_education_history%2Cemail%2Cuser_relationship_details%2Cuser_friends%2Cuser_work_history%2Cuser_likes&response_type=token%2Csigned_request&default_audience=friends&return_scopes=true&auth_type=rerequest&client_id=464891386855067&ret=login&sdk=ios&logger_id=30F06532-A1B9-4B10-BB28-B29956C71AB1&ext=1470840777&hash=AeZqkIcf-NEW6vBd")
                        .method(Connection.Method.GET)
                        .header("user-agent","MOBILE_USER_AGENT")
                        .header("parser","lxml")
                        .ignoreContentType(true)
                        .execute();
                Document login = loginForm.parse();
                Elements form = login.getElementsByTag("form");
                String url = form.get(0).attr("action");
                myWebview.getSettings().setJavaScriptEnabled(true);
                MyJavaScriptInterface jInterface = new MyJavaScriptInterface(MainActivity.this);
                myWebview.addJavascriptInterface(jInterface, "HtmlViewer");

                myWebview.setWebViewClient(
                        new WebViewClient()
                        {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                //Load HTML
                                myWebview.loadUrl(url);
                            }
                        }

                );

                Document next = Jsoup.connect(url)
                        .data("cookieexists", "false")
                        .data("email", this.email)
                        .data("pass", this.password)
                        .cookies(loginForm.cookies())
                        .post();
                System.out.print(next);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    class GetMessage extends AsyncTask<String, Void, Response> {

        String tinderToken;
        Response response = null;
        String id;
        String message;

        public GetMessage(String id) {
            this.id = id;
        }

        protected Response doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.gotinder.com/message/" + id)
                    .get()
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
            try {
                message = this.response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

            class MyJavaScriptInterface {

                private Context ctx;
                public String html;

                MyJavaScriptInterface(Context ctx) {
                    this.ctx = ctx;
                }

                @JavascriptInterface
                public void showHTML(String _html) {
                    html = _html;
                }
            }
}


