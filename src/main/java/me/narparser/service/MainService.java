package me.narparser.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StringType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import me.narparser.model.business.Loading;
import me.narparser.model.business.Photo;
import me.narparser.model.business.Project;
import me.narparser.model.business.Variant;
import me.narparser.model.business.VariantData;
import me.narparser.model.business.VariantFromList;
import me.narparser.model.business.VariantStatusChange;
import me.narparser.service.logging.PropertiesLogService;

@Service
public class MainService {

    private static final String SERVICE_HOST = "realty.nar.ru";

    private static final String SERVICE_URL = "http://" + SERVICE_HOST;

    private static final String GET_VARIANT_METHOD_PATH = "result/object";

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private PropertiesLogService propertiesLogService;

    @Autowired
    private HibernateTemplate hibernate;

    @Value("${application.images.directory}")
    private String folder;
    
    private MainService thisBeanProxy;
    
    @PostConstruct
    private void postConstruct() {
        thisBeanProxy = applicationContext.getBean(MainService.class);
    }

    @Transactional
    public void loadList(PrintWriter out) {

        // Шаблон на последовательность цифр
        Pattern pattern = Pattern.compile("\\d+");

        try {

            // Очистить VariantFromList
            // Это буферная таблица, из которой, после обработки, данные пойдут в Variant
            hibernate.bulkUpdate("delete from VariantFromList");

            @SuppressWarnings("unchecked")
            List<Project> projects = (List<Project>) hibernate.find("from Project order by id");

            for (Project project : projects) {
                
                out.println("Проект №" + project.getId() + ": " + project.getName());

                // Делаем список для вариантов
                List<VariantFromList> variants = new ArrayList<>(100);

                Connection connection = Jsoup.connect(SERVICE_URL + "/" + project.getListUrl());

                connection.timeout(10000);

                Document doc = connection.get();

                Element table = doc.getElementById("o-results");

                Elements rows = table.getElementsByTag("tr");

                Iterator<Element> it = rows.iterator();

                // Строка с заголовками
                it.next();

                // Строки с вариантами
                while (it.hasNext()) {

                    Element tr = it.next();

                    Iterator<Element> tds = tr.children().iterator();

                    // Фото
                    tds.next();

                    // Ссылка
                    Element a = tds.next().getElementsByTag("a").first();
                    String href = a.attr("href");
                    String code = a.text();

                    Matcher matcher = pattern.matcher(href);

                    Validate.isTrue(matcher.find());

                    String id = matcher.group();

                    VariantFromList variant = new VariantFromList();
                    variant.setId(id);
                    variant.setCode(code);

                    variants.add(variant);
                }

                for (VariantFromList variant : variants) {

                    VariantFromList existingVariant = hibernate.get(VariantFromList.class, variant.getId());

                    if (existingVariant == null) {
                        variant.getProjects().add(project);
                        hibernate.saveOrUpdate(variant);
                    } else {
                        existingVariant.getProjects().add(project);
                    }
                }

                hibernate.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void loadVariants() {

        Loading loading = getLastLoading();

        String sql =
                "SELECT\n" +
                        "  q.variant_id,\n" +
                        "  max(q.newVariant_code)     AS newVariant_code,\n" +
                        "  max(q.wasOpen)             AS wasOpen,\n" +
                        "  max(q.nowOpen)             AS nowOpen,\n" +
                        "  max(q.wasInDb)             AS wasInDb\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      vfl.id   AS variant_id,\n" +
                        "      vfl.code AS newVariant_code,\n" +
                        "      FALSE    AS wasOpen,\n" +
                        "      TRUE     AS nowOpen,\n" +
                        "      FALSE    AS wasInDb\n" +
                        "    FROM\n" +
                        "      VariantFromList vfl\n" +
                        "\n" +
                        "    UNION ALL\n" +
                        "\n" +
                        "    SELECT\n" +
                        "      sc.variant_id,\n" +
                        "      NULL           AS newVariant_code,\n" +
                        "      sc.open        AS wasOpen,\n" +
                        "      FALSE          AS nowOpen,\n" +
                        "      TRUE           AS wasInDb\n" +
                        "    FROM\n" +
                        "      (\n" +
                        "        SELECT\n" +
                        "          sc.variant_id,\n" +
                        "          max(sc.loadingDate) AS loadingDate\n" +
                        "        FROM\n" +
                        "          VariantStatusChange sc\n" +
                        "          JOIN Variant v\n" +
                        "            ON sc.variant_id = v.id\n" +
                        "        GROUP BY\n" +
                        "          sc.variant_id\n" +
                        "      ) q\n" +
                        "      JOIN VariantStatusChange sc\n" +
                        "        ON sc.variant_id = q.variant_id\n" +
                        "           AND sc.loadingDate = q.loadingDate\n" +
                        "      JOIN Variant v\n" +
                        "        ON q.variant_id = v.id\n" +
                        "  ) q\n" +
                        "GROUP BY\n" +
                        "  q.variant_id";

        Query query = hibernate.getSessionFactory().getCurrentSession().createSQLQuery(sql)
                .addScalar("variant_id", StringType.INSTANCE)
                .addScalar("newVariant_code", StringType.INSTANCE)
                .addScalar("wasOpen", BooleanType.INSTANCE)
                .addScalar("nowOpen", BooleanType.INSTANCE)
                .addScalar("wasInDb", BooleanType.INSTANCE)
                .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> queryRows = query.list();

        for (Map<String, Object> row : queryRows) {

            //@formatter:off
            String  variantId          = (String)  row.get("variant_id");
            String  newVariantCode     = (String)  row.get("newVariant_code");
            Boolean wasOpen            = (Boolean) row.get("wasOpen");
            Boolean nowOpen            = (Boolean) row.get("nowOpen");
            Boolean wasInDb            = (Boolean) row.get("wasInDb");
            //@formatter:on

            System.out.println(
                    "variantId = " + variantId +
                            ", newVariantCode = " + newVariantCode +
                            ", wasOpen = " + wasOpen +
                            ", nowOpen = " + nowOpen
            );

            Variant variant;
            if (wasInDb) {
                variant = hibernate.get(Variant.class, variantId);
            } else {
                variant = new Variant();
                variant.setId(variantId);
                hibernate.save(variant);
            }
            
            if (nowOpen) {
                VariantFromList variantFromList = hibernate.get(VariantFromList.class, variantId);
                // Копируем проекты загруженного варианта, в созданный или обновляемый вариант
                variant.setProjects(new ArrayList<>(variantFromList.getProjects()));
            }

            // Обновить код
            if (newVariantCode != null) {
                variant.setCode(newVariantCode);
            }

            // Изменяем статус, если он изменился
            if (wasOpen != nowOpen) {
                VariantStatusChange variantStatusChange = new VariantStatusChange();
                variantStatusChange.setVariant(variant);
                variantStatusChange.setLoading(loading);
                variantStatusChange.setLoadingDate(loading.getLoadingDate());
                variantStatusChange.setOpen(nowOpen == Boolean.TRUE);

                hibernate.save(variantStatusChange);
            }
        }

        hibernate.flush();
    }

    public void loadVariantsData(PrintWriter out) {

        int loadingId = thisBeanProxy.getLastLoadingId();

        Session session = hibernate.getSessionFactory().openSession();

        // Запрашиваем список открытых вариантов
        String sql =
                "SELECT v.*\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      st.variant_id,\n" +
                        "      max(st.loadingDate) AS loadingDate\n" +
                        "    FROM\n" +
                        "      VariantStatusChange st\n" +
                        "    GROUP BY\n" +
                        "      st.variant_id\n" +
                        "  ) q\n" +
                        "  JOIN VariantStatusChange st\n" +
                        "    ON q.variant_id = st.variant_id\n" +
                        "       AND q.loadingDate = st.loadingDate\n" +
                        "       AND st.open\n" +
                        "  JOIN Variant v\n" +
                        "    ON q.variant_id = v.id\n" +
                        "WHERE\n" +
                        "  v.id NOT IN (\n" +
                        "    SELECT vd.variant_id\n" +
                        "    FROM VariantData vd\n" +
                        "    WHERE vd.loading_id = :loading_id\n" +
                        "  )";

        @SuppressWarnings("unchecked")
        List<Variant> list = session.createSQLQuery(sql)
                .addEntity("v", Variant.class)
                .setInteger("loading_id", loadingId)
                .list();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            
            // По каждому открытому на данный момент варианту
            for (Variant variant : list) {

                out.print("" + variant.getId() + "(" + variant.getCode() + ")...");
                out.flush();

                try {
                    
                    // Загружаем его данные несколькими отдельными HTTP запросами
                    thisBeanProxy.loadVariantData(loadingId, variant.getId(), httpClient);
                    
                    System.out.println("OK");
                    out.println("OK");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR");
                    out.println("ERROR");
                }

                out.flush();
            }
        } finally {
            IOUtils.closeQuietly(httpClient);
        }
    }

    public void loadVariantsWithData(PrintWriter out) {

        thisBeanProxy.loadVariants();
        thisBeanProxy.loadVariantsData(out);
    }

    private Loading getLastLoading() {

        Session session = hibernate.getSessionFactory().getCurrentSession();

        Iterator it = session.createQuery("from Loading order by loadingDate desc")
                .setMaxResults(1)
                .iterate();

        Validate.isTrue(it.hasNext());

        return (Loading) it.next();
    }
    
    @Transactional
    public int getLastLoadingId() {
        return getLastLoading().getId();
    }

    private VariantData getLastVariantData(Variant variant) {

        Session session = hibernate.getSessionFactory().getCurrentSession();

        Iterator it = session.createQuery("from VariantData where variant = :variant order by loadingDate desc")
                .setParameter("variant", variant)
                .setMaxResults(1)
                .iterate();

        if (it.hasNext()) {
            return (VariantData) it.next();
        } else {
            return null;
        }
    }

    @Transactional
    public void loadVariantData(int loadingId, String variantId, CloseableHttpClient httpClient) {

        Loading loading = hibernate.get(Loading.class, loadingId);

        Variant variant = hibernate.get(Variant.class, variantId);
        
        System.out.println("=== " + variant.getCode());

        String variantUrl = SERVICE_URL + "/" + GET_VARIANT_METHOD_PATH + "/" + variantId;

        Connection connection = Jsoup.connect(variantUrl);

        connection.timeout(10000);

        Document doc;
        try {
            doc = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String info = doc
                .getElementsByClass("object-center").first()
                .getElementsByClass("object-num").first()
                .text();

        loadVariantPhotos(variant, doc, httpClient);

        // Вариант № 17_000991673, обновлен 11.07.2015, опубликован 21.04.2015, проверенный вариант
        info = info.replace("\u00A0", " ");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Pattern pattern = Pattern.compile(
                ",\\s*обновлен\\s*(\\d{2}\\.\\d{2}\\.\\d{4})" +
                        ",\\s*опубликован\\s*(\\d{2}\\.\\d{2}\\.\\d{4})"
        );

        Matcher matcher = pattern.matcher(info);

        Validate.isTrue(matcher.find());

        String changeDateStr = matcher.group(1);
        String postDateStr = matcher.group(2);

        Date changeDate;
        Date postDate;
        try {
            changeDate = sdf.parse(changeDateStr);
            postDate = sdf.parse(postDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Element table = doc.getElementsByClass("result").first();

        Elements rows = table.getElementsByTag("tr");

        Map<String, String> map = new LinkedHashMap<>(50);

        for (Element row : rows) {

            Elements cells = row.getElementsByTag("td");

            if (cells.size() == 2) {

                String key = cells.first().text();
                String value = cells.last().text();
                map.put(key, value);

            } else if (cells.size() == 1) {

                Elements pair = cells.first().children();

                Validate.isTrue(pair.size() == 2);

                String key = pair.first().text();
                String value = pair.last().text();
                map.put(key, value);
            }
        }

        //@formatter:off
        String views       = map.get("Просмотров"          );
        String shortCode   = map.get("№ варианта"         );
        String price       = map.get("Цена, т.р."          );
        String rooms       = map.get("Количество комнат"   );
        String city        = map.get("Населенный пункт"    );
        String district    = map.get("Район"               );
        String street      = map.get("Улица"               );
        String building    = map.get("Номер дома"          );
        String type        = map.get("Тип квартиры"        );
        String floor       = map.get("Этаж"                );
        String floors      = map.get("Этажность"           );
        String material    = map.get("Материал дома"       );
        String area        = map.get("Общая площадь, м2"   );
        String livingArea  = map.get("Жилая площадь, м2"   );
        String kitchenArea = map.get("Площадь кухни, м2"   );
        String layout      = map.get("Планировка"          );
        String balcony     = map.get("Балкон/Лоджия"       );
        String bathroom    = map.get("Санузел"             );
        String phone       = map.get("Телефон"             );
        String pureSell    = map.get("Чистая продажа"      );
        String exchange    = map.get("Обмен"               );
        String property    = map.get("Форма собственности" );
        String description = map.get("Описание объекта"    );
        //@formatter:on

        area = area.replace(",", ".");
        livingArea = livingArea.replace(",", ".");
        kitchenArea = kitchenArea.replace(",", ".");

        VariantData variantData = new VariantData();

        //@formatter:off
        variantData.setLoading     ( loading                       );
        variantData.setLoadingDate ( loading.getLoadingDate()      );
        variantData.setPostDate    ( postDate                      );
        variantData.setChangeDate  ( changeDate                    );
        variantData.setVariant     ( variant                       );
        variantData.setViews       ( parseInt(views)       );
        variantData.setShortCode   ( shortCode                     );
        variantData.setPrice       ( parseInt(price)       );
        variantData.setRooms       ( parseInt(rooms)       );
        variantData.setCity        ( city                          );
        variantData.setDistrict    ( district                      );
        variantData.setStreet      ( street                        );
        variantData.setBuilding    ( building                      );
        variantData.setType        ( type                          );
        variantData.setFloor       ( parseInt(floor)       );
        variantData.setFloors      ( parseInt(floors)      );
        variantData.setMaterial    ( material                      );
        variantData.setArea        ( parseFloat(area)        );
        variantData.setLivingArea  ( parseFloat(livingArea)  );
        variantData.setKitchenArea ( parseFloat(kitchenArea) );
        variantData.setLayout      ( layout                        );
        variantData.setBalcony     ( balcony                       );
        variantData.setBathroom    ( bathroom                      );
        variantData.setPhone       ( phone                         );
        variantData.setPureSell    ( pureSell.equals("+")          );
        variantData.setExchange    ( exchange.equals("+")          );
        variantData.setProperty    ( property                      );
        variantData.setDescription ( description                   );
        //@formatter:on

        VariantData lastVariantData = getLastVariantData(variant);

        if (!variantData.sameData(lastVariantData)) {
            hibernate.saveOrUpdate(variantData);

            propertiesLogService.logChangedProperties(lastVariantData, variantData);
        }

        variant.setLastLoadingDate(loading.getLoadingDate());
    }

    private int parseInt(String str) {
        if ("-".equals(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private float parseFloat(String str) {
        if ("-".equals(str)) {
            return 0;
        }
        return Float.parseFloat(str);
    }

    private void loadVariantPhotos(Variant variant, Document doc, CloseableHttpClient httpClient) {
        Element objectPhoto = doc.getElementById("object-photo");
        if (objectPhoto != null) {

            Elements photoLinks = objectPhoto.getElementsByClass("img_zoom");

            for (Element photoLink : photoLinks) {

                String href = photoLink.attr("href");

                href = href.replace(" ", "%20");

                URI resource;
                try {
                    resource = new URI(href);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                File resourcePath = new File(resource.getPath());

                String fileName = resourcePath.getName();

                System.out.println("    " + fileName);

                Photo photo = variant.getPhotos().get(fileName);

                if (photo == null) {
                    photo = new Photo();
                    photo.setVariant(variant);
                    photo.setFileName(fileName);

                    hibernate.save(photo);

                    variant.getPhotos().put(fileName, photo);

                    InputStream in = null;
                    OutputStream out = null;

                    CloseableHttpResponse response = null;

                    try {
                        response = getHttpResponse(httpClient, resource);

                        HttpEntity entity = response.getEntity();

                        String contentType = entity.getContentType().getValue();
                        long contentLength = entity.getContentLength();

                        if (contentLength != photo.getLength() || !Objects.equals(contentType,
                                photo.getContentType())) {

                            photo.setLength(contentLength);
                            photo.setContentType(contentType);
                            photo.setUrl(href);

                            in = entity.getContent();

                            File file = getPhotoFile(variant, fileName);

                            out = new BufferedOutputStream(new FileOutputStream(file));

                            IOUtils.copy(in, out);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        IOUtils.closeQuietly(in);
                        IOUtils.closeQuietly(out);
                        IOUtils.closeQuietly(response);
                    }
                }
            }
        }
    }

    private File getPhotoFile(Variant variant, String fileName) {
        
        Path path = Paths.get(folder, variant.getId());

        File dir = path.toFile();

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        return new File(dir, fileName);
    }

    private CloseableHttpResponse getHttpResponse(CloseableHttpClient httpClient, URI resource) throws IOException {

        HttpGet request = new HttpGet(resource);

        return httpClient.execute(request, new BasicHttpContext());
    }

    @SuppressWarnings("unused")
    public Collection getSessionObjects() {
        Session s = hibernate.getSessionFactory().getCurrentSession();
        SessionImplementor sessionImpl = (SessionImplementor) s;
        PersistenceContext persistenceContext = sessionImpl.getPersistenceContext();
        return persistenceContext.getEntitiesByKey().values();
    }
    
    @Transactional
    public void createLoading() {
        
        Loading loading = new Loading();
        loading.setLoadingDate(new Date());
        hibernate.save(loading);
        
        hibernate.bulkUpdate("delete from VariantFromList");
    }
}
