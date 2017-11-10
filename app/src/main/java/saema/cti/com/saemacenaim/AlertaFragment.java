package saema.cti.com.saemacenaim;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import saema.cti.com.saemacenaim.utils.JSONParser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlertaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Spinner cam_spinner;
    List<String> camaronera =  new ArrayList<String>();
    private OnFragmentInteractionListener mListener;

    public AlertaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertaFragment newInstance(String param1, String param2) {
        AlertaFragment fragment = new AlertaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mConsultarButton = (Button) getView().findViewById(R.id.Btn_consultar);
        cam_spinner = (Spinner) getView().findViewById(R.id.cam_spinner);
        final AutoCompleteTextView pisc_spinner = (AutoCompleteTextView) getView().findViewById(R.id.piscina);
        final Spinner anio_spinner = (Spinner) getView().findViewById(R.id.anio_spinner);
        final Spinner mes_spinner = (Spinner) getView().findViewById(R.id.mes_spinner);



        new JsonDataTask().execute();

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

        ArrayAdapter<String> adapterAnio = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, anio);
        ArrayAdapter<String> adapterMes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mes);

        adapterAnio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        anio_spinner.setAdapter(adapterAnio);
        mes_spinner.setAdapter(adapterMes);

        mConsultarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertaInfoFragment fragment = new AlertaInfoFragment();
                FragmentTransaction tran = getFragmentManager().beginTransaction();
                final Bundle bundle = new Bundle();
                bundle.putString("camaronera", cam_spinner.getSelectedItem().toString());
                bundle.putString("piscina", pisc_spinner.getText().toString());
                bundle.putString("mes", mes_spinner.getSelectedItem().toString());
                bundle.putString("anio", anio_spinner.getSelectedItem().toString());
                fragment.setArguments(bundle);
                tran.replace(R.id.fragment_container, fragment)
                        // Add this transaction to the back stack
                        .addToBackStack(null)
                        .commit();

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerta, container, false);
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

    private class JsonDataTask extends AsyncTask<String, Integer, String> {
        private ProgressBar miBarraDeProgreso;

        @Override
        protected void onPreExecute() {

            miBarraDeProgreso = (ProgressBar) getActivity().findViewById(R.id.progressBar_indicador);
            miBarraDeProgreso.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... urls) {
            String data ="";
            MainActivity main;
            main = (MainActivity) getActivity();
            data = JSONParser.getJSONFromUrl("http://200.10.150.98/usercam/?user="+main.userid);
            return data;
        }

        protected void onPostExecute(String result) {
            JSONArray arr = null;
            if (result != null){
                // try parse the string to a JSON object
                try {
                    arr = new JSONArray(result);
                    for (int i=0; i < arr.length(); i++)
                    {
                        JSONObject oneObject = arr.getJSONObject(i);
                        JSONObject fields = oneObject.getJSONObject("fields");
                        camaronera.add(fields.get("nom_cam").toString());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, camaronera);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cam_spinner.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

            }else{
                Toast.makeText(getActivity(),"Problema de conexion",Toast.LENGTH_LONG).show();
            }
            miBarraDeProgreso.setVisibility(View.GONE);
            //showDialog("Downloaded " + result + " bytes");
        }
    }
}
