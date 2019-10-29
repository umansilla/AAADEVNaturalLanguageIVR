package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;

/**
 *
 * @author umansilla
 */
public class GetAllUsers {

    public List<Usuario> getAllUsers() {
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        List<Usuario> listUsuarios = new ArrayList<>();
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).has("username") ? jobj.getJSONObject(i).getString("username") : "";
            String name = jobj.getJSONObject(i).has("name") ? jobj.getJSONObject(i).getString("name") : "";
            String verbiouser = jobj.getJSONObject(i).has("verbiouser") ? jobj.getJSONObject(i).getString("verbiouser") : "";
            String fecha = jobj.getJSONObject(i).has("fecha") ? jobj.getJSONObject(i).getString("fecha") : "";
            String hora = jobj.getJSONObject(i).has("hora") ? jobj.getJSONObject(i).getString("hora") : "";
            String phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "";
            String train = jobj.getJSONObject(i).has("train") ? jobj.getJSONObject(i).getString("train") : "";
            String country = jobj.getJSONObject(i).has("country") ? jobj.getJSONObject(i).getString("country") : "";
            Boolean cajaSocialExists = jobj.getJSONObject(i).has("Caja_Social");
            String cuenta = "";
            String saldo = "";
            ArrayList<String> historicoList = null;
            if (cajaSocialExists) {
                JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                cuenta = cajaSocial.getString("Cuenta_Caja_Social");
                saldo = cajaSocial.getString("Saldo_Caja_Social");
                JSONArray cajaSocialArray = cajaSocial.getJSONArray("Historico_Caja_Social");
                historicoList = new ArrayList<>();
                for (int j = 0; j <= cajaSocialArray.length() - 1; j++) {
                    historicoList.add(cajaSocialArray.getString(j));
                }
            }
            listUsuarios.add(new Usuario(jobj.getJSONObject(i).getInt("id"), name, verbiouser, userName, fecha, hora, phone, train, country, "es", cuenta, saldo, historicoList));
        }
        return listUsuarios;
    }
}
