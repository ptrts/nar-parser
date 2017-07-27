SELECT
  vd.variant_id,
  vd.rooms,
  date(vd.postDate)                                      AS postDate,
  date(q.sellDate)                                       AS sellDate,
  datediff(q.sellDate, vd.postDate)                      AS days,
  concat_ws(', ', vd.street, vd.building)                AS address,
  concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea) AS area,
  concat_ws('/', vd.floor, vd.floors)                    AS floor,
  vd.price,
  vd.views
FROM
  (
    SELECT
      vd.variant_id,
      q.sellDate,
      max(vd.loadingDate) AS loadingDate
    FROM
      VariantData vd
      JOIN
      (
        SELECT
          q.variant_id,
          date(q.loadingDate) AS sellDate
        FROM
          (
            SELECT
              st.variant_id,
              max(st.loadingDate) AS loadingDate
            FROM VariantStatusChange st
            GROUP BY
              st.variant_id
          ) q
          JOIN VariantStatusChange st
            ON q.variant_id = st.variant_id
               AND q.loadingDate = st.loadingDate
               AND NOT st.open
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
  sellDate,
  postDate