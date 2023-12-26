//package co.ke.emtechhousee.emtr.Users.utils;
//
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ResourceUtils;
//
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class InitAuth {
////    @Value("${spring.application.basicActions}")
//    private String basicActions;
//
////    public List<Privilege> getAllPriviledges(){
////        String json = "{ \"privileges\":  [\n" +
////                "      { name: 'DASHBOARD', selected: true, code: 1 },\n" +
////                "      { name: 'CONFIGURATIONS', selected: true, code: 2 },\n" +
////                "      { name: 'CHARGE PARAMS', selected: true, code: 3 },\n" +
////                "      { name: 'INTEREST MAINTENANCE', selected: true, code: 4 },\n" +
////                "      { name: 'PRODUCTS', selected: true, code: 5 },\n" +
////                "      { name: 'MEMBERSHIP MANAGEMENT', selected: true, code: 6 },\n" +
////                "      { name: 'ACCOUNTS MANAGEMENT', selected: true, code: 7 },\n" +
////                "      { name: 'COLLATERALS MANAGEMENT', selected: true, code: 8 },\n" +
////                "      { name: 'TRANSACTION MAINTENANCE', selected: true, code: 9 },\n" +
////                "      { name: 'EOD MANAGEMENT', selected: true, code: 10 },\n" +
////                "      { name: 'REPORTS', selected: true, code: 11 },\n" +
////                "      { name: 'ACCESS MANAGEMENT', selected: true, code: 12 },\n" +
////                "      { name: 'ACCOUNTS STATEMENT', selected: false, code: 13 },\n" +
////                "    ]}";
////        List<Privilege> privilegeList = new ArrayList<>();
////        JSONObject jo = new JSONObject(json);
////        JSONArray ja = jo.getJSONArray("privileges");
////        for (Object obj : ja) {
////            JSONObject jo2 = new JSONObject(obj.toString());
////            Privilege privilege = new Gson().fromJson(jo2.toString(),Privilege.class);
////            privilegeList.add(privilege);
////        }
////        return privilegeList;
////    }
////    public List<Basicactions> getAllBasicActions() {
////        List<Basicactions> basicactionsList = new ArrayList<>();
////        try {
////            File file = ResourceUtils.getFile(basicActions);
////            InputStream in = new FileInputStream(file);
////            String strbasicactions = readFileToString(file.getAbsolutePath());
////            JSONObject jo = new JSONObject(strbasicactions);
////            JSONArray ja = jo.getJSONArray("basicActions");
////            for (Object obj : ja) {
////                JSONObject jo2 = new JSONObject(obj.toString());
////                Basicactions basicActions = new Gson().fromJson(jo2.toString(),Basicactions.class);
////                basicactionsList.add(basicActions);
////            }
////        } catch (FileNotFoundException e) {
////            throw new RuntimeException(e);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////
////        return basicactionsList;
////    }
//
//    public String readFileToString(String path)
//            throws IOException
//    {
//        byte[] encoded = Files.readAllBytes(Paths.get(path));
//        return new String(encoded, Charset.defaultCharset());
//    }
//
//
////    public List<Basicactions> getViewBasicActions() {
////        List<Basicactions> allActions = getAllBasicActions();
////        List<Basicactions> viewActions = new ArrayList<>();
////        for (Basicactions basicactions: allActions){
////            if (isViewAction(basicactions.getName())){
////                viewActions.add(basicactions);
////            }
////        }
////        return viewActions;
////    }
//
//    private boolean isViewAction(String name) {
//        String[] exceptions = new String[]{"ADD","MODIFY","DELETE","VERIFY","SATISFY","CLOSE","POST","ENTER","DETACH",
//                "CASH WITHDRAWAL","CASH DEPOSIT","TRANSFER","PROCESS CHEQUE"};
//        for (String e: exceptions) {
//            if (name.equalsIgnoreCase(e))
//                return false;
//        }
//        return true;
//    }
//}
