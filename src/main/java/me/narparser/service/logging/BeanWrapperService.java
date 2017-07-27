package me.narparser.service.logging;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import me.narparser.model.business.VariantData;
import me.narparser.model.business.VariantStatusChange;

@Service
public class BeanWrapperService {

    private final BeanWrapperImpl variantDataWrapper = new BeanWrapperImpl(VariantData.class);

    private final BeanWrapperImpl variantStatusChangeWrapper = new BeanWrapperImpl(VariantStatusChange.class);

    public BeanWrapperImpl getWrapper(Class clazz) {
        if (clazz == VariantData.class) {
            return variantDataWrapper;
        } else if (clazz == VariantStatusChange.class) {
            return variantStatusChangeWrapper;
        } else {
            return null;
        }
    }
}
