package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Session;



//@Configuration
//@EnableSpringHttpSession
public class SessionConfig {

//    @Bean
//    public HttpSessionIdResolver httpSessionIdResolver() {
//        return new HttpSessionIdResolver() {
//            public List<String> resolveSessionIds(HttpServletRequest request) {
//                final var sessionId = request.getHeader("X-SessionId");
//                System.out.println("Session id is: "+sessionId);
//                request.setAttribute(SessionConfig.class.getName() + "SessionIdAttr", sessionId);
//                return List.of(sessionId);
//            }
//
//            public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
//            }
//
//            public void expireSession(HttpServletRequest request, HttpServletResponse response) {
//            }
//        };
//    }
//
//    @Bean
//    public SessionRepository<MapSession> sessionRepository() {
//        return new MapSessionRepository(new HashMap<>()) {
//            @Override
//            public MapSession createSession() {
//                var sessionId =
//                        (String) RequestContextHolder
//                                .currentRequestAttributes()
//                                .getAttribute(SessionConfig.class.getName() + "SessionIdAttr", 0);
//                final var session = super.createSession();
//                if (sessionId != null) {
//                    session.setId(sessionId);
//                }
//                return session;
//            }
//        };
//    }
}