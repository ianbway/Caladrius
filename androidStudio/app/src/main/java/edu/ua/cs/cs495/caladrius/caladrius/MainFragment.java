package edu.ua.cs.cs495.caladrius.caladrius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class MainFragment extends Fragment {
    public MainFragment() {
        // Empty public constructor
    }

    public final GraphViewGraph[][] defaultGraphTypes = {
            {GraphViewGraph.BarGraph},
            {GraphViewGraph.PointsGraph},
            {GraphViewGraph.LineGraph,GraphViewGraph.BarGraph},
    };

    public final String[][] defaultGraphStats = {
            {"Heartrate"},
            {"CaloricBurn"},
            {"BPM", "asdf"},
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.graph_list, container, false);

        LinearLayout ll = rootView.findViewById(R.id.list);
        assert defaultGraphStats.length == defaultGraphTypes.length;
        for (int c = defaultGraphStats.length - 1; c >= 0; c--) {
            ArrayList<GraphViewGraph> graphTypes = new ArrayList<>();
            graphTypes.addAll(Arrays.asList(defaultGraphTypes[c]));

            ArrayList<String> stats = new ArrayList<>();
            stats.addAll(Arrays.asList(defaultGraphStats[c]));

            FitbitGraphView fgv = new FitbitGraphView(getContext(),
                    graphTypes,
                    stats,
                    false,
                    false,
                    false,
                    false
            );

            ll.addView(fgv, 0);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Button viewAllDataButton = getView().findViewById(R.id.viewAllData);
        viewAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent (v.getContext(), AllData.class);
                startActivityForResult(nextScreen, 0);
            }
        });
    }
}
