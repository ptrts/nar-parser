package me.narparser.gwt.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.narparser.gwt.client.service.GwtService;
import me.narparser.gwt.shared.model.VariantBean;
import me.narparser.gwt.shared.model.report.ReportModel;
import me.narparser.service.ReportService;

@Service("gwtService")
public class GwtServiceImpl implements GwtService {

    @Autowired
    private HibernateTemplate hibernate;

    @Autowired
    private ReportService reportService;
    
    @Value("${application.images.directory}")
    private String folder;

    @Override
    @Transactional
    public List<VariantBean> getVariantBeans(int projectId) {

        Session session = hibernate.getSessionFactory().getCurrentSession();

        String sql =
                "SELECT\n" +
                        "  vd.variant_id as id,\n" +
                        "  q.code,\n" +
                        "  vd.district,\n" +
                        "  vd.street,\n" +
                        "  vd.building,\n" +
                        "  vd.floor,\n" +
                        "  vd.type,\n" +
                        "  vd.price,\n" +
                        "  sc.open\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      vd.variant_id,\n" +
                        "      max(v.code)         AS code,\n" +
                        "      max(vd.loadingDate) AS loadingDate\n" +
                        "    FROM\n" +
                        "      Variant v\n" +
                        "      JOIN VariantData vd\n" +
                        "        ON vd.variant_id = v.id\n" +
                        "    WHERE\n" +
                        "      v.project_id = :project_id\n" +
                        "    GROUP BY\n" +
                        "      vd.variant_id\n" +
                        "  ) q\n" +
                        "  JOIN VariantData vd\n" +
                        "    ON vd.variant_id = q.variant_id\n" +
                        "       AND vd.loadingDate = q.loadingDate\n" +
                        "  JOIN\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      sc.variant_id,\n" +
                        "      max(sc.loadingDate) AS loadingDate\n" +
                        "    FROM\n" +
                        "      Variant v\n" +
                        "      JOIN VariantStatusChange sc\n" +
                        "        ON sc.variant_id = v.id\n" +
                        "    WHERE\n" +
                        "      v.project_id = :project_id\n" +
                        "    GROUP BY\n" +
                        "      sc.variant_id\n" +
                        "  ) q2\n" +
                        "    ON q.variant_id = q2.variant_id\n" +
                        "  JOIN VariantStatusChange sc\n" +
                        "    ON sc.variant_id = q2.variant_id\n" +
                        "       AND sc.loadingDate = q2.loadingDate";

        Query query = session.createSQLQuery(sql)
                .setInteger("project_id", projectId)
                .setResultTransformer(new AliasToBeanResultTransformer(VariantBean.class));

        @SuppressWarnings({ "UnnecessaryLocalVariable", "unchecked" })
        List<VariantBean> list = query.list();

        return list;
    }

    @Override
    public ReportModel getReport(String name, Map<String, Serializable> param) {
        return reportService.getReport(name, param);
    }

    @Override
    @Transactional
    public VariantBean getVariant(String id) {

        String sql =
                "SELECT\n" +
                        "  v.id,\n" +
                        "  v.code,\n" +
                        "  st.open,\n" +
                        "  vd.area,\n" +
                        "  vd.balcony,\n" +
                        "  vd.bathroom,\n" +
                        "  vd.building,\n" +
                        "  vd.changeDate,\n" +
                        "  vd.city,\n" +
                        "  vd.description,\n" +
                        "  vd.district,\n" +
                        "  vd.exchange,\n" +
                        "  vd.floor,\n" +
                        "  vd.floors,\n" +
                        "  vd.kitchenArea,\n" +
                        "  vd.layout,\n" +
                        "  vd.livingArea,\n" +
                        "  vd.material,\n" +
                        "  vd.phone,\n" +
                        "  vd.postDate,\n" +
                        "  vd.price,\n" +
                        "  vd.property,\n" +
                        "  vd.pureSell,\n" +
                        "  vd.rooms,\n" +
                        "  vd.shortCode,\n" +
                        "  vd.street,\n" +
                        "  vd.type,\n" +
                        "  vd.views\n" +
                        "FROM\n" +
                        "  Variant v\n" +
                        "\n" +
                        "  JOIN\n" +
                        "  (\n" +
                        "    SELECT max(st.loadingDate) AS loadingDate\n" +
                        "    FROM\n" +
                        "      VariantStatusChange st\n" +
                        "    WHERE\n" +
                        "      st.variant_id = :variant_id\n" +
                        "  ) stMax\n" +
                        "    ON TRUE\n" +
                        "\n" +
                        "  JOIN VariantStatusChange st\n" +
                        "    ON st.variant_id = :variant_id\n" +
                        "       AND stMax.loadingDate = st.loadingDate\n" +
                        "\n" +
                        "  JOIN\n" +
                        "  (\n" +
                        "    SELECT max(vd.loadingDate) AS loadingDate\n" +
                        "    FROM\n" +
                        "      VariantData vd\n" +
                        "    WHERE\n" +
                        "      vd.variant_id = :variant_id\n" +
                        "  ) vdMax\n" +
                        "    ON TRUE\n" +
                        "\n" +
                        "  JOIN VariantData vd\n" +
                        "    ON vd.variant_id = :variant_id\n" +
                        "       AND vdMax.loadingDate = vd.loadingDate\n" +
                        "\n" +
                        "WHERE\n" +
                        "  v.id = :variant_id";

        Session session = hibernate.getSessionFactory().getCurrentSession();

        Query query = session.createSQLQuery(sql)
                .setString("variant_id", id)
                .setResultTransformer(new AliasToBeanResultTransformer(VariantBean.class));

        @SuppressWarnings({ "UnnecessaryLocalVariable", "unchecked" })
        List<VariantBean> list = query.list();

        List<String> fileNames = new ArrayList<>();

        File dir = new File(folder, id);

        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                name = name.replaceAll("\\.(\\w+)$", "-$1");
                fileNames.add("image/" + id + "/" + name);
            }
        }

        VariantBean variantBean = list.get(0);

        variantBean.setImageFileNames(fileNames);

        return variantBean;
    }
}
