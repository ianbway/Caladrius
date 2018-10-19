package edu.ua.cs.cs495.caladrius.caladrius;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

enum GraphViewGraph
{
    LineGraph, BarGraph, PointsGraph;
}

public class FitbitGraphView extends GraphView
{
    // Instance Variables

    // Supplied graphType array list needs to be one of three enums,
    // there needs to be the same number of elements as the statsToRetrieve
    // ArrayList:
    // 1. "LineGraph"
    // 2. "BarGraph"
    // 3. "PointsGraph"
    ArrayList<GraphViewGraph> graphType;

    // Supplied statsToRetrieve ArrayList must contain relevant statistics
    // that can be retrieved via Fitbit API. Ex: BPM, BasalCaloricBurn, etc.
    // Must have same number of elements as graphType list.
    ArrayList<String> statsToRetrieve;

    // Supplied horizontalScroll Boolean enables/disables horizontal
    // scrolling through the graph
    Boolean horizontalScroll;

    // Supplied verticalScroll Boolean enables/disables vertical
    // scrolling through the graph
    Boolean verticalScroll;

    // Supplied horizontalZoomAndScroll Boolean enables/disables horizontal
    // scrolling through the graph and horizontal zooming
    Boolean horizontalZoomAndScroll;

    // Supplied verticalZoomAndScroll Boolean enables/disables vertical
    // scrolling through the graph and vertical zooming
    Boolean verticalZoomAndScroll;

    // converts from density independent pixels to pixels
    private int pxFromDip(int dip)
    {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        return Math.round(px);
    }

    // Constructor
    public FitbitGraphView(final Context context, ArrayList<GraphViewGraph> graphType,
                           ArrayList<String> statsToRetrieve,
                           Boolean horizontalScroll, Boolean verticalScroll,
                           Boolean horizontalZoomAndScroll,
                           Boolean verticalZoomAndScroll) {
        super(context);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        //int pxPadding = pxFromDip(30);
        //params.setMargins(pxPadding, pxPadding, pxPadding, pxPadding);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxFromDip(300)));
        //setLayoutParams(params);
        //setPaddingRelative(pxPadding, pxPadding, pxPadding, pxPadding);

        this.graphType = graphType;
        this.statsToRetrieve = statsToRetrieve;
        this.horizontalScroll = horizontalScroll;
        this.verticalScroll = verticalScroll;
        this.horizontalZoomAndScroll = horizontalZoomAndScroll;
        this.verticalZoomAndScroll = verticalZoomAndScroll;

        makeGraphViewGraph();

        // Navigate to the split graph/data view
        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent submitPage = new Intent (context, GraphActivity.class);
                submitPage.putExtra("startDate", "N/A");
                submitPage.putExtra("endDate", "N/A");
                submitPage.putExtra("item_1", "N/A");
                submitPage.putExtra("item_2", "N/A");
                context.startActivity(submitPage);
            }
        });
    }

    // Getter Methods

    public ArrayList<GraphViewGraph> getGraphType()
    {
        return this.graphType;
    }

    public ArrayList<String> getStatsToRetrieve()
    {
        return this.statsToRetrieve;
    }

    public Boolean getHorizontalScroll()
    {
        return this.horizontalScroll;
    }

    public Boolean getVerticalScroll()
    {
        return this.verticalScroll;
    }

    public Boolean getHorizontalZoomAndScroll()
    {
        return this.horizontalZoomAndScroll;
    }

    public Boolean getVerticalZoomAndScroll()
    {
        return this.verticalZoomAndScroll;
    }

    // Setter Methods
    public void setGraphType(ArrayList<GraphViewGraph> gType)
    {
        this.graphType = gType;
    }

    public void setStatsToRetrieve(ArrayList<String> sToRetrieve)
    {
        this.statsToRetrieve = sToRetrieve;
    }

    public void setHorizontalScroll(Boolean hScroll)
    {
        this.horizontalScroll = hScroll;
    }

    public void setVerticalScroll(Boolean vScroll)
    {
        this.verticalScroll = vScroll;
    }

    public void setHorizontalZoomAndScroll(Boolean hZScroll)
    {
        this.horizontalZoomAndScroll = hZScroll;
    }

    public void setVerticalZoomAndScroll(Boolean vZScroll)
    {
        this.verticalZoomAndScroll = vZScroll;
    }

    private void scrollHandler(GraphView g)
    {
        if (getHorizontalScroll() == true)
        {
            g.getViewport().setScrollable(true);
        }

        if (getVerticalScroll() == true)
        {
            g.getViewport().setScrollableY(true);
        }

        if (getHorizontalZoomAndScroll() == true)
        {
            g.getViewport().setScalable(true);
        }

        if (getVerticalZoomAndScroll() == true)
        {
            g.getViewport().setScalableY(true);
        }
    }

    private void makeGraphViewGraph()
    {
        for (int i=0; i<this.graphType.size(); i++)
        {
            DataPoint[] points = new DataPoint[] {
                    // Some sort of loop creating DataPoint objects from
                    // whatever you want to plot, getting information from
                    // FitBit API.

                    //* Delete first / to demo bug
                    new DataPoint(00, 20),
                    new DataPoint(05, 10),
                    new DataPoint(10, 15),
                    new DataPoint(15, 00),
                    /*/
                    new DataPoint(0.0, 2.0),
                    new DataPoint(0.5, 1.0),
                    new DataPoint(1.0, 1.5),
                    new DataPoint(1.5, 0.0),
                    //*/

                    // Use this.statsToRetrieve[i] to find out what we are
                    // plotting at this point in the loop. Send that
                    // string to the fitbit API.
            };

            Series<DataPoint> series;
            // LineGraph
            if (this.graphType.get(i).equals(GraphViewGraph.LineGraph))
            {
                series = new LineGraphSeries<>(points);
            }

            // BarGraph
            else if (this.graphType.get(i).equals(GraphViewGraph.BarGraph))
            {
                series = new BarGraphSeries<>(points);
            }

            // PointsGraph
            else
            {
                series = new PointsGraphSeries<>(points);
            }

            this.addSeries(series);
            scrollHandler(this);
        }
    }
}
