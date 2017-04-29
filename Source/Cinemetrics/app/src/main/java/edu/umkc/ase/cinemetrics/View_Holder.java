package edu.umkc.ase.cinemetrics;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


//The adapters View Holder
public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView title;
    TextView description;
    ImageView imageView;
    ToggleButton watchtoggleButton;
    ToggleButton towatchtoggleButton;
    ToggleButton favtoggleButton;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        watchtoggleButton = (ToggleButton) itemView.findViewById(R.id.watchtoggleButton);
        towatchtoggleButton = (ToggleButton) itemView.findViewById(R.id.towatchtoggleButton);
        favtoggleButton = (ToggleButton) itemView.findViewById(R.id.favtoggleButton);
    }

}
