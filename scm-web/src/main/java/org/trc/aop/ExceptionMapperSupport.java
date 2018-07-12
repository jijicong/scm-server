package org.trc.aop;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.enums.ResultEnum;
import org.trc.util.AppResult;
@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Exception> {
	
	private Logger logger = LoggerFactory.getLogger(ExceptionMapperSupport.class);
	
	@Override
	public Response toResponse(Exception ex) {
		logger.error("exceptionMapper get exception:", ex);
        AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), ex.getMessage(), "");
        return Response.status(Response.Status.BAD_REQUEST).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build();
	}

}
