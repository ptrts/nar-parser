SELECT
  vd.rooms,
  q.postDate,
  date(vd.loadingDate)                                   AS date,
  concat_ws(', ', vd.street, vd.building)                AS address,
  concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea) AS area,
  concat_ws('/', vd.floor, vd.floors)                    AS floor,
  vd.layout,
  vd.price,
  vd.variant_id,
  vd.description
FROM
  (
    SELECT
      vd.variant_id,
      q.postDate,
      max(vd.loadingDate) AS loadingDate
    FROM
      VariantData vd
      JOIN
      (
        SELECT
          st.variant_id,
          st.loadingDate AS postDate
        FROM
          variantstatuschange st
        where
          st.open
          and st.loadingDate >= '2015-07-15'
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