package edu.umkc.ase.cinemetrics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.umkc.ase.cinemetrics.managers.SharedPreferenceManager;
import edu.umkc.ase.cinemetrics.model.MoviesModel;
import edu.umkc.ase.cinemetrics.model.UserModel;

public class PieMainActivity extends AppCompatActivity {


	@Bind(R.id.nav_view)
	NavigationView navigationView;

	@Bind(R.id.layout)
	DrawerLayout drawerLayout;

	SimpleDraweeView simpleDraweeView;

	TextView nameTextView;

	TextView emailTextView;

	UserModel userModel = null;
	String userName = null;


	MongoClientURI uri = null;
	MongoClient client = null;
	MongoDatabase db = null;

	List<MoviesModel> movielist = null;
	Map<String,Integer> languageMap =null;
	Map<String,Integer> genreMap = null;
	Map<String,Integer> dateMap = null;

	public Spinner spinnerValue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pie_main);

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setItemIconTintList(null);

		final PieChart pieChart = (PieChart) findViewById(R.id.pchart);
		Spinner spinner=(Spinner)findViewById(R.id.spinner1);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		ButterKnife.bind(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.menu);
		ab.setDisplayHomeAsUpEnabled(true);

		userModel = (UserModel) getIntent().getParcelableExtra("userModelClass");

		if(userModel!=null){
			hideItem(false);
			userName = userModel.userEmail;
		}else{
			hideItem(true);
		}
		// if(userModel!=null)
		setDataOnNavigationView(userModel);

		java.security.Security.addProvider(new gnu.javax.crypto.jce.GnuSasl());
		uri  = new MongoClientURI("mongodb://moviedb:moviedb@ds155820.mlab.com:55820/movies_collection");
		client = new MongoClient(uri);
		db = client.getDatabase(uri.getDatabase());

		movielist = new ArrayList<MoviesModel>();

		MongoCollection<Document> moviesCollection = db.getCollection("watched_movies");

		Document findQuery = new Document("username",userModel.userEmail);

		MongoCursor<Document> cursor = moviesCollection.find(findQuery).iterator();


		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				MoviesModel movie = new MoviesModel(doc.get("movieTitle").toString(),doc.get("genre").toString(),doc.get("moviePoster").toString(),doc.get("releaseDate").toString(),doc.get("language").toString());
				movielist.add(movie);

			}
		} finally {
			cursor.close();
		}


		languageMap = new LinkedHashMap<String,Integer>();
		genreMap = new LinkedHashMap<String,Integer>();
		dateMap = new LinkedHashMap<String,Integer>();

		for(MoviesModel movie: movielist){
			if(movie.getLanguage()!=null) {
				if (!languageMap.containsKey(movie.getLanguage()))
					languageMap.put(movie.getLanguage(), 1);
				else
					languageMap.put(movie.getLanguage(), languageMap.get(movie.getLanguage()) + 1);
			}
			if(movie.getGenre()!=null){
				String[] genre = movie.getGenre().split(",");
				for(String str:genre){
					if(str!=null) {
						if (!genreMap.containsKey(str))
							genreMap.put(str, 1);
						else
							genreMap.put(str, genreMap.get(str) + 1);
					}
				}
			}
			if(movie.getReleaseDate()!=null){
				String date = movie.getReleaseDate().split("-")[0];
				if (!dateMap.containsKey(date))
					dateMap.put(date, 1);
				else
					dateMap.put(date, dateMap.get(date) + 1);
			}
		}

		List<String> dropdownlist = new ArrayList<>();
		dropdownlist.add("Genre");
		dropdownlist.add("Language");
		dropdownlist.add("Year");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dropdownlist);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		spinnerValue = (Spinner) findViewById(R.id.spinner1);


		spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				String  mselection=spinnerValue.getSelectedItem().toString();


					List<PieEntry> entries = new ArrayList<>();
					PieDataSet dataset = new PieDataSet(entries,"# of Watched Movies");

					final List<String> labels = new ArrayList<String>();
					List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();

				Iterator it = null;
				int[] colors = null;
				if("language".equalsIgnoreCase(mselection)) {
					it = languageMap.entrySet().iterator();
					colors = new int[languageMap.size()];
				}else if("genre".equalsIgnoreCase(mselection)){
					it = genreMap.entrySet().iterator();
					colors = new int[genreMap.size()];
				}else if("year".equalsIgnoreCase(mselection)){
					it = dateMap.entrySet().iterator();
					colors = new int[dateMap.size()];
				}
					int count=0;
					Random rand = new Random();

					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry)it.next();
						labels.add(pair.getKey().toString());
						LegendEntry legendEntry = new LegendEntry();
						legendEntry.label = pair.getKey().toString();
						int r = rand.nextInt(255);
						int g = rand.nextInt(255);
						int b = rand.nextInt(255);
						legendEntry.formColor = Color.rgb(r,g,b);
						colors[count] = Color.rgb(r,g,b);
						legendEntries.add(legendEntry);

						entries.add(new PieEntry(Float.parseFloat(pair.getValue().toString()), count));
						count++;
					}


					dataset.setColors(colors);
					dataset.setSliceSpace(2);
					dataset.setValueTextSize(12);


					Legend l = pieChart.getLegend();

					l.setFormSize(10f); // set the size of the legend forms/shapes
					l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
					l.setXOffset(3f);
					l.setTextSize(12f);
					l.setTextColor(Color.WHITE);
					l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
					l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
					l.setCustom(legendEntries);
					l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
					l.setOrientation(Legend.LegendOrientation.VERTICAL);


					PieData data =  new PieData(dataset);
					pieChart.setData(data);
					pieChart.setHoleRadius(25);
					pieChart.setCenterText(String.valueOf(movielist.size()));
					pieChart.setCenterTextColor(Color.BLACK);
					pieChart.setCenterTextSize(16f);
					pieChart.setExtraTopOffset(20);
					pieChart.animateY(5000);

					pieChart.invalidate();



				pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
					@Override
					public void onValueSelected(Entry e, Highlight h) {

						Toast.makeText(PieMainActivity.this,labels.get((int)h.getX()) +": "+(int)h.getY()+" Movies", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNothingSelected() {

					}
				});

			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				//
			}
		});




	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_another, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onSignOut(View view) {
		finish();
	}

	private UserModel getUserModelFromIntent()
	{
		Intent intent = getIntent();
		return intent.getParcelableExtra(UserModel.class.getSimpleName());
	}

	private void setDataOnNavigationView(UserModel userModel) {
		if (navigationView != null) {
			setupDrawerContent(userModel);
		}

		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						menuItem.setChecked(true);
						switch (menuItem.getItemId()) {
							case R.id.nav_sign_out:
								drawerLayout.closeDrawers();
								SharedPreferenceManager.getSharedInstance().clearAllPreferences();
								startLoginActivity();
								return true;
							case R.id.nav_favourite:
								drawerLayout.closeDrawers();
								SharedPreferenceManager.getSharedInstance().clearAllPreferences();
								startWatchActivity();
								return true;
							case R.id.nav_toBeWatchedList:
								drawerLayout.closeDrawers();
								SharedPreferenceManager.getSharedInstance().clearAllPreferences();
								startToWatchActivity();
								return true;
							case R.id.nav_watchedList:
								drawerLayout.closeDrawers();
								SharedPreferenceManager.getSharedInstance().clearAllPreferences();
								startFavActivity();
								return true;
							default:
								return true;
						}
					}
				});
	}

	private void hideItem(boolean newUser)
	{
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		Menu nav_Menu = navigationView.getMenu();

		if(newUser) {
			nav_Menu.findItem(R.id.nav_watchedList).setVisible(false);
			nav_Menu.findItem(R.id.nav_toBeWatchedList).setVisible(false);
			nav_Menu.findItem(R.id.nav_favourite).setVisible(false);
			nav_Menu.findItem(R.id.nav_sign_out).setVisible(false);
		}else
			nav_Menu.findItem(R.id.nav_sign_in).setVisible(false);
	}

	private void startWatchActivity() {
		Intent intent = new Intent(this, WatchedActivity.class);
		intent.putExtra("userModelClass", userModel);
		startActivity(intent);
		finishAffinity();
	}

	private void startToWatchActivity() {
		Intent intent = new Intent(this, ToBeWatchedActivity.class);
		intent.putExtra("userModelClass", userModel);
		startActivity(intent);
		finishAffinity();
	}

	private void startFavActivity() {
		Intent intent = new Intent(this, FavouriteActivity.class);
		intent.putExtra("userModelClass", userModel);
		startActivity(intent);
		finishAffinity();
	}

	private void setupDrawerContent(UserModel userModel) {
		View headerView = navigationView.getHeaderView(0);

		simpleDraweeView = ButterKnife.findById(headerView, R.id.user_imageview);
		simpleDraweeView.setImageURI(Uri.parse(userModel.profilePic));

		nameTextView = ButterKnife.findById(headerView, R.id.name_textview);
		nameTextView.setText(userModel.userName);

		emailTextView = ButterKnife.findById(headerView, R.id.email_textview);
		emailTextView.setText(userModel.userEmail);
	}
	private void startLoginActivity()
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finishAffinity();
	}
}
