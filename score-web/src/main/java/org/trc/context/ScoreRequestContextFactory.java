package org.trc.context;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by george on 2017/4/7.
 */
public class ScoreRequestContextFactory {

    private static AppDto appDto;

    private static ThreadLocal<ScoreRequestContext> jerseyScoreRequestContextThreadLocal;

    public static void init(AppDto appDto){
        ScoreRequestContextFactory.appDto = appDto;
    }

    public static ScoreRequestContext instance(){
        ScoreRequestContext scoreRequestContext = new ScoreRequestContext();
        if (jerseyScoreRequestContextThreadLocal == null) {
            synchronized (ScoreRequestContextFactory.class) {
                if (jerseyScoreRequestContextThreadLocal == null) {
                    jerseyScoreRequestContextThreadLocal = new ThreadLocal<>();
                }
            }
        }
        jerseyScoreRequestContextThreadLocal.set(scoreRequestContext);
        return scoreRequestContext;
    }

    public static ScoreRequestContext getInstance() throws Exception {
        ScoreRequestContext instance = jerseyScoreRequestContextThreadLocal.get();
        return instance;
    }

    public static AppDto getAppDto() {
        return appDto;
    }

    public static void setAppDto(AppDto appDto) {
        ScoreRequestContextFactory.appDto = appDto;
    }

}
