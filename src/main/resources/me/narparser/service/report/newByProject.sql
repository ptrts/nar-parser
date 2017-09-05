SELECT
  vd.variant_id,
  date(q.postDate)                                       AS postDate,
  vd.rooms,
  concat_ws(', ', vd.street, vd.building)                AS address,
  concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea) AS area,
  concat_ws('/', vd.floor, vd.floors)                    AS floor,
  vd.price
FROM
  (
    SELECT
      vd.variant_id,
      max(q.postDate) AS postDate,
      max(vd.loadingDate) AS loadingDate
    FROM
      VariantData vd

      JOIN Variant_Project vp
        ON vp.Variant_id = vd.variant_id
           AND vp.projects_id = :project_id

      JOIN
      (
        SELECT
          st.variant_id,
          st.loadingDate AS postDate
        FROM
          VariantStatusChange st
        WHERE
          st.open
          AND st.loadingDate >= '2017-07-29'
      ) q
        ON vd.variant_id = q.variant_id
    GROUP BY
      vd.variant_id
  ) q
  JOIN VariantData vd
    ON q.variant_id = vd.variant_id
       AND q.loadingDate = vd.loadingDate
ORDER BY
  rooms,
  postDate