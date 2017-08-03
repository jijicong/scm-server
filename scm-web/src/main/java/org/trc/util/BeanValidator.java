package org.trc.util;

import org.springframework.stereotype.Component;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hzwdx on 2017/4/24.
 */
@Component
public class BeanValidator {

    private Validator validator;

    public BeanValidator(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 验证某个bean的参数
     *
     * @param object 被校验的参数
     * @throws ValidationException 如果参数校验不成功则抛出此异常
     */
    public void validate(Object object) {
        /*if(object instanceof Integer || object instanceof Long || object instanceof String ||
                object instanceof Double || object instanceof Collection<?> || object instanceof Array){
            return;
        }*/
        if(null == object)
            return;
        //执行验证
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        Iterator<ConstraintViolation<Object>> it = constraintViolations.iterator();
        ConstraintViolation<Object> constraintViolation = null;
        while(it.hasNext()){
            constraintViolation = it.next();
            if(null != constraintViolation)
                break;
        }
        if(null != constraintViolation)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION,CommonUtil.joinStr("参数",constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage()).toString());
    }

}
