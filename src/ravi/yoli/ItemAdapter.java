package ravi.yoli;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<Item> {
	private Context mContext;
	private List<Item> mItems;
	
	public ItemAdapter(Context context, List<Item> objects)
	{
		super(context, R.layout.place_row_item, objects);
		this.mContext = context;
		this.mItems = objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
	      if(convertView == null){
	          LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
	          convertView = mLayoutInflater.inflate(R.layout.place_row_item, null);
	      }

	      Item task = mItems.get(position);

	      TextView descriptionView = (TextView) convertView.findViewById(R.id.place_description);
	      
	      descriptionView.setText(task.getDescription());
	      // Need to associate this text with corresponding latitude and longitude
	      
	      return convertView;
	  }
	
	@TargetApi(11)
	// Workaround for addAll function as suggested in the following post
	// http://stackoverflow.com/questions/9677172/listviews-how-to-use-arrayadapter-addall-function-before-api-11
	public void addItems(List<Item> data) {
	    clear();
	    if (data != null) {
	        //If the platform supports it, use addAll, otherwise add in loop
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	            addAll(data);
	        }else{
	            for(Item item: data){
	                add(item);
	            }
	        }
	    }
	}
}	
