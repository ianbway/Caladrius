package edu.ua.cs.cs495.caladrius.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.apis.fitbit.FitBitOAuth2AccessToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import edu.ua.cs.cs495.caladrius.android.Caladrius;
import edu.ua.cs.cs495.caladrius.fitbit.Fitbit;
import edu.ua.cs.cs495.caladrius.fitbit.FitbitInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FitbitAndroid implements FitbitInterface {

    public FitbitAndroid() {
    }

    public static String[] getSupportedStats(Context context)
    {
        Resources resources = context.getResources();
        String[] stats = resources.getStringArray(R.array.array_graph_stats_options);
        return stats;
    }

    public JSONArray getFitbitData(String stat) throws JSONException, InterruptedException, ExecutionException, IOException
    {
        Fitbit.PseudoResponse ret = new GetDataCall().execute(stat).get();

        if (ret.code > 299 || ret.code < 200)
            throw new IOException(ret.body);

        JSONObject obj = new JSONObject(ret.body);
		/*System.out.println("\nOBJECT");
		System.out.println(obj.toString());*/

		/*JSONArray arr = obj.getJSONArray("activities-"+stat);
		System.out.println(String.format("\nARRAY %s", stat));
		System.out.println(arr.toString());*/

		/*System.out.println("\nVALUES");
		for(int i = 0; i < arr.length(); i++)
			System.out.println(arr.getJSONObject(i).getInt("value"));*/

        return obj.getJSONArray("activities-" + stat);
    }

    public void logout()
    {
        new RevokeToken().execute();
    }



    class GetDataCall extends AsyncTask<String, Void, Fitbit.PseudoResponse>
    {
        private final OAuth20Service service = new ServiceBuilder("22D7HK")
                .apiSecret("0eefb77c8b921283cb5e4477ac063178")
                .scope("activity heartrate location nutrition profile settings sleep social weight") // replace with desired scope
                //your callback URL to store and handle the authorization code sent by Fitbit
                .callback("caladrius://authcallback")
                //.state("some_params")
                .build(FitbitApi20.instance());


        protected Fitbit.PseudoResponse doInBackground(String... stat) {
            try{
                return new Fitbit().getFitbitData(Caladrius.user.fAcc, stat[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        protected void onPostExecute(Fitbit.PseudoResponse response) {}
    }


    class RevokeToken extends AsyncTask<Void, Void, String>
    {

        private final OAuth20Service service = new ServiceBuilder("22D7HK")
                .apiSecret("0eefb77c8b921283cb5e4477ac063178")
                .scope("activity heartrate location nutrition profile settings sleep social weight") // replace with desired scope
                //your callback URL to store and handle the authorization code sent by Fitbit
                .callback("caladrius://authcallback")
                //.state("some_params")
                .build(FitbitApi20.instance());


        protected String doInBackground(Void... things)
        {
            try {
                service.revokeToken(Caladrius.user.fAcc.getPrivateToken().toString());

                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String string)
        {
            try {
                Caladrius.user.fAcc = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}