/*
* Code for MainActivity class; 
* that is invoked on starting the application
*/


package com.example.readingrssfeeddata;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	// creating global objects of the relevant classes 
	// that we will be working with

    ListView listRSS;
	ArrayList<String> titles, links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
		/* this is the function that is invoked first upon opening the app */
		super.onCreate(savedInstanceState);
		
		// setting up the display as customised in activity_main.xml file
        setContentView(R.layout.activity_main);
        
		// calling user-defined method to check if Network is available in the device
		if(isNetworkAvailable()) 
		{ 
			// network IS available in device, so app proceeds with
			//displaying of its contents
			
			// initialising the objects defined globally
			listRSS = (ListView) findViewById(R.id.listRSS);
            titles = new ArrayList<>();
            links = new ArrayList<>();

			// setting a listener to the list elements so that 
			// the clicks on the list item are recognised 
            listRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, 
										int position, long id) {
					/*
					* this method defines what action to perform 
					*  when a list item is clicked
					*/
					
					// gets the link from the ArrayList at a specified position
                    Uri uri = Uri.parse(links.get(position)); 

					
					// creating an intent which is to be invoked
					// when a list item is clicked
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    
					// starting the Activity by passing in the intent
					startActivity(intent);
                }
            });
			/* 
			* the following line of code creates a temporary instance of ProcessInBackground class
			* that calls the execute() method to perform operations in background.
			* the aforesaid class is defined at line 146
			*/
            new ProcessInBackground().execute();
        }
        else 
		{
			// Network is unavailable in the device, so 
			// the app displays a relevant Toast to the user 

            Toast.makeText(MainActivity.this, "There's a Network problem, 
							check your Data Settings or Connect to WiFi", 
							Toast.LENGTH_LONG).show();
        }
    }

	// method to check Network connectivity that  
	// returns true if network is available in device and false otherwise
    private boolean isNetworkAvailable() 
	{
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
		// aasserting that the returned value isn't null
         assert manager != null;

		 // getting Network information 
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		// checking if device is connected to a Network or not
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) 
		{
            isAvailable = true;
        }
        return isAvailable;
    }

	/* method that returns the input stream from the URL connection */
    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) // if an IOException occurs 
        {
            return null;
        }
    }
	
	/*
	* This class defines the processes to be performed in background
	* and the methods to be called while the app loads information
	* It extends AsyncTask class that is used for UI Multithreading purposes
	*/

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>
    {
		// creating a progressDialog box that shows status of loading information from website
        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() { 
            super.onPreExecute();
			// this enables showing the dialog box while the information is loaded 
            progressDialog.setMessage("Busy loading feed...please wait");
            progressDialog.show();
        }

		// method to load data from an xml file i.e. an RSS Feed in background
        @Override
        protected Exception doInBackground(Integer... params) {
            try
            {
				/* 
				* this block of code loads the Feed in background
				* We parse through the XML document as given in the URL through every tag
				* and pull up the values in the <item> tag, namely the <link> tag
				* we extract the text of the links and the titles,
				* and put them in their respective arraylists.
				*/
                
				// this is the URL of the site whose RSS Feed is to be displayed
				URL url = new URL("https://feeds.feedburner.com/ndtvnews-latest?format=xml");

                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);

				// variable to parse through various tags of the xml document
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

				// boolean variable that specifies whether we are inside an <item> tag or not
                boolean insideItem = false; 

                int eventType = xpp.getEventType();  // stores the current state of the parser
				
				//loop that iterates through all the XML elements we reach end of the file
                while(eventType != XmlPullParser.END_DOCUMENT) 
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        //check if tag is <item> or not
                        if(xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true; // the parser inside of the <item> tag
                        }
						
						// to check if we are at the <title> tag inside <item>
                        else if(xpp.getName().equalsIgnoreCase("title") && insideItem)
                        {
                            titles.add(xpp.nextText()); // adding the title to the list
                        }
						
						// to check if we are at the <link> tag inside <item>
                        else if(xpp.getName().equalsIgnoreCase("link") && insideItem)
                        {
                            links.add(xpp.nextText()); // adding the link to the list
                        }
                    }
					// to check if the parser is at the closing tag of item i.e. </item>
                    else if(eventType == XmlPullParser.END_TAG 
									  && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false; // the parser is now out of the parsed 'item' tag
                    }
                    eventType = xpp.next(); // advancing the parser to the next event
                }

            } 
			
			/* handling the various exceptions that might just occur while the app is running */
            
			catch (MalformedURLException e) // if the URL is malformed 
            {
                exception = e;
            }
            catch (XmlPullParserException e) // if an xml parser exception occurs
            {
                exception = e;
            }
            catch (IOException e) // if an IOException occurs 
            {
                exception = e;
            }

            return exception;
        }


        @Override
        protected void onPostExecute(Exception s) {
			/* method invoked on the UI thread after the background processes are done with */
            super.onPostExecute(s);

			// creating the list viewing layout with the titles of the links
            ArrayAdapter<String> adapter =
			new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_list_item_1, titles);
            
			// setting up the adapter as the listview property
			listRSS.setAdapter(adapter);
            
			// dismissing the dialog box once the list view is loaded successfully
			progressDialog.dismiss();
        }
    }
}

// Code is contributed by Soumitri Chattopadhyay
