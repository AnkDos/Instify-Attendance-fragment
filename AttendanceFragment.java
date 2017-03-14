package srm.erp.app.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import srm.erp.app.R;
import srm.erp.app.activity.MainActivity;
import srm.erp.app.additional.ListAdapterExpandible;
import srm.erp.app.app.AppConfig;
import srm.erp.app.app.AppController;
import srm.erp.app.helper.SQLiteHandler;






 class Att {

    private  double pre;
    private  double ttl;
    double main_attendance;
    //Variables
    int count = 0, num = 1, denom = 1;
   int countp = 0, deno = 1;

    //****************************************************************************
    public  double attnCalc(double present, double total) {
        pre = present;
        ttl = total;
        main_attendance = pre / ttl * 100;


        while (true) {

            double current_attendance = ((present + num) / (total + denom)) * 100;
            num++;
            denom++;

            if (current_attendance > 75) {
                break;
            }
            count++;
        }
        return count;
    }

    //****************************************************************************
    public  double predict() {

        if (main_attendance > 75) {
            while (deno <= 1000) {

                double predicted_attandance = ((pre) / (ttl + deno)) * 100;
                if (predicted_attandance >= 75) {
                    countp++;
                }
                deno++;
            }

        }
        return countp;
    }

    //****************************************************************************
}











public class AttendanceFragment extends Fragment {
    //ProgressDialog pDialog = new ProgressDialog(getContext());
   // SQLiteHandler db;

    public AttendanceFragment() {

    }

    public static AttendanceFragment newInstance() {
        AttendanceFragment frag = new AttendanceFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
frag.getattendance();
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getContext()).mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableListView expListView;

   // private BarChart mChart;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_attendance, container, false);
//        mSwipeRefreshLayout = (SwipeRefreshLayout)
//                rootView.findViewById(R.id.swipe_refresh_layout_attendance);
        expListView = (ExpandableListView)rootView.findViewById(R.id.expListView);

     return rootView;
    }






    private void getattendance() {
        // Tag used to cancel the request
        String tag_string_req = "req_attendance";


//        pDialog.setMessage("loading");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETATTANDENCE, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Login Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        ListAdapterExpandible adapter;
                    //    ExpandableListView expListView;
                        //expListView = (ExpandableListView)

                        // declare array List for all headers in list
                        ArrayList<String> headersArrayList = new ArrayList<String>();

                        // Declare Hash map for all headers and their corresponding values
                        HashMap<String, ArrayList<String>> childArrayList = new HashMap<String, ArrayList<String>>();

//                        expListView = (ExpandableListView)findViewById(R.id.expListView);
                         JSONArray user = jObj.getJSONArray("subjects");

                        int i;

                        double ar[],br[];
                        ar=new double[100];
                        br=new double[100];
                        for(i=0;i<user.length();i++) {
                            Att obj=new Att();
                            String name = user.getString(i);
                            JSONObject subs = jObj.getJSONObject(user.getString(i));

;

                            ArrayList<String> daysOfWeekArrayList = new ArrayList<String>();
                            headersArrayList.add(name +"-"+subs.getString("sub-desc")+" "+subs.getString("avg-attd")+"%");

                           // daysOfWeekArrayList.add(subs.getString("sub-desc"));
                            daysOfWeekArrayList.add("MAX-HOURS: "+subs.getString("max-hrs"));
                            daysOfWeekArrayList.add("ATTENDED-HOURS: "+subs.getString("attd-hrs"));
                            daysOfWeekArrayList.add("ABSENT-HOURS: "+subs.getString("abs-hrs")+"  PERCENTAGE: "+subs.getString("avg-attd")+"%");
                           // daysOfWeekArrayList.add();
                 ar[i]= Double.parseDouble(subs.getString("attd-hrs"));
                            br[i]= Double.parseDouble(subs.getString("max-hrs"));
double tempa=ar[i];
                            double tempb=br[i];

                           double resultA =obj.attnCalc(tempa,tempb);
                            double resultB =obj.predict();
                            daysOfWeekArrayList.add("Total Classes Needed To hit 75% :  "+resultA);
                            daysOfWeekArrayList.add("May Take Leave For Next: "+resultB +" consecutive classes *if taken ");


                            childArrayList.put(name +"-"+subs.getString("sub-desc")+" "+subs.getString("avg-attd")+"%", daysOfWeekArrayList);

 }






                        adapter = new ListAdapterExpandible(getContext(), headersArrayList,childArrayList);

                        expListView.setAdapter(adapter);

                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v,
                                                        int groupPosition, int childPosition, long id) {
                              //  Toast.makeText(getContext(), "Child is clicked", Toast.LENGTH_LONG).show();
                                return false;
                            }
                        });

                        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                        int groupPosition, long id) {

                               // Toast.makeText(getContext(), "Group is Clicked", Toast.LENGTH_LONG).show();
                                return false;
                            }
                        });
                        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                            @Override
                            public void onGroupCollapse(int groupPosition) {

                                //Toast.makeText(getContext(), "Child is Collapsed", Toast.LENGTH_LONG).show();
                            }
                        });

                        final ExpandableListView finalExpListView = expListView;
                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            int previousGroup = -1;
                            @Override
                            public void onGroupExpand(int groupPosition) {


                                if(groupPosition != previousGroup)
                                    finalExpListView.collapseGroup(previousGroup);
                                previousGroup = groupPosition;

                              //  Toast.makeText(getContext(), "Child is Expanded", Toast.LENGTH_LONG).show();
                            }
                        });













                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
              //  Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                SQLiteHandler db =  new SQLiteHandler(getContext());
              String pass =   db.getUserDetails().get("token");
              String unm = db.getUserDetails().get("regno");

                params.put("regno", unm);
                params.put("pass", pass);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//
//    }



    private void showRefreshing() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }



}



