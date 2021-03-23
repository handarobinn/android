package com.Test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    ProgressBar progressBar;
    TextView currentTempTextView;
    TextView minTempTextView;
    TextView maxTempTextView;
    TextView uvRatingTempTextView;
    ImageView weatherImageView;

    String TEMPERATURE_URL = "https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
    String IMAGE_URL = "https://openweathermap.org/img/w/";
    String UV_URL = "https://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        currentTempTextView = (findViewById(R.id.currentTemperature));
        minTempTextView = (findViewById(R.id.minTemperature));
        maxTempTextView = (findViewById(R.id.maxTemperature));
        uvRatingTempTextView = (findViewById(R.id.uvRating));
        weatherImageView = (findViewById(R.id.currentWeather));

        progressBar = (findViewById(R.id.progressBar));
        progressBar.setVisibility(View.VISIBLE);


        ForecastQuery req = new ForecastQuery();
        req.execute(TEMPERATURE_URL);

    }


    class ForecastQuery extends AsyncTask<String, Integer, String> {

        Bitmap currentWeatherImage;
        String currentTemperature;
        String minTemperature;
        String maxTemperature;
        String uvRating;
        String iconName;


        int progress = 0; // progress number


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            publishProgress();
        }


        /**
         * method for updating progress bar
         * */
        void publishProgress() {
            progress += 25;
            progressBar.setProgress(progress);
        }


        @Override
        protected String doInBackground(String... args) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = urlConnection.getInputStream();
                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8"); //response is data from the server


                //From part 3, slide 20
                String parameter = null;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                int progress = 1;
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {


                        //If you get here, then you are pointing at a start tag
                        if (xpp.getName().equals("weather")) {
                            //If you get here, then you are pointing to a <Weather> start tag
                            iconName = xpp.getAttributeValue(null, "icon");

                            //update progress bar
                            publishProgress();

                            // check the value of icon is not null or empty
                            if (iconName != null && !iconName.isEmpty()) {
                                String imageName = iconName + ".png";

                                // check image file exist in file or not
                                if (fileExistance(imageName)) {
                                    Log.i(imageName, "loaded from local");
                                    // get image from file
                                    getImageFRomFile(imageName);
                                } else {
                                    Log.i(imageName, "downloaded");
                                    String bitmapUrl = IMAGE_URL + imageName;
                                    //download image
                                    getBitMap(bitmapUrl);
                                }

                                //update progress bar
                                publishProgress();
                            }
                        }
                        // check the current tag’s name is “temperature”
                        else if (xpp.getName().equals("temperature")) {
                            // parse from XMl response and set to global variables
                            currentTemperature = xpp.getAttributeValue(null, "value");
                            minTemperature = xpp.getAttributeValue(null, "min");
                            maxTemperature = xpp.getAttributeValue(null, "max");


                            //update progress bar
                            publishProgress();
                        }
                    }


                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }


                getUVReating();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }


        /**
         * get UV rating from web server.
         */
        private void getUVReating() {
            URL url;
            HttpURLConnection urlConnection;
            InputStream response;
            try {
                url = new URL(UV_URL);
                urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                response = urlConnection.getInputStream();

                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jsonObject = new JSONObject(result);
                uvRating = jsonObject.get("value").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * method excuted after doInBackground
         * <p>
         * set data to the textView and Imageviews
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // set value to textviews
            currentTempTextView.setText( currentTemperature);
                minTempTextView.setText( minTemperature);
                maxTempTextView.setText( maxTemperature);
           uvRatingTempTextView.setText( uvRating);

            progressBar.setVisibility(View.INVISIBLE); // set progress bar visibility to invisible

            // check the bitmap is not null
            if (currentWeatherImage != null) {
                weatherImageView.setImageBitmap(currentWeatherImage);
            }


        }


        /**
         * load Image from file
         *
         * @Param imageName - image name used to query from files
         */
        private void getImageFRomFile(String imageName) {
            FileInputStream fis = null;
            try {
                fis = openFileInput(imageName);
                currentWeatherImage = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Method for checking the file is exist ro not
         *
         * @return -true if the file is exist
         * -false if the file does not exist
         */
        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }


        /**
         * download Image Bitmap form web server
         *
         * @Param urlString - url where the image is located
         */
        private void getBitMap(String urlString) {
            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    currentWeatherImage = BitmapFactory.decodeStream(connection.getInputStream());
                    FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                    currentWeatherImage.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}