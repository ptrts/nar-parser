package me.narparser.gwt.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.*;
import me.narparser.gwt.client.ClientSideContext;
import me.narparser.gwt.client.service.GwtService;
import me.narparser.gwt.client.service.GwtServiceAsync;
import me.narparser.gwt.shared.model.VariantBean;

import java.util.Date;
import java.util.List;

public class VariantForm extends VerticalPanel {

    private static final GwtServiceAsync gwtService = GWT.create(GwtService.class);

    private final Margins margins = new Margins(5, 0, 0, 5);

    private final BoxLayoutContainer.BoxLayoutData fieldLayoutData = new BoxLayoutContainer.BoxLayoutData(margins);

    public static void activate(String id, String text) {
        VariantForm form = new VariantForm(id);
        ClientSideContext.add(form, text);
    }

    private void addTextField(String name, String value, VBoxLayoutContainer vlc) {
        TextField field = new TextField();
        field.setValue(value);

        vlc.add(new FieldLabel(field, name), fieldLayoutData);
    }

    private void addDateField(String name, Date value, VBoxLayoutContainer vlc) {
        DateField field = new DateField();
        field.setValue(value);
        vlc.add(new FieldLabel(field, name), fieldLayoutData);
    }

    private void addIntField(String name, int value, VBoxLayoutContainer vlc) {
        IntegerField field = new IntegerField();
        field.setValue(value);
        vlc.add(new FieldLabel(field, name), fieldLayoutData);
    }

    private void addFloatField(String name, float value, VBoxLayoutContainer vlc) {
        FloatField field = new FloatField();
        field.setValue(value);
        vlc.add(new FieldLabel(field, name), fieldLayoutData);
    }

    private void addBooleanField(String name, boolean value, HorizontalPanel horizontalContainer) {

        CheckBox check = new CheckBox();
        check.setValue(value);
        check.setBoxLabel(name);

        horizontalContainer.add(check);
    }

    public VariantForm(String id) {

        final VerticalLayoutContainer all = new VerticalLayoutContainer();
        all.setHeight(910);
        add(all);

        all.setScrollMode(ScrollSupport.ScrollMode.AUTO);

        final HBoxLayoutContainer section1 = new HBoxLayoutContainer();
        section1.setHeight(350);
        all.add(section1);

        final VBoxLayoutContainer section1Column1 = new VBoxLayoutContainer();
        section1Column1.setWidth(300);
        section1Column1.setHeight(300);

        final VBoxLayoutContainer section1Column2 = new VBoxLayoutContainer();
        section1Column2.setWidth(300);
        section1Column2.setHeight(300);

        final VBoxLayoutContainer section1Column3 = new VBoxLayoutContainer();
        section1Column3.setWidth(300);
        section1Column3.setHeight(300);

        section1.add(section1Column1);
        section1.add(section1Column2);
        section1.add(section1Column3);

        gwtService.getVariant(id, new AsyncCallback<VariantBean>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("ERROR: " + caught.getMessage());
            }

            @Override
            public void onSuccess(VariantBean variantBean) {
                //@formatter:off
                addTextField    ("Id"         , variantBean.getId          (), section1Column1 );
                addTextField    ("Code"       , variantBean.getCode        (), section1Column1 );
                addDateField    ("ChangeDate" , variantBean.getChangeDate  (), section1Column1 );
                addDateField    ("PostDate"   , variantBean.getPostDate    (), section1Column1 );
                addIntField     ("Views"      , variantBean.getViews       (), section1Column1 );
                addTextField    ("ShortCode"  , variantBean.getShortCode   (), section1Column1 );
                addIntField     ("Price"      , variantBean.getPrice       (), section1Column1 );
                addIntField     ("Rooms"      , variantBean.getRooms       (), section1Column1 );

                addTextField    ("City"       , variantBean.getCity        (), section1Column2 );
                addTextField    ("District"   , variantBean.getDistrict    (), section1Column2 );
                addTextField    ("Street"     , variantBean.getStreet      (), section1Column2 );
                addTextField    ("Building"   , variantBean.getBuilding    (), section1Column2 );
                addTextField    ("Type"       , variantBean.getType        (), section1Column2 );
                addIntField     ("Floor"      , variantBean.getFloor       (), section1Column2 );
                addIntField     ("Floors"     , variantBean.getFloors      (), section1Column2 );
                addTextField    ("Material"   , variantBean.getMaterial    (), section1Column2 );

                addFloatField   ("Area"       , variantBean.getArea        (), section1Column3 );
                addFloatField   ("LivingArea" , variantBean.getLivingArea  (), section1Column3 );
                addFloatField   ("KitchenArea", variantBean.getKitchenArea (), section1Column3 );
                addTextField    ("Layout"     , variantBean.getLayout      (), section1Column3 );
                addTextField    ("Balcony"    , variantBean.getBalcony     (), section1Column3 );
                addTextField    ("Bathroom"   , variantBean.getBathroom    (), section1Column3 );
                addTextField    ("Phone"      , variantBean.getPhone       (), section1Column3 );
                addTextField    ("Property"   , variantBean.getProperty    (), section1Column3 );

                HorizontalPanel checkBoxes = new HorizontalPanel();

                addBooleanField ("Exchange"   , variantBean.isExchange     (), checkBoxes );
                addBooleanField ("PureSell"   , variantBean.isPureSell     (), checkBoxes );
                addBooleanField ("Open"       , variantBean.getOpen        (), checkBoxes );

                section1Column1.add(checkBoxes,  fieldLayoutData);

                TextArea descriptionField = new TextArea();
                descriptionField.setValue(variantBean.getDescription());
                descriptionField.setWidth(300);
                descriptionField.setHeight(300);
                section1.add(descriptionField, new BoxLayoutContainer.BoxLayoutData(new Margins(10, 0, 0, 0)));

                HTML html = new HTML("<div id='ymap" + variantBean.getId() + "' style='width: 304px;height: 360px; margin-top: 10px; border: 1px solid #ddd;'></div>");
                section1.add(html, new BoxLayoutContainer.BoxLayoutData(new Margins(0, 0, 0, 10)));

                //@formatter:on

                List<String> imageFileNames = variantBean.getImageFileNames();
                if (imageFileNames != null) {
                    for (String imageFileName : imageFileNames) {
                        Image image = new Image(imageFileName);
                        image.setWidth("400px");
                        all.add(image);
                    }
                }

                all.forceLayout();

                buildOurYandexMap(variantBean.getId(), variantBean.getStreet(), variantBean.getBuilding());
            }
        });
    }

    public static native void buildOurYandexMap(String id, String street, String building) /*-{
        $wnd.buildOurYandexMap(id, street, building);
    }-*/;
}
