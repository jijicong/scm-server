package org.trc.biz;

import org.trc.domain.category.Property;
import org.trc.form.PropertyForm;
import org.trc.util.Pagenation;

/**
 * Created by hzqph on 2017/5/5.
 */
public interface IPropertyBiz {

    public Pagenation<Property> propertyPage(PropertyForm queryModel, Pagenation<Property> page) throws Exception;
}
