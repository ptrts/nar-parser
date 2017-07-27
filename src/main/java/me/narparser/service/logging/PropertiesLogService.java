package me.narparser.service.logging;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.narparser.model.auxilary.PropertyChangeLogEntry;
import me.narparser.model.business.Loading;
import me.narparser.model.business.Variant;
import me.narparser.model.business.VariantData;

@Service
public class PropertiesLogService {

    private final Set<String> notLoggedProperties = new HashSet<>(
            Arrays.asList("class", "variant", "loading", "loadingDate"));

    @Autowired
    private BeanWrapperService beanWrapperService;

    @Autowired
    private HibernateTemplate hibernate;

    private boolean isPropertyLogged(String name) {
        return !notLoggedProperties.contains(name);
    }

    private boolean isPropertyChanged(Object oldValue, Object newValue) {

        if (ObjectUtils.equals(oldValue, newValue)) {
            return false;
        }

        if (oldValue != null && oldValue.getClass() == BigDecimal.class
                &&
                newValue != null && newValue.getClass() == BigDecimal.class) {

            BigDecimal bdOldValue = (BigDecimal) oldValue;
            BigDecimal bdNewValue = (BigDecimal) newValue;
            if (bdOldValue.compareTo(bdNewValue) == 0) {
                return false;
            }
        }

        if (oldValue != null && oldValue instanceof Date
                &&
                newValue != null && newValue instanceof Date) {

            Date typedOldValue = (Date) oldValue;
            Date typedNewValue = (Date) newValue;

            return typedOldValue.getTime() != typedNewValue.getTime();
        }

        return true;
    }

    private void logPropertyChange(Variant variant, Loading loading, String name, Object oldValue, Object newValue) {

        PropertyChangeLogEntry entry = new PropertyChangeLogEntry();

        entry.setLoadingDate(loading.getLoadingDate());

        entry.setVariant(variant);
        entry.setProperty(name);

        entry.setLoading(loading);

        entry.setOldValue("" + oldValue);
        entry.setNewValue("" + newValue);

        hibernate.save(entry);
    }

    @Transactional
    public boolean logChangedProperties(VariantData oldBean, VariantData newBean) {

        boolean somePropertyWasChanged = false;

        BeanWrapperImpl beanWrapper = beanWrapperService.getWrapper(newBean.getClass());

        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Map<String, Object> oldValues;

        if (oldBean == null) {
            oldValues = Collections.emptyMap();
        } else {
            beanWrapper.setWrappedInstance(oldBean);

            oldValues = new HashMap<>(propertyDescriptors.length);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String name = propertyDescriptor.getName();
                Object value = beanWrapper.getPropertyValue(name);
                oldValues.put(name, value);
            }
        }

        beanWrapper.setWrappedInstance(newBean);

        for (PropertyDescriptor propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
            String name = propertyDescriptor.getName();
            Object oldValue = oldValues.get(name);
            Object newValue = beanWrapper.getPropertyValue(name);

            if (isPropertyLogged(name) && isPropertyChanged(oldValue, newValue)) {

                somePropertyWasChanged = true;

                logPropertyChange(newBean.getVariant(), newBean.getLoading(), name, oldValue, newValue);
            }
        }

        return somePropertyWasChanged;
    }
}
