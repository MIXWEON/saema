package saema.cti.com.saemacenaim;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonPolygon;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import saema.cti.com.saemacenaim.utils.JSONParser;

public class RegionFragment extends Fragment implements
        OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = RegionFragment.class.getSimpleName();
    private GoogleMap mMap;
    String anio_selected = "";
    JSONObject JsonColors = null;
    static JSONObject jObj = null;
    Spinner mes_spinner;

    private AlertaInfoFragment.OnFragmentInteractionListener mListener;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    SupportMapFragment mapFragment;
    Button search;
    JSONObject JsonData = null;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    GeoJsonLayer layer;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_region, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlertaInfoFragment.OnFragmentInteractionListener) {
            mListener = (AlertaInfoFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        mes_spinner = (Spinner) getView().findViewById(R.id.spinner_mes);
        search = (Button) getView().findViewById(R.id.btn_search);
        final Spinner anio_spinner = (Spinner) getView().findViewById(R.id.spinner_anio);

        List<String> mes =  new ArrayList<String>();
        mes.add("Enero");
        mes.add("Febrero");
        mes.add("Marzo");
        mes.add("Abril");
        mes.add("Mayo");
        mes.add("Junio");
        mes.add("Julio");
        mes.add("Agosto");
        mes.add("Septiembre");
        mes.add("Octubre");
        mes.add("Noviembre");
        mes.add("Diciembre");

        List<String> anio =  new ArrayList<String>();
        Date currentDate = new Date();
        Calendar year = Calendar.getInstance();
        int anio_string = year.get(Calendar.YEAR);
        int diff = anio_string - 2000;
        int inicio = 2000;
        for(int i=0; i<=diff;i++){
            anio.add(i, String.valueOf(inicio));
            inicio = inicio + 1;
        }

        ArrayAdapter<String> adapterAnio = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, anio);
        ArrayAdapter<String> adapterMes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mes);

        adapterAnio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        anio_spinner.setAdapter(adapterAnio);
        mes_spinner.setAdapter(adapterMes);
        mes_spinner.setSelection(11);
        anio_spinner.setSelection(diff);
        anio_selected = anio_spinner.getSelectedItem().toString();

        /*anio_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                anio_selected = anio_spinner.getSelectedItem().toString();
                new JsonColorsTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });*/

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anio_selected = anio_spinner.getSelectedItem().toString();
                new JsonColorsTask().execute();

            }
        });

        buildGoogleApiClient();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMaxZoomPreference(20);
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(-2.5917844,-80.0830797) , 9) );


        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient.
     * Uses the addApi() method to request the Google Places API and the Fused Location Provider.
     */
    private synchronized void buildGoogleApiClient() {


        new JsonDataTask().execute();
        //new JsonColorsTask().execute();

       // mMap.data.addGeoJson(JsonData);

//layer.getFeature("28").setPolygonStyle();

    }
    private class JsonDataTask extends AsyncTask<String, Integer, String> {
        private ProgressBar miBarraDeProgreso;

        @Override
        protected void onPreExecute() {

            miBarraDeProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miBarraDeProgreso.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... urls) {
            String data ="";
            data = JSONParser.getJSONFromUrl("http://200.10.150.98:8080/geoserver/ws_saema/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=ws_saema:grillas_celdas&srsName=EPSG:4326&outputFormat=application/json");
            return data;
        }

        protected void onPostExecute(String result) {
            if (result != null){
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(result);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                layer = new GeoJsonLayer(mMap, jObj);
                layer.addLayerToMap();
            }else{
                Toast.makeText(getActivity(),"Problema de conexion",Toast.LENGTH_LONG).show();
            }
            miBarraDeProgreso.setVisibility(View.GONE);
            //showDialog("Downloaded " + result + " bytes");
        }
    }

    private class JsonColorsTask extends AsyncTask<String, Integer, String> {
        private ProgressBar miProgreso;

        @Override
        protected void onPreExecute() {

            miProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miProgreso.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(String... urls) {
            String colors = "";
            colors = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/ColorCeldas/?anio="+anio_selected);
            return colors;
        }

        protected void onPostExecute(String result) {
            //Log.e("Json", "el final " + result.toString());
            JSONArray arr = null;
            ArrayList arr1 = new ArrayList();
            ArrayList arrmes = new ArrayList();
            ArrayList arrmes1 = new ArrayList();
            JSONObject value = null;
            JSONArray features = null;
            try {
                arr = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //mMap.clear();
            for (GeoJsonFeature feature : layer.getFeatures()) {
                if (feature.hasGeometry()) {
                        GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                        poligono.setFillColor(Color.TRANSPARENT);
                        poligono.setStrokeColor(Color.BLACK);
                        feature.setPolygonStyle(poligono);
                 }
            }
            JSONObject finalobject = new JSONObject();
            try {
                finalobject.put("color", arr);

                for (int i=0; i < arr.length(); i++)
                {
                    try {
                        JSONObject oneObject = arr.getJSONObject(i);
                        // Pulling items from the array
                        if (oneObject.getString("enero_color").equals("6")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                arr.remove(i);
                            }
                        } else {

                            arr1.add(oneObject.getString("cel_id"));
                            arrmes1 = new ArrayList();
                            arrmes1.add(oneObject.getString("enero_color"));
                            arrmes1.add(oneObject.getString("febrero_color"));
                            arrmes1.add(oneObject.getString("marzo_color"));
                            arrmes1.add(oneObject.getString("abril_color"));
                            arrmes1.add(oneObject.getString("mayo_color"));
                            arrmes1.add(oneObject.getString("junio_color"));
                            arrmes1.add(oneObject.getString("julio_color"));
                            arrmes1.add(oneObject.getString("agosto_color"));
                            arrmes1.add(oneObject.getString("septiembre_color"));
                            arrmes1.add(oneObject.getString("octubre_color"));
                            arrmes1.add(oneObject.getString("noviembre_color"));
                            arrmes1.add(oneObject.getString("diciembre_color"));
                            arrmes.add(arrmes1);

                        }
                    } catch (JSONException e) {
                        // Oops
                    }
                }

                String meselc = mes_spinner.getSelectedItem().toString();
                //Log.e("Mes", "meselc " + meselc);
                features = jObj.getJSONArray("features");
                //Log.e("id", "id " + features.getString(1));
                for (int j=0; j<arr1.size();j++){
                    for (int k=0; k < features.length(); k++)
                    {

                            try {
                                value = features.getJSONObject(k);
                                //Log.e("gid", String.valueOf(value.getJSONObject("properties").get("gid").toString()));

                                //Log.e("gid", arr1.get(j).toString());
                                if (String.valueOf(value.getJSONObject("properties").get("gid")).equals(arr1.get(j).toString())) {
                                    ArrayList temporal = new ArrayList();
                                    int color = 0;
                                    //String meselc = mes_spinner.getSelectedItem().toString();
                                    Log.e("Mes", "color " + arrmes.get(j).toString());
                                    if(meselc.equals("Enero")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(0).toString()).intValue(); /// extrae el color del mes de enero
                                    }else if(meselc.equals("Febrero")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(1).toString()).intValue();/// extrae el color del mes de febrero
                                    }else if(meselc.equals("Marzo")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(2).toString()).intValue();/// extrae el color del mes de marzo
                                    }else if(meselc.equals("Abril")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(3).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Mayo")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(4).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Junio")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(5).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Julio")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(6).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Agosto")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(7).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Septiembre")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(8).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Octubre")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(9).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Noviembre")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(10).toString()).intValue();/// extrae el color del mes de enero
                                    }else if(meselc.equals("Diciembre")){
                                        temporal = (ArrayList)arrmes.get(j);
                                        color = new Double(temporal.get(11).toString()).intValue();/// extrae el color del mes de diciembre
                                    }
                                    int colorele= color;
                                    Log.e("color", "color escogido " + color);
                                    String color_alerta_celda ="";
                                    if(colorele==1){
                                        color_alerta_celda="23ff09";
                                        value.getJSONObject("properties").put("fillColor","#23ff09");
                                        value.getJSONObject("properties").put("opacity","1");
                                        value.getJSONObject("properties").put("stroke","#000000");
                                        //createPolygonFromJson(value.getJSONObject("geometry").get("coordinates").toString(), R.color.uno);
                                        // Get the coordinates
                                        for (GeoJsonFeature feature : layer.getFeatures()) {
                                            if (feature.hasGeometry()) {
                                                GeoJsonGeometry geometry = feature.getGeometry();

                                                if(feature.getId().toString().equals(value.get("id").toString())){

                                                    GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                                                    poligono.setFillColor(0x7F23FF09);
                                                    poligono.setStrokeColor(Color.BLACK);
                                                    feature.setPolygonStyle(poligono);
                                                    break;
                                                }
                                            }
                                        }
                                        Log.e("Objeto", "23ff09 " + value.get("id").toString());
                                    }else if(colorele==2){
                                        color_alerta_celda="ffff0c";
                                        value.getJSONObject("properties").put("fillColor","#ffff0c");
                                        value.getJSONObject("properties").put("opacity","1");
                                        value.getJSONObject("properties").put("stroke","#000000");
                                       // createPolygonFromJson(value.getJSONObject("geometry").get("coordinates").toString(), R.color.dos);
                                        for (GeoJsonFeature feature : layer.getFeatures()) {
                                            if (feature.hasGeometry()) {
                                                GeoJsonGeometry geometry = feature.getGeometry();

                                                if(feature.getId().toString().equals(value.get("id").toString())){

                                                    GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                                                    poligono.setFillColor(0x7FFFFF0C);
                                                    poligono.setStrokeColor(Color.BLACK);
                                                    feature.setPolygonStyle(poligono);
                                                    break;
                                                }
                                            }
                                        }
                                        Log.e("Objeto", "ffff0c " + value.get("id").toString());
                                    }else if(colorele==3){
                                        color_alerta_celda="fc8649";
                                        value.getJSONObject("properties").put("fillColor","#fc8649");
                                        value.getJSONObject("properties").put("opacity","1");
                                        value.getJSONObject("properties").put("stroke","#000000");
                                        //createPolygonFromJson(value.getJSONObject("geometry").get("coordinates").toString(), R.color.tres);
                                        for (GeoJsonFeature feature : layer.getFeatures()) {
                                            if (feature.hasGeometry()) {
                                                GeoJsonGeometry geometry = feature.getGeometry();

                                                if(feature.getId().toString().equals(value.get("id").toString())){

                                                    GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                                                    poligono.setFillColor(0x7FFC8649);
                                                    poligono.setStrokeColor(Color.BLACK);
                                                    feature.setPolygonStyle(poligono);
                                                    break;
                                                }
                                            }
                                        }
                                        Log.e("Objeto", "fc8649 " + value.get("id").toString());
                                    }else if(colorele==4){
                                        color_alerta_celda="fb0007";
                                        value.getJSONObject("properties").put("fillColor","#fb0007");
                                        value.getJSONObject("properties").put("opacity","1");
                                        value.getJSONObject("properties").put("stroke","#000000");
                                    //createPolygonFromJson(value.getJSONObject("geometry").get("coordinates").toString(), R.color.cuatro);
                                        for (GeoJsonFeature feature : layer.getFeatures()) {
                                            if (feature.hasGeometry()) {
                                                GeoJsonGeometry geometry = feature.getGeometry();

                                                if(feature.getId().toString().equals(value.get("id").toString())){

                                                    GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                                                    poligono.setFillColor(0x7FFB0007);
                                                    poligono.setStrokeColor(Color.BLACK);
                                                    feature.setPolygonStyle(poligono);
                                                    break;
                                                }
                                            }
                                        }
                                        Log.e("Objeto", "fb0007 " + value.get("id").toString());
                                    }else if(colorele==6){
                                        value.getJSONObject("properties").put("opacity","1");
                                        value.getJSONObject("properties").put("stroke","#000000");
                                        //createPolygonFromJson(value.getJSONObject("geometry").get("coordinates").toString(), 0xFF000000);
                                        for (GeoJsonFeature feature : layer.getFeatures()) {
                                            if (feature.hasGeometry()) {
                                                GeoJsonGeometry geometry = feature.getGeometry();

                                                if(feature.getId().toString().equals(value.get("id").toString())){

                                                    GeoJsonPolygonStyle poligono = new GeoJsonPolygonStyle();
                                                    poligono.setFillColor(Color.TRANSPARENT);
                                                    poligono.setStrokeColor(Color.BLACK);
                                                    feature.setPolygonStyle(poligono);
                                                    break;
                                                }
                                            }
                                        }
                                        Log.e("Objeto", "NO COLOR " + value.get("id").toString());
                                    }
                                }
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            Log.e("Json", "el objeto " +  jObj.toString());
            miProgreso.setVisibility(View.GONE);
            //showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}