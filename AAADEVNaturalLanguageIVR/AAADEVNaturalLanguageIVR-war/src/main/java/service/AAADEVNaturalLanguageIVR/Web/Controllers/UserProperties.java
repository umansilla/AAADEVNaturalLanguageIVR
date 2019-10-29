/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

import com.avaya.collaboration.util.logger.Logger;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "UserProperties", urlPatterns = {"/UserProperties"})
public class UserProperties extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        String actionString = new PartToString().getStringValue(request.getPart("action"));
        response.setContentType("application/json");

        HttpSession userSession = (HttpSession) request.getSession();
        Usuario usuario = (Usuario) userSession.getAttribute("userActive");
        
        if (usuario == null) {
        	logger.error("Do Post /UserProperties NO User Registered.");
            json.put("status", "false");

        } else {
            if (actionString.equals("userProp")) {
                json.put("user", usuario.getUsername());
                json.put("real_name", usuario.getName());
                json.put("verbio_user", usuario.getVerbiouser());
                json.put("date", usuario.getFecha());
                json.put("hour", usuario.getHora());
                json.put("phone_active", usuario.getPhone());
                json.put("verbio_train", usuario.getTrain());
                json.put("country", usuario.getCountry());

                //MODIFICACIoN 10 DE JULIO
                if (!usuario.getCuenta().equals("")) {
                    json.put("saldoCajaSocial", usuario.getSaldo());
                    json.put("cuentaCajaSocial", usuario.getCuenta());
                    JSONArray jsonArrayHistoricos = new JSONArray();
                    for (int i = 0; i < usuario.getHistoricoList().size(); i++) {
                        jsonArrayHistoricos.put(usuario.getHistoricoList().get(i));
                    }
                    json.put("historicoMovimientos", jsonArrayHistoricos);
                }
                /////////////////////////////
                if (usuario.getUsername().equals("umansilla@avaya.com") || usuario.getUsername().equals("jlramirez@breeze.com")) {
                    json.put("admin", "admin");
                }
            }
            if (actionString.equals("closeSession")) {
            	userSession.invalidate();
                json.put("status", "ok");

            }

            if (actionString.equals("createVerbio")) {
                String userVerbioString =  new PartToString().getStringValue(request.getPart("userVerbio"));
                String phoneActiveString = new PartToString().getStringValue(request.getPart("phoneActive"));
                try {
                    createVerbio(userVerbioString, phoneActiveString, usuario);
                    usuario.setVerbiouser(userVerbioString);
                    usuario.setPhone(phoneActiveString);
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "updated");
                } catch (Exception e) {
                    logger.error("doPsot /UserProperties Create Verbio Error " + e.toString());
                    json.put("error", "error");
                }

            }

            if (actionString.equals("saveSettings")) {
                String nameString = new PartToString().getStringValue(request.getPart("name"));
                String phoneString = new PartToString().getStringValue(request.getPart("phone"));
                String countryString = new PartToString().getStringValue(request.getPart("country"));
                String passwordString = new PartToString().getStringValue(request.getPart("password"));
                String deciptString = decryptText(passwordString, "secret");

                try {
                    updateSettings(nameString, phoneString, countryString, deciptString, usuario);
                    if (nameString.equals("") == false) {
                    	usuario.setName(nameString);
                    }
                    if (phoneString.equals("") == false) {
                    	usuario.setPhone(phoneString);
                    }
                    if (countryString.equals("") == false) {
                    	usuario.setCountry(countryString);
                    }
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "updated");
                } catch (IOException | JSONException e) {
                    logger.error("doPost /UserProperties Save Settings Error " + e.toString());
                    json.put("error", "error");
                }

            }
            //CREADO EL 10 DE JULIO 2019
            if (actionString.equals("createCajaNacional")) {
                try {
                    JSONObject jsonReturn = new JSONObject();
                    jsonReturn = createCajaNacional(usuario);
                    usuario.setCuenta(jsonReturn.getString("Cuenta_Caja_Social"));
                    usuario.setSaldo(jsonReturn.getString("Saldo_Caja_Social"));
                    JSONArray jsonArray = jsonReturn.getJSONArray("Historico_Caja_Social");
                    ArrayList<String> historicoList = new ArrayList<>();
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        historicoList.add(jsonArray.getString(i));
                    }
                    usuario.setHistoricoList(historicoList);
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("doPost /UserProperties Create Caja Social Error: " + e.toString());
                    json.put("error", "error");
                }
            }
            if (actionString.equals("refreshAccount")) {
                try {
                    String newAccount = refreshAccount(usuario);
                    usuario.setCuenta(newAccount);
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("doPost /UserProperties Refresh Account Error " + e.toString());
                    json.put("error", "error");
                }

            }
            if (actionString.equals("newBalance")) {
                try {
                    newBalance(usuario, new PartToString().getStringValue(request.getPart("balance")));
                    usuario.setSaldo(new PartToString().getStringValue(request.getPart("balance")));
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("doPsot /UserProperties New Balance Error " + e.toString());
                    json.put("error", "error");
                }
            }
            if (actionString.equals("addMovement")) {
                try {
                    String movement = addNewMovement(usuario, new PartToString().getStringValue(request.getPart("movement")));
                    usuario.getHistoricoList().add(movement);
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("doPost /UserProperties Add Movement Error " + e.toString());
                    json.put("error", "error");
                }

            }
            if (actionString.equals("deleteMovement")) {
                try {
                    String movementString = new PartToString().getStringValue(request.getPart("movement"));
                    JSONArray jsonNewMovements = removeMovement(usuario, movementString);
                    usuario.getHistoricoList().clear();
                    for (int i = 0; i <= jsonNewMovements.length() - 1; i++) {
                    	usuario.getHistoricoList().add(jsonNewMovements.getString(i));
                    }
                    userSession.setAttribute("userActive", usuario);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("doPost /UserProperties Delete Movement Error: " + e.toString());
                    json.put("error", "error");
                }
            }

            /////////////////////////////
        }
        out.println(json);
    }

    public JSONArray removeMovement(Usuario user, String movementString) throws IOException {
        JSONArray nuevoJsonArray = new JSONArray();
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                JSONArray jsonArrayHistorico = jsonAccount.getJSONArray("Historico_Caja_Social");

                for (int j = 0; j <= jsonArrayHistorico.length() - 1; j++) {
                    if (!movementString.equals(jsonArrayHistorico.get(j))) {
                        nuevoJsonArray.put(jsonArrayHistorico.get(j));
                    }
                }

                jobj.getJSONObject(i).getJSONObject("Caja_Social").put("Historico_Caja_Social", nuevoJsonArray);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return nuevoJsonArray;
    }

    public String addNewMovement(Usuario user, String movementString) throws IOException {
        String historico = null;
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                JSONArray jsonArrayHistorico = jsonAccount.getJSONArray("Historico_Caja_Social");
                String strDateFormat = "hh:mm:ss a dd MMMM yyyy";
                SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); 
                Date historicoOne = new Date();
                historico = objSDF.format(historicoOne) + " " + movementString;
                jsonArrayHistorico.put(historico);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return historico;
    }

    public void newBalance(Usuario user, String balanceString) throws IOException {
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                jsonAccount.put("Saldo_Caja_Social", balanceString);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();

    }

    public String refreshAccount(Usuario user) throws IOException {
        String newAccount = generateRandomNumber(6);
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                jsonAccount.put("Cuenta_Caja_Social", newAccount);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return newAccount;
    }

    //CREADO EL 10 DE JULIO 2019
    public JSONObject createCajaNacional(Usuario user) throws IOException, Exception {
        JSONObject jsonProperties = new JSONObject();
        JSONArray jsonArrayHistoricos = new JSONArray();
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                if (jobj.getJSONObject(i).has("Caja_Social")) {
                    throw new Exception("El usuario ya cuenta con session");
                } else {
                    jsonProperties.put("Cuenta_Caja_Social", generateRandomNumber(6));
                    jsonProperties.put("Saldo_Caja_Social", "0.00");
                    String strDateFormat = "hh:mm:ss a dd MMMM yyyy";
                    SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
                    Date historicoOne = new Date();
                    String primerHistorico = objSDF.format(historicoOne) + " Trial session has been created, Historical first";
                    jsonArrayHistoricos.put(primerHistorico);
                    Date historicoTwo = new Date();
                    String SegundoHistorico = objSDF.format(historicoTwo) + " Trial session has been created, Historical Second";
                    jsonArrayHistoricos.put(SegundoHistorico);
                    jsonProperties.put("Historico_Caja_Social", jsonArrayHistoricos);
                    jobj.getJSONObject(i).put("Caja_Social", jsonProperties);
                }
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return jsonProperties;
    }

    public String generateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public void updateSettings(String nameString, String phoneString, String countryString, String deciptString, Usuario user) throws IOException {
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {

                if (nameString.equals("") == false) {
                    jobj.getJSONObject(i).put("name", nameString);
                }
                if (phoneString.equals("") == false) {
                    jobj.getJSONObject(i).put("phone", phoneString);
                }
                if (countryString.equals("") == false) {
                    jobj.getJSONObject(i).put("country", countryString);
                }
                if (deciptString.equals("") == false) {
                    jobj.getJSONObject(i).put("password", deciptString);
                }
                break;
            } else {
                continue;
            }

        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
    }

    public static String decryptText(String cipherText, String secret) {

        String decryptedText = null;
        byte[] cipherData = java.util.Base64.getDecoder().decode(cipherText);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            return decryptedText;
        } catch (Exception ex) {
            return decryptedText;
        }
    }

    public void createVerbio(String userVerbioString, String phoneActiveString, Usuario user) throws IOException {

        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                jobj.getJSONObject(i).put("verbiouser", userVerbioString);
                jobj.getJSONObject(i).put("phone", phoneActiveString);

                break;
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();

    }
    
    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0) {
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                }
                md.update(password);
                if (salt != null) {
                    md.update(salt, 0, 8);
                }
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0) {
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            }

            return result;

        } catch (DigestException e) {

            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte) 0);
        }
    }

}
