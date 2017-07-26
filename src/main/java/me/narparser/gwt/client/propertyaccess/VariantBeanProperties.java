package me.narparser.gwt.client.propertyaccess;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import me.narparser.gwt.shared.model.VariantBean;

public interface VariantBeanProperties extends PropertyAccess<VariantBean> {

  ModelKeyProvider<VariantBean> id();

  ValueProvider<VariantBean, String> code();
  ValueProvider<VariantBean, String> district();
  ValueProvider<VariantBean, String> street();
  ValueProvider<VariantBean, String> building();
  ValueProvider<VariantBean, Integer> floor();
  ValueProvider<VariantBean, String> type();
  ValueProvider<VariantBean, Integer> price();
  ValueProvider<VariantBean, Boolean> open();
}