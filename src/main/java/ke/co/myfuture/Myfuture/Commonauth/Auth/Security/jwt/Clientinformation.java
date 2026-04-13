package ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.Session.Activesession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.UUID;

@Slf4j
@Component
public class Clientinformation {


    public void getClientInformation(HttpServletRequest request){
        String  browserDetails  =   request.getHeader("User-Agent");
        String  userAgent       =   browserDetails;
        String  user            =   userAgent.toLowerCase();
        String os = "";
        String browser = "";
        //=================OS=======================
        if (userAgent.toLowerCase().indexOf("windows") >= 0 )
        {
            os = "Windows";
        } else if(userAgent.toLowerCase().indexOf("mac") >= 0)
        {
            os = "Mac";
        } else if(userAgent.toLowerCase().indexOf("x11") >= 0)
        {
            os = "Unix";
        } else if(userAgent.toLowerCase().indexOf("android") >= 0)
        {
            os = "Android";
        } else if(userAgent.toLowerCase().indexOf("iphone") >= 0)
        {
            os = "IPhone";
        }else{
            os = "UnKnown, More-Info: "+userAgent;
        }
        //===============Browser===========================
        if (user.contains("msie"))
        {
            String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) )
        {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";

        } else if (user.contains("firefox"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv"))
        {
            browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else
        {
            browser = "UnKnown, More-Info: "+userAgent;
        }
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        Activesession activesession = new Activesession();
        activesession.setUuid(UUID.randomUUID());
        activesession.setUsername("Guest");
        activesession.setStatus("Active");
        activesession.setLoginAt(LocalDateTime.now().toString());
        activesession.setAddress(ipAddress);
        activesession.setOs(os);
        activesession.setBrowser(browser);
//        System.out.println(activesession);
        CurrentUserContext.setCurrentActiveUser(activesession);
        printRequestDetails(request);
    }

    public void printRequestDetails(HttpServletRequest request) {
        // Print HTTP method
//        System.out.println("HTTP Method: " + request.getMethod());

        // Print full URL
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append("?").append(queryString);
        }
        System.out.println("Full URL: " + requestURL.toString());

        // Print parameters
        Enumeration<String> parameterNames = request.getParameterNames();

        int i = 0;
        while (parameterNames.hasMoreElements()) {
            if (i++ == 0) {
                System.out.println("Parameters:");
            }
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            for (String value : paramValues) {
                System.out.println("  " + paramName + " = " + value);
            }
        }
    }
}
