package ravi.yoli;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Item")
public class Item extends ParseObject {

	  public Item(){

	  }

	  public double getLatitude()
	  {
		  return getDouble("latitude");
	  }
	  
	  public double getLongitude()
	  {
		  return getDouble("longitude");
	  }
	  
	  public void setLatitude(double latitude)
	  {
	      put("latitude", latitude);
	  }
	  
	  public void setLongitude(double longitude)
	  {
		  put("longitude", longitude);
	  }

	  public String getDescription()
	  {
	      return getString("description");
	  }

	  public void setDescription(String description)
	  {
	      put("description", description);
	  }
	  
}
