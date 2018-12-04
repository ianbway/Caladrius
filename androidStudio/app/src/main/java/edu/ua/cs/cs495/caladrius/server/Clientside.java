package edu.ua.cs.cs495.caladrius.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import android.support.annotation.NonNull;
import edu.ua.cs.cs495.caladrius.rss.Feed;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class Clientside
{
	public OkHttpClient client;

	public Clientside()
	{
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
		client = new OkHttpClient.Builder()
			//.addInterceptor(logging)
			.build();
	}

	public String[] getFeedIDs(ServerAccount sa) throws IOException
	{
		String userid = sa.uuid;
		final String URL = "https://caladrius.ivanjohnson.net/webapi/config/feeds";

		Request request = new Request.Builder()
			.url(URL)
			.addHeader("useruuid", userid)
			.build();

		Response r = client.newCall(request).execute();

		byte[] bytes = r.body().bytes();

		ByteArrayInputStream bais;
		Scanner s;

		bais = new ByteArrayInputStream(bytes);
		s = new Scanner(bais, "UTF-8");
		int numLines = 0;
		while(s.hasNextLine()) {
			numLines++;
			s.nextLine();
		}
		s.close();

		String feedids[] = new String[numLines];
		bais = new ByteArrayInputStream(bytes);
		s = new Scanner(bais, "UTF-8");
		while(s.hasNextLine()) {
			numLines--;
			feedids[numLines] = s.nextLine();
		}
		if (numLines != 0) {
			throw new RuntimeException("Server did not yield expected number of feeds");
		}
		s.close();

		return feedids;
	}

	protected String getFeedstring(String userid, String feeduuid) throws IOException
	{
		final String URL_BASE = "https://caladrius.ivanjohnson.net/webapi/config/feed";
		final String URL_QUERY_KEY = "id";

		final String url = URL_BASE + "?" + URL_QUERY_KEY + "=" + feeduuid;

		long lNow = System.currentTimeMillis() / 1000L;

		Request request = new Request.Builder()
			.url(url)
			.addHeader("useruuid", userid)
			.addHeader("MODDATE", Long.toString(lNow))
			.build();

		Scanner s = null;
		try {
			Response r = client.newCall(request)
			                   .execute();
			if (r.code() != 200) {
				return null;
			}

			s = new Scanner(r.body().byteStream(), "UTF-8");
			s.useDelimiter("\\A");
		} catch (IOException e) {
			return null;
		}

		String str = s.hasNext() ? s.next() : ""; // base64: ..., lastModify: ...
		String attribute = str.split(",")[0];     // base64: ...
		String base64 = attribute.split(" ")[1];  // ...

		s.close();
		return base64;
	}

	public void setFeed(ServerAccount sa, String feedid, String base64) throws IOException
	{
		String userid = sa.uuid;
		final String URL_BASE = "https://caladrius.ivanjohnson.net/webapi/config/feed";
		final String URL_QUERY_KEY = "id";
		final String url = URL_BASE + "?" + URL_QUERY_KEY + "=" + feedid;

		long lNow = System.currentTimeMillis() / 1000L;

		if (base64.length() > 100000) {
			throw new IllegalArgumentException("Given string is " + base64.length() + " char long; it must be less than 100000 to fit in the database.");
		}

		MediaType mt = MediaType.parse("text/plain; charset=utf-8");

		RequestBody body = RequestBody.create(mt, base64.getBytes("UTF-8"));

		Request request = new Request.Builder()
			.url(url)
			.put(body)
			.addHeader("useruuid", userid)
			.addHeader("MODDATE", Long.toString(lNow))
			.build();

		Response r = client.newCall(request)
		                   .execute();
		if (r.code() != 200) {
			throw new IOException("Server responded with code " + r.code());
		}
	}

	public Feed getFeed(ServerAccount sa, String uuid) throws IOException
	{
		String userid = sa.uuid;
		String base64 = getFeedstring(userid, uuid);
		byte bytes[] = Base64.getDecoder().decode(base64);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			Feed f = (Feed) ois.readObject();
			return f;
		} catch (ClassNotFoundException cnfe) {
			return null;
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

	public void setFeed(ServerAccount sa, @NonNull Feed f) throws IOException
	{
		if (f == null) {
			throw new NullPointerException("Cannot set feed to null");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(f);
			out.flush();
			byte[] bytes = bos.toByteArray();
			String base64 = Base64.getEncoder().encodeToString(bytes);
			setFeed(sa, f.uuid, base64);
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// NOP
			}
		}
	}

	public static void main(String args[]) throws IOException
	{
		final String UUID = "NoLogin";
		ServerAccount sa = new ServerAccount(UUID);
		sa.uuid = UUID;

		Clientside cs = new Clientside();
		for (int x = 0; x < 3; x++) {
			Feed fPush = new Feed("name #"+x);

			cs.setFeed(sa, fPush);
			Feed fPull = cs.getFeed(sa, fPush.uuid);

			System.out.println(fPush.toString());
			System.out.println(fPull.toString());
		}
	}
}
