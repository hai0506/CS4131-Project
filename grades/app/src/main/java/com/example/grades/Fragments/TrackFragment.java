package com.example.grades.Fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grades.Misc.ForegroundService;
import com.example.grades.R;
import com.example.grades.Adapters.RecyclerAdapter3;
import com.example.grades.Misc.SharedViewModel;

import java.util.ArrayList;

public class TrackFragment extends Fragment {
    LinearLayout trackContainer, trackBtn;
    TextView trackText, psiTV, uvTV,timerTV,consequenceTV;
    TextView ntext;
    SharedViewModel sharedViewModel;
    ScrollView scrollView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter3 adapter;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        trackContainer = v.findViewById(R.id.buttonContainer);
        trackBtn = v.findViewById(R.id.trackBtn);
        trackText = v.findViewById(R.id.trackText);
        psiTV = v.findViewById(R.id.psiTV);
        uvTV = v.findViewById(R.id.uvTV);
        timerTV = v.findViewById(R.id.timerTV);
        consequenceTV = v.findViewById(R.id.consequenceTV);
        scrollView = v.findViewById(R.id.scrollView);
        if(!isMyServiceRunning(ForegroundService.class)) trackText.setText("Start Tracking");
        else trackText.setText("Stop Tracking");
        Intent service = new Intent(getActivity(), ForegroundService.class);

        if(isMyServiceRunning(ForegroundService.class) && ntext == null) {
            ntext = new TextView(getContext());
            LinearLayout.LayoutParams nlparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width Of The TextView
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ntext.setLayoutParams(nlparams);
            ntext.setTextSize(28);
            ntext.setTextColor(getResources().getColor(R.color.black));
            ntext.setPadding(0,10,0,0);
            ntext.setAlpha(0);
            trackBtn.addView(ntext);
            ntext.animate().alpha(1).setDuration(300);
            sharedViewModel.getTime().observe(getViewLifecycleOwner(),item-> {
                long hour = item / 3600;
                long minute = (item - hour*3600)/60;
                long seconds = item % 60;
                ntext.setText(hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds));
            });
        }
        recyclerView = v.findViewById(R.id.imageContainer);
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!isMyServiceRunning(ForegroundService.class)) {
                        trackText.setText("Stop Tracking");
                        service.putExtra("psi", sharedViewModel.getPsi().getValue());
                        service.putExtra("uv", sharedViewModel.getUv().getValue());
                        getActivity().startService(service);
                        ntext = new TextView(getContext());
                        LinearLayout.LayoutParams nlparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, // Width Of The TextView
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        ntext.setLayoutParams(nlparams);
                        ntext.setTextSize(28);
                        ntext.setTextColor(getResources().getColor(R.color.black));
                        ntext.setPadding(0,10,0,0);
                        ntext.setAlpha(0);
                        trackBtn.addView(ntext);
                        ntext.animate().alpha(1).setDuration(500);
                        sharedViewModel.getTime().observe(getViewLifecycleOwner(),item-> {
                            long hour = item / 3600;
                            long minute = (item - hour*3600)/60;
                            long seconds = item % 60;
                            ntext.setText(hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds));
                        });
                        slide2();
                    } else {
                        trackText.setText("Start Tracking");
                        getActivity().stopService(service);
                        trackBtn.removeViewAt(1);
                        long item = sharedViewModel.getTime().getValue();
                        long hour = item / 3600;
                        long minute = (item - hour*3600)/60;
                        long seconds = item % 60;
                        timerTV.setText(hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds));
                        slide();
                    }
                }
                else {
                    Toast.makeText(getContext(),"This feature is unavailable, please grant the necessary permissions",Toast.LENGTH_LONG).show();
                }
            }
        });


        return v;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver,new IntentFilter("service_result"));
        if(!isMyServiceRunning(ForegroundService.class)) trackText.setText("Start Tracking");
        else trackText.setText("Stop Tracking");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Double> result = (ArrayList<Double>) intent.getExtras().get("result");
            if(result!=null) {
                Log.d("debug",result.toString());
                    double sum = 0;
                    for (double i : result) {
                        sum += i;
                    }
                    double psiAvg = sum / result.size();
                    ArrayList<Long> result2 = (ArrayList<Long>) intent.getExtras().get("result2");
                    long sum2 = 0;
                    for (long i : result2) {
                        sum2 += i;
                    }
                    double uvAvg = ((double) sum2) / result2.size();
                    String a = psiAvg+"";
                    if(a.equals("NaN")) {
                        psiAvg = 0;
                        uvAvg = 0;
                    }
                    psiTV.setText(String.format("%.1f", psiAvg));
                    uvTV.setText(String.format("%.1f", uvAvg));
                    updateHealthEffects(psiAvg, uvAvg);
            }
            sharedViewModel.setTime(intent.getLongExtra("seconds",0));
        }
    };

    public void slide(){
        ValueAnimator widthAnimator = ValueAnimator.ofInt(trackContainer.getHeight(), 600);
        widthAnimator.setDuration(500);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                trackContainer.getLayoutParams().height = (int) animation.getAnimatedValue();
                trackContainer.requestLayout();
            }
        });
        widthAnimator.start();
    }
    public void slide2(){
        int parentWidth = ((View)trackContainer.getParent()).getMeasuredHeight();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(trackContainer.getHeight(), parentWidth);
        widthAnimator.setDuration(500);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                trackContainer.getLayoutParams().height = (int) animation.getAnimatedValue();
                trackContainer.requestLayout();
            }
        });
        widthAnimator.start();
    }

    private void updateHealthEffects(double psiAvg, double uvAvg) {
        ArrayList<Integer> images = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<String> advice = new ArrayList<>();
        if(psiAvg<=50) {}
        else if (psiAvg <= 100) {
            images.add(R.drawable.cough_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.yellow));
            advice.add("Cause coughing and wheezing");
        } else if (psiAvg <= 200) {
            images.add(R.drawable.lungs_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.orange));
            advice.add("Cause lung damage and other respiratory disorders");
        } else {
            images.add(R.drawable.lungs_icon);
            images.add(R.drawable.heart_icons);
            colors.add(ContextCompat.getColor(mContext,R.color.red));
            colors.add(ContextCompat.getColor(mContext,R.color.red));
            advice.add("Cause serious respiratory disorders");
            advice.add("Lead to heart disorders and strokes");
        }
        if (uvAvg == 0) {
            images.add(R.drawable.vitamin_d_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.yellow));
            advice.add("Cause vitamin D deficiency");
        }
        else if (uvAvg <= 2) {
            images.add(R.drawable.vitamin_d_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.green));
            advice.add("Help your body synthesize vitamin D");
        }
        else if (uvAvg <= 5) {
            images.add(R.drawable.skin_disorders_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.yellow));
            advice.add("Cause sunburn in 30 - 45 minutes");
        } else if (uvAvg <= 7) {
            images.add(R.drawable.skin_disorders_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.orange));
            advice.add("Lead to skin disorders");
        } else {
            images.add(R.drawable.skin_disorders_icon);
            images.add(R.drawable.eye_disorders_icon);
            colors.add(ContextCompat.getColor(mContext,R.color.red));
            colors.add(ContextCompat.getColor(mContext,R.color.red));
            advice.add("Lead to skin disorders");
            advice.add("Cause eye problems");
        }

        adapter = new RecyclerAdapter3(images, colors, advice);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new RecyclerAdapter3.onItemClickListener() {
            @Override
            public void onClick(String str) {
                consequenceTV.setText(str);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}