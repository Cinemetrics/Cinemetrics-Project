package edu.umkc.ase.cinemetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class PieMainActivity extends Activity {

	public Spinner spinnerValue;
	String label;
	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pie_main);

		Spinner spinner=(Spinner)findViewById(R.id.spinner1);

		List<String> list = new ArrayList<>();
		list.add("Genre");
		list.add("Language");
		list.add("Director");
		list.add("Hero");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);


		final Random random = new Random();

		final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

		final PieChart p = new PieChart(this);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		p.setLayoutParams(lp);
		//p.setBackgroundColor(0xffffffff);
		p.setOnSliceClickListener(new PieChart.OnSliceClickListener() {
			@Override
			public void onSliceClicked(PieChart pieChart, int sliceNumber) {
				spinnerValue = (Spinner) findViewById(R.id.spinner1);
				switch (sliceNumber){
					case 0: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Comedy";
					} else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "karan";
					} else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "Hindi";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Shahrukh";
					} else
					{
						label = spinnerValue.getSelectedItem().toString() + "0";
					}
					break;
					case 1: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Drama";
					}
					else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "Darren Aronofsky";
					}else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "English";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Brad Pitt";
					}
					else
					{
						label = spinnerValue.getSelectedItem().toString() + "1";
					}
					break;
					case 2: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Thriller";
					}
					else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "Rajmouli";
					}else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "Hindi";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Prabhas";
					}
					else
					{
						label = spinnerValue.getSelectedItem().toString() + "2";
					}
						break;
					case 3: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Action";
					}
					else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "Sofia Coppola";
					}else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "English";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Tom Cruise";
					}
					else
					{
						label = spinnerValue.getSelectedItem().toString() + "3";
					}
						break;
					case 4: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Horror";
					}
					else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "Ram Gopal Varma";
					}else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "Hindi";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Abhishek";
					}
					else
					{
						label = spinnerValue.getSelectedItem().toString() + "4";
					}
						break;
					case 5: if(spinnerValue.getSelectedItem().toString().equals("Genre"))
					{
						label = "Romantic";
					}
					else if(spinnerValue.getSelectedItem().toString().equals("Director"))
					{
						label = "Ang Lee";
					}else if(spinnerValue.getSelectedItem().toString().equals("Language"))
					{
						label = "English";
					}else if(spinnerValue.getSelectedItem().toString().equals("Hero"))
					{
						label = "Will Smith";
					}
					else
					{
						label = spinnerValue.getSelectedItem().toString() + "1";
					}
						break;
					default:label="slice";

				}
				Toast.makeText(PieMainActivity.this, "slice: " + label, Toast.LENGTH_SHORT).show();
			}
		});
		layout.addView(p);

		spinnerValue = (Spinner) findViewById(R.id.spinner1);

			findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float[] data = new float[2 + random.nextInt(3)];
				for (int i = 0; i < data.length; i++) {
					data[i] = random.nextFloat();
				}
				p.setSlices(data);
				p.anima();
			}
		});
	}
}
