package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.HttpInterceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {
        System.out.println("/////////////////////////////Logs Capture////////////////////////////////////");

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String protocol = request.getProtocol();
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String fileName = now.format(formatter) + "RequestLogs.txt";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write("method: "+ method+" uri: "+uri+" queryString: "+queryString+" protocol: "+protocol+" remoteAddr: "+remoteAddr+" "+" remotePort: "+remotePort+" userAgent: "+userAgent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }


}
