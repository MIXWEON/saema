package saema.cti.com.saemacenaim;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import saema.cti.com.saemacenaim.utils.JSONParser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertaInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlertaInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertaInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "camaronera";
    private static final String ARG_PARAM2 = "piscina";
    private static final String ARG_PARAM3 = "mes";
    private static final String ARG_PARAM4 = "anio";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private TextView nombre, ipm, supervivencia, rendimiento, peso;
    private Button color;

    private OnFragmentInteractionListener mListener;

    public AlertaInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertaInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertaInfoFragment newInstance(String param1, String param2, String param3, String param4) {
        AlertaInfoFragment fragment = new AlertaInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            //Toast.makeText(getActivity(),mParam1+", "+mParam2+", "+mParam3+", "+mParam4,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = (Button) getView().findViewById(R.id.button);
        nombre = (TextView) getView().findViewById(R.id.textView18);
        ipm = (TextView) getView().findViewById(R.id.textView10);
        rendimiento = (TextView) getView().findViewById(R.id.textView12);
        supervivencia = (TextView) getView().findViewById(R.id.textView14);
        peso = (TextView) getView().findViewById(R.id.textView16);
        nombre.setText(mParam1);
        if (mParam2.length()>0){
            new PiscinaTask().execute();
            /*ipm.setText("0");
            rendimiento.setText("0");
            supervivencia.setText("0");
            peso.setText("0");*/
            new ValoresPiscinaTask().execute();
        }else{
            new CamaroneraTask().execute();
            /*ipm.setText("1.93");
            rendimiento.setText("16758.50");
            supervivencia.setText("63.67");
            peso.setText("26.4");*/
            new ValoresTask().execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerta_info, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    private class PiscinaTask extends AsyncTask<String, Integer, String> {
        private ProgressBar miProgreso;

        @Override
        protected void onPreExecute() {

            miProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miProgreso.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(String... urls) {
            String colors = "";
            colors = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/ColorPiscinas/?anio="+mParam4+"&nom_cam="+mParam1);
            return colors;
        }

        protected void onPostExecute(String result) {

            JSONArray arr = null;
            ArrayList arrmes = new ArrayList();
            ArrayList arrmes1 = new ArrayList();
            try {
                arr = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //mMap.clear();

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
                            arrmes1.add(oneObject.getString("pis_id"));
                            arrmes.add(arrmes1);
                        }
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                String meselc = mParam3.toString();
                int colorele = 0;
                for (int i =0;i<arrmes.size();i++) {
                    ArrayList temporal = (ArrayList) arrmes.get(i);
                    Log.e("info", "info " + temporal.toString());
                    if (temporal.get(12).toString().equals(mParam2)){
                        if(meselc.equals("Enero")){
                            colorele = new Double(temporal.get(0).toString()).intValue(); /// extrae el color del mes de enero
                        }else if(meselc.equals("Febrero")){
                            colorele = new Double(temporal.get(1).toString()).intValue();/// extrae el color del mes de febrero
                        }else if(meselc.equals("Marzo")){
                            colorele = new Double(temporal.get(2).toString()).intValue();/// extrae el color del mes de marzo
                        }else if(meselc.equals("Abril")){
                            colorele = new Double(temporal.get(3).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Mayo")){
                            colorele = new Double(temporal.get(4).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Junio")){
                            colorele = new Double(temporal.get(5).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Julio")){
                            colorele = new Double(temporal.get(6).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Agosto")){
                            colorele = new Double(temporal.get(7).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Septiembre")){
                            colorele = new Double(temporal.get(8).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Octubre")){
                            colorele = new Double(temporal.get(9).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Noviembre")){
                            colorele = new Double(temporal.get(10).toString()).intValue();/// extrae el color del mes de enero
                        }else if(meselc.equals("Diciembre")){
                            colorele = new Double(temporal.get(11).toString()).intValue();/// extrae el color del mes de diciembre
                        }
                        break;
                    }
                }


                if(colorele==1){
                    color.setBackgroundResource(R.color.uno);
                    color.invalidate();

                }else if(colorele==2){
                    color.setBackgroundResource(R.color.dos);
                    color.invalidate();

                }else if(colorele==3){
                    color.setBackgroundResource(R.color.tres);
                    color.invalidate();

                }else if(colorele==4){
                    color.setBackgroundResource(R.color.cuatro);
                    color.invalidate();

                }else if(colorele==6){
                    color.setBackgroundResource(R.color.colorPrimary);
                    color.invalidate();

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            miProgreso.setVisibility(View.GONE);
        }
    }

    private class CamaroneraTask extends AsyncTask<String, Integer, String> {
        private ProgressBar miProgreso;

        @Override
        protected void onPreExecute() {

            miProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miProgreso.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(String... urls) {
            String colors = "";
            colors = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/ColorCamaroneras/?anio="+mParam4+"&nom_cam="+mParam1);
            return colors;
        }

        protected void onPostExecute(String result) {

            JSONArray arr = null;
            ArrayList arrmes = new ArrayList();
            try {
                arr = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //mMap.clear();

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

                            arrmes.add(oneObject.getString("enero_color"));
                            arrmes.add(oneObject.getString("febrero_color"));
                            arrmes.add(oneObject.getString("marzo_color"));
                            arrmes.add(oneObject.getString("abril_color"));
                            arrmes.add(oneObject.getString("mayo_color"));
                            arrmes.add(oneObject.getString("junio_color"));
                            arrmes.add(oneObject.getString("julio_color"));
                            arrmes.add(oneObject.getString("agosto_color"));
                            arrmes.add(oneObject.getString("septiembre_color"));
                            arrmes.add(oneObject.getString("octubre_color"));
                            arrmes.add(oneObject.getString("noviembre_color"));
                            arrmes.add(oneObject.getString("diciembre_color"));

                        }
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                String meselc = mParam3.toString();
                int colorele = 0;

                if(meselc.equals("Enero")){
                    colorele = new Double(arrmes.get(0).toString()).intValue(); /// extrae el color del mes de enero
                }else if(meselc.equals("Febrero")){
                    colorele = new Double(arrmes.get(1).toString()).intValue();/// extrae el color del mes de febrero
                }else if(meselc.equals("Marzo")){
                    colorele = new Double(arrmes.get(2).toString()).intValue();/// extrae el color del mes de marzo
                }else if(meselc.equals("Abril")){
                    colorele = new Double(arrmes.get(3).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Mayo")){
                    colorele = new Double(arrmes.get(4).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Junio")){
                    colorele = new Double(arrmes.get(5).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Julio")){
                    colorele = new Double(arrmes.get(6).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Agosto")){
                    colorele = new Double(arrmes.get(7).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Septiembre")){
                    colorele = new Double(arrmes.get(8).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Octubre")){
                    colorele = new Double(arrmes.get(9).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Noviembre")){
                    colorele = new Double(arrmes.get(10).toString()).intValue();/// extrae el color del mes de enero
                }else if(meselc.equals("Diciembre")){
                    colorele = new Double(arrmes.get(11).toString()).intValue();/// extrae el color del mes de diciembre
                }

                if(colorele==1){
                    color.setBackgroundResource(R.color.uno);
                    color.invalidate();

                }else if(colorele==2){
                    color.setBackgroundResource(R.color.dos);
                    color.invalidate();

                }else if(colorele==3){
                    color.setBackgroundResource(R.color.tres);
                    color.invalidate();

                }else if(colorele==4){
                    color.setBackgroundResource(R.color.cuatro);
                    color.invalidate();

                }else if(colorele==6){
                    color.setBackgroundResource(R.color.colorPrimary);
                    color.invalidate();

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            miProgreso.setVisibility(View.GONE);
        }
    }

    private class ValoresTask extends AsyncTask<String, Integer, ArrayList> {
        private ProgressBar miProgreso;

        @Override
        protected void onPreExecute() {

            miProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miProgreso.setVisibility(View.VISIBLE);
        }


        protected ArrayList doInBackground(String... urls) {
            int mes = mesSelected(mParam3.toString());
            String ipm = "";
            ArrayList arr1 = new ArrayList();
            String meselc = mParam3.toString();
            String supervivencia = "";
            String pesopromedio = "";
            String rendimiento = "";

            ipm = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/IpmCamaroneras/?anio="+mParam4+"&nom_cam="+mParam1);
            supervivencia = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/SupervCelcampis/?anio="+mParam4+"&nom_cam="+mParam1);
            pesopromedio = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/PesopCelcampis/?anio="+mParam4+"&nom_cam="+mParam1);
            rendimiento = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/ProduCelcampis/?anio="+mParam4+"&nom_cam="+mParam1);
            arr1.add(ipm);
            arr1.add(promedia_parametro_supervivencia(supervivencia));
            //arr1.add(supervivencia);
            arr1.add(promedia_parametro_pesopromedio(pesopromedio));
            //arr1.add(pesopromedio);
            arr1.add(promedia_parametro_produccion(rendimiento));
            //arr1.add(rendimiento);

            return arr1;
        }

        protected void onPostExecute(ArrayList result) {
            //mMap.clear();
            int meselc = mesSelected(mParam3);
            JSONArray arr = new JSONArray(result);
            DecimalFormat df = new DecimalFormat("0.00");
            Log.e("ipm", "ipm " + result.get(0));
            try {

                String ipmarr = (String) arr.get(0);
                ipmarr = ipmarr.replace("[","");
                ipmarr = ipmarr.replace("]","");
                JSONObject object = new JSONObject(ipmarr);

                JSONArray supervivenciaarr = arr.getJSONArray(1);
                JSONArray pesopromedioarr = arr.getJSONArray(2);
                JSONArray rendimientoarr = arr.getJSONArray(3);

                ipm.setText(df.format(object.getDouble(mParam3.toLowerCase()+"_ipm")));//String.valueOf(ipmarr.get(meselc+3)));
                supervivencia.setText(String.valueOf(supervivenciaarr.get(meselc)));
                rendimiento.setText(String.valueOf(rendimientoarr.get(meselc)));
                peso.setText(String.valueOf(pesopromedioarr.get(meselc)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("supervivencia", "supervivencia " + result.get(1));
            Log.e("pesopromedio", "pesopromedio " + result.get(2));
            Log.e("rendimiento", "rendimiento " + result.get(3));
            miProgreso.setVisibility(View.GONE);
        }
    }

    private class ValoresPiscinaTask extends AsyncTask<String, Integer, ArrayList> {
        private ProgressBar miProgreso;

        @Override
        protected void onPreExecute() {

            miProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miProgreso.setVisibility(View.VISIBLE);
        }


        protected ArrayList doInBackground(String... urls) {
            int mes = mesSelected(mParam3.toString());
            String ipm = "";
            ArrayList arr1 = new ArrayList();
            String meselc = mParam3.toString();

            ipm = JSONParser.getJSONFromUrl("http://200.10.150.98/rest/Excel4/?anio="+mParam4+"&nom_cam="+mParam1+"&pis_id="+mParam2);
            arr1.add(ipm);

            //arr1.add(rendimiento);

            return arr1;
        }

        protected void onPostExecute(ArrayList result) {
            //mMap.clear();
            int meselc = mesSelected(mParam3);
            try {
                JSONArray arr = new JSONArray(result.get(0).toString());

                DecimalFormat df = new DecimalFormat("0.00");
                Log.e("ipm", "ipm " + result.get(0));

                for(int i=0; i<arr.length();i++) {
                    JSONObject object = arr.getJSONObject(i);
                    if(Integer.valueOf(object.get("fec_cosec").toString().substring(5,7))>=meselc){
                        ipm.setText(df.format(object.getDouble("ipm_ref")));//String.valueOf(ipmarr.get(meselc+3)));
                        supervivencia.setText(df.format(object.getDouble("superv_ref")));
                        rendimiento.setText(df.format(object.getDouble("produ_ref")));
                        peso.setText(df.format(object.getDouble("pesoprom_ref")));
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            miProgreso.setVisibility(View.GONE);
        }
    }

    public int mesSelected(String mes){
        if(mes.equals("Enero")){
            return 0;
        }else if(mes.equals("Febrero")){
            return 1;
        }else if(mes.equals("Marzo")){
            return 2;
        }else if(mes.equals("Abril")){
            return 3;
        }else if(mes.equals("Mayo")){
            return 4;
        }else if(mes.equals("Junio")){
            return 5;
        }else if(mes.equals("Julio")){
            return 6;
        }else if(mes.equals("Agosto")){
            return 7;
        }else if(mes.equals("Septiembre")){
            return 8;
        }else if(mes.equals("Octubre")){
            return 9;
        }else if(mes.equals("Noviembre")){
            return 10;
        }else if(mes.equals("Diciembre")){
            return 11;
        }
        return 0;
    }

    public double validanullipm(Double ipm){
        if(ipm==null  || ipm.equals("null")){
            return 0;
        }else{
            return ipm;
        }
    }

    public ArrayList promedia_parametro_supervivencia(String result){
        JSONArray data = null;
        ArrayList mesespromediados = new ArrayList();
        ArrayList meses = new ArrayList();
        for (int j =0;j<12;j++){
            meses.add(0.00);
        }
        ArrayList meses_length = new ArrayList();
        for (int j =0;j<12;j++){
            meses_length.add(0.00);
        }
        ArrayList resultado = new ArrayList();
        DecimalFormat df = new DecimalFormat("0.00");

        try {
            data = new JSONArray(result);
            for(int i=0; i<data.length();i++){
                JSONObject object = data.getJSONObject(i);
                ArrayList prop1= new ArrayList();
                if (!object.isNull("enero_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("enero_superv"))));
                }else {
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("febrero_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("febrero_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("marzo_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("marzo_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("abril_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("abril_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("mayo_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("mayo_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("junio_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("junio_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("julio_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("julio_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("agosto_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("agosto_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("septiembre_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("septiembre_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("octubre_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("octubre_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("noviembre_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("noviembre_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("diciembre_superv")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("diciembre_superv"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }

                    mesespromediados = prop1;
                    for(int j=0; j<mesespromediados.size();j++){
                        meses.set(j,(double)meses.get(j) + (double)mesespromediados.get(j));
                        if((double)mesespromediados.get(j)!=0){
                            meses_length.set(j,(double)meses_length.get(j)+1);
                        }
                    }

            }
            ArrayList res = new ArrayList();
            for(int j=0; j<meses.size();j++){
                double val= (double)meses.get(j)/(double)meses_length.get(j);
                res.add(j,val);
            }

            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(0).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(1).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(2).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(3).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(4).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(5).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(6).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(7).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(8).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(9).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(10).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(11).toString())))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public ArrayList promedia_parametro_pesopromedio(String result){
        ArrayList mesespromediados = new ArrayList();
        DecimalFormat df = new DecimalFormat("0.00");
        ArrayList meses = new ArrayList();
        for (int j =0;j<12;j++){
            meses.add(0.00);
        }
        ArrayList resultado = new ArrayList();
        JSONArray data = null;
        try {
            data = new JSONArray(result);
            for(int i=0; i<data.length();i++){
               // if(data[i].nom_cam=='{{ nom_cam }}'){
                JSONObject object = data.getJSONObject(i);
                ArrayList prop1= new ArrayList();
                if (!object.isNull("enero_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("enero_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("febrero_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("febrero_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("marzo_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("marzo_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("abril_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("abril_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("mayo_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("mayo_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("junio_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("junio_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("julio_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("julio_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("agosto_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("agosto_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("septiembre_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("septiembre_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("octubre_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("octubre_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("noviembre_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("noviembre_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("diciembre_pesop")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("diciembre_pesop"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }

                    mesespromediados = prop1;
                    for(int j=0; j<mesespromediados.size();j++){
                        meses.set(j,(double)meses.get(j) + (double)mesespromediados.get(j));
                    }
                //}
            }
            ArrayList res = new ArrayList();
            for(int j=0; j<meses.size();j++){
                double val= (double)meses.get(j);
                res.add(j,val);
            }
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(0).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(1).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(2).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(3).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(4).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(5).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(6).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(7).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(8).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(9).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(10).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(11).toString())))));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public ArrayList promedia_parametro_produccion(String result){
        JSONArray data = null;
        ArrayList mesespromediados = new ArrayList();
        ArrayList meses = new ArrayList();
        for (int j =0;j<12;j++){
            meses.add(0.00);
        }
        ArrayList meses_length = new ArrayList();
        for (int j =0;j<12;j++){
            meses_length.add(0.00);
        }
        ArrayList resultado = new ArrayList();
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            data = new JSONArray(result);
            for(int i=0; i<data.length();i++){
                JSONObject object = data.getJSONObject(i);
                ArrayList prop1= new ArrayList();
                if (!object.isNull("enero_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("enero_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("febrero_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("febrero_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("marzo_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("marzo_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("abril_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("abril_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("mayo_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("mayo_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("junio_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("junio_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("julio_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("julio_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("agosto_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("agosto_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("septiembre_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("septiembre_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("octubre_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("octubre_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("noviembre_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("noviembre_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }
                if (!object.isNull("diciembre_produ")) {
                    prop1.add(Double.valueOf(df.format(object.getDouble("diciembre_produ"))));
                }else{
                    prop1.add(Double.valueOf(df.format(0)));
                }

                mesespromediados = prop1;
                for(int j=0; j<mesespromediados.size();j++){
                    meses.set(j,(double)meses.get(j) + (double)mesespromediados.get(j));
                }

            }
            ArrayList res = new ArrayList();
            for(int j=0; j<meses.size();j++){
                double val= (double)meses.get(j);
                res.add(j,val);
            }
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(0).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(1).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(2).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(3).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(4).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(5).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(6).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(7).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(8).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(9).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(10).toString())))));
            resultado.add(Double.valueOf(df.format(validanullipm(Double.valueOf(res.get(11).toString())))));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultado;
    }

}
