package br.com.senac.appcar;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.senac.appcar.modelo.AppCar;
import br.com.senac.appcar.webservice.Api;
import br.com.senac.appcar.webservice.RequestHandler;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextIdCarro;
    EditText editTextCarro;
    EditText editTextPlaca;
    EditText editTextServico;
    Button buttonSalvar;
    ProgressBar progressBar;
    ListView listView;
    List<AppCar> appCarList;

    Boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.barraProgresso);
        listView = findViewById(R.id.listViewTarefas);
        editTextIdCarro = findViewById(R.id.editTextIdCarro);
        editTextCarro = findViewById(R.id.editTextCarro);
        editTextPlaca = findViewById(R.id.editTextPlaca);
        editTextServico = findViewById(R.id.editTextServico);

        buttonSalvar = findViewById(R.id.buttonSalvar);

        appCarList = new ArrayList<>();

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUpdating) {
                    updateAppCar();
                } else {
                    createAppCar();
                }
            }
        });
        readAppCar();
    }

    private void createAppCar(){
        String carro = editTextCarro.getText().toString().trim();
        String placa = editTextPlaca.getText().toString().trim();
        String servico = editTextServico.getText().toString().trim();

        if (TextUtils.isEmpty(carro)) {
            editTextCarro.setError("Digite o modelo do carro");
            editTextCarro.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(placa)) {
            editTextCarro.setError("Digite a placa do carro");
            editTextCarro.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(servico)) {
            editTextCarro.setError("Digite o serviço executado");
            editTextCarro.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("carro", carro);
        params.put("placa", placa);
        params.put("servico", servico);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_APPCAR,params, CODE_POST_REQUEST);
        request.execute();

    }

    private void updateAppCar(){
        String id = editTextIdCarro.getText().toString();
        String carro = editTextCarro.getText().toString().trim();
        String placa = editTextPlaca.getText().toString().trim();
        String servico = editTextServico.getText().toString().trim();

        if (TextUtils.isEmpty(carro)) {
            editTextCarro.setError("Digite o modelo do carro");
            editTextCarro.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(placa)) {
            editTextCarro.setError("Digite a placa do carro");
            editTextCarro.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(servico)) {
            editTextCarro.setError("Digite o serviço executado");
            editTextCarro.requestFocus();
            return;
        }

        HashMap<String,String> params = new HashMap<>();
        params.put("id", id);
        params.put("carro", carro);
        params.put("placa", placa);
        params.put("servico", servico);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_APPCAR,params,CODE_POST_REQUEST);
        request.execute();

        buttonSalvar.setText("Salvar");
        editTextCarro.setText("");
        editTextPlaca.setText("");
        editTextServico.setText("");

        isUpdating = false;

    }
    private void readAppCar() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_APPCAR,null,CODE_GET_REQUEST);
        request.execute();
    }

    private void deleteAppCar(int id){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_APPCAR + id,null,CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshAppCarList(JSONArray appcar) throws JSONException{
        appCarList.clear();

        for (int i = 0; i < appcar.length(); i++){
            JSONObject obj = appcar.getJSONObject(i);

            appCarList.add(new AppCar(
                    obj.getInt("id"),
                    obj.getString("carro"),
                    obj.getString("placa"),
                    obj.getString("servico")

            ));
        }

        AppCarAdapter adapter = new AppCarAdapter(appCarList);
        listView.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(),object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshAppCarList(object.getJSONArray("appcar"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
    class AppCarAdapter extends ArrayAdapter<AppCar>{
        List <AppCar> appCarList;
        public AppCarAdapter(List<AppCar> appCarList){
            super(MainActivity.this,R.layout.layout_appcar_list, appCarList);

            this.appCarList = appCarList;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_appcar_list, null,true);

            TextView textViewServico = listViewItem.findViewById(R.id.textViewServico);

            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);
            TextView textViewAlterar = listViewItem.findViewById(R.id.buttonSalvar);


            final AppCar appCar = appCarList.get(position);

            textViewServico.setText(appCar.getCarro());
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Delete " + appCar.getCarro()).setMessage("Você deseja realmente deletar?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteAppCar(appCar.getId());

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            textViewAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isUpdating = true;
                    editTextIdCarro.setText(String.valueOf(appCar.getId()));
                    editTextCarro.setText(appCar.getCarro());
                    editTextPlaca.setText(appCar.getPlaca());
                    editTextServico.setText(appCar.getServico());
                    buttonSalvar.setText("Alterar");

                }
            });
            return listViewItem;
        }
    }
}



