package com.filipe.controlevideomapping;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.net.*;
import java.util.*;

import com.illposed.osc.*;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /* These two variables hold the IP address and port number.
       * You should change them to the appropriate address and port.
       */
    private static String myIP = "192.168.1.200.";
    private static int myPort = 6666;

    // This is used to send messages
    private static OSCPortOut oscPortOut;

    // This thread will contain all the code that pertains to OSC
    private static Thread oscThread = new Thread() {
        @Override
        public void run() {
      /* The first part of the run() method initializes the OSCPortOut for sending messages.
       *
       * For more advanced apps, where you want to change the address during runtime, you will want
       * to have this section in a different thread, but since we won't be changing addresses here,
       * we only have to initialize the address once.
       */

            try {
                // Connect to some IP address and port
                oscPortOut = new OSCPortOut(InetAddress.getByName(myIP), myPort);
            } catch(UnknownHostException e) {
                // Error handling when your IP isn't found
                return;
            } catch(Exception e) {
                // Error handling for any other errors
                return;
            }


      /* The second part of the run() method loops infinitely and sends messages every 500
       * milliseconds.
       */
            while (true) {
                if (oscPortOut != null) {
                    // Creating the message
                    Object[] thingsToSend = new Object[3];
                    thingsToSend[0] = "Hello World";
                    thingsToSend[1] = 12345;
                    thingsToSend[2] = 1.2345;

          /* The version of JavaOSC from the Maven Repository is slightly different from the one
           * from the download link on the main website at the time of writing this tutorial.
           *
           * The Maven Repository version (used here), takes a Collection, which is why we need
           * Arrays.asList(thingsToSend).
           *
           * If you're using the downloadable version for some reason, you should switch the
           * commented and uncommented lines for message below
           */
                    OSCMessage message = new OSCMessage(myIP, Arrays.asList(thingsToSend));
                    // OSCMessage message = new OSCMessage(myIP, thingsToSend);


          /* NOTE: Since this version of JavaOSC uses Collections, we can actually use ArrayLists,
           * or any other class that implements the Collection interface. The following code is
           * valid for this version.
           *
           * The benefit of using an ArrayList is that you don't have to know how much information
           * you are sending ahead of time. You can add things to the end of an ArrayList, but not
           * to an Array.
           *
           * If you want to use this code with the downloadable version, you should switch the
           * commented and uncommented lines for message2
           */
                    ArrayList<Object> moreThingsToSend = new ArrayList<Object>();
                    moreThingsToSend.add("Hello World2");
                    moreThingsToSend.add(123456);
                    moreThingsToSend.add(12.345);

                    OSCMessage message2 = new OSCMessage(myIP, moreThingsToSend);
                    //OSCMessage message2 = new OSCMessage(myIP, moreThingsToSend.toArray());

                    try {
                        // Send the messages
                        oscPortOut.send(message);
                        oscPortOut.send(message2);

                        // Pause for half a second
                        sleep(500);
                    } catch (Exception e) {
                        // Error handling for some error
                    }
                }
            }
        }
    };
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button bt = (Button) rootView.findViewById(R.id.buttonPlay);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oscThread.start();
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
