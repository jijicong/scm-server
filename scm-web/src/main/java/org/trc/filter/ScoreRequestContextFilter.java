package org.trc.filter;

import org.trc.constants.SupplyConstants;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;

/**
 * Created by george on 2017/4/7.
 */
public class ScoreRequestContextFilter implements ContainerRequestFilter{

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    }

}
