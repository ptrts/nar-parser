SELECT
  vd.rooms,
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
      max(vd.loadingDate) AS loadingDate
    FROM
      VariantData vd
      JOIN
      (
        SELECT DISTINCT variant_id
        FROM variantstatuschange
        WHERE NOT open
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
  date;


SELECT
  q.variant_id,
  date(q.loadingDate) AS sellDate
FROM
  (
    SELECT
      st.variant_id,
      max(st.loadingDate) AS loadingDate
    FROM variantstatuschange st
    GROUP BY
      st.variant_id
  ) q
  JOIN variantstatuschange st
    ON q.variant_id = st.variant_id
       AND q.loadingDate = st.loadingDate
       AND NOT st.open;

SELECT
  vd.rooms,
  q.sellDate                                                           AS sellDate,
  q.priceDate                                                          AS priceDate,
  concat_ws(', ', vd.street, vd.building)                              AS address,
  concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea)               AS area,
  concat_ws('/', vd.floor, vd.floors)                                  AS floor,
  vd.layout,
  group_concat(log.newValue ORDER BY (log.loadingDate) SEPARATOR ', ') AS prices,
  vd.variant_id,
  vd.description
FROM
  (
    SELECT
      vd.variant_id,
      max(vd.loadingDate) AS loadingDate,
      q.sellDate,
      q.priceDate
    FROM
      VariantData vd
      JOIN
      (
        SELECT
          q.variant_id,
          max(q.sellDate)  AS sellDate,
          max(q.priceDate) AS priceDate
        FROM
          (
            SELECT
              q.variant_id,
              date(q.loadingDate) AS sellDate,
              NULL                AS priceDate
            FROM
              (
                SELECT
                  st.variant_id,
                  max(st.loadingDate) AS loadingDate
                FROM variantstatuschange st
                GROUP BY
                  st.variant_id
              ) q
              JOIN variantstatuschange st
                ON q.variant_id = st.variant_id
                   AND q.loadingDate = st.loadingDate
                   AND NOT st.open

            UNION ALL

            SELECT
              log.variant_id,
              NULL                 AS loadingDate,
              max(log.loadingDate) AS priceDate
            FROM
              propertychangelogentries log
            WHERE
              log.property = 'price'
            GROUP BY
              log.variant_id
            HAVING
              count(*) > 1
          ) q
        GROUP BY
          q.variant_id
      ) q
        ON vd.variant_id = q.variant_id
    GROUP BY
      vd.variant_id
  ) q
  JOIN VariantData vd
    ON vd.variant_id = q.variant_id
       AND vd.loadingDate = q.loadingDate
  JOIN propertychangelogentries log
    ON log.variant_id = q.variant_id
       AND log.property = 'price'
GROUP BY
  q.variant_id
ORDER BY
  rooms,
  sellDate, priceDate;

SELECT
  q.variant_id,
  q.loadingDate,
  q.rooms,
  q.address,
  q.area,
  q.floor,
  q.layout,
  group_concat(log.newValue ORDER BY (log.loadingDate) SEPARATOR ', ') AS prices,
  q.description
FROM
  (
    SELECT
      q.variant_id,
      vd.loadingDate,
      vd.rooms,
      concat_ws(', ', vd.street, vd.building)                AS address,
      concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea) AS area,
      concat_ws('/', vd.floor, vd.floors)                    AS floor,
      vd.layout,
      vd.price,
      vd.description
    FROM
      (
        SELECT
          vd.variant_id,
          max(vd.loadingDate) AS loadingDate
        FROM
          (
            SELECT log.variant_id
            FROM
              propertychangelogentries log
            WHERE
              log.property = 'price'
            GROUP BY
              log.variant_id
            HAVING
              count(*) > 1
          ) q
          JOIN VariantData vd
            ON vd.variant_id = q.variant_id
        GROUP BY
          vd.variant_id
      ) q
      JOIN VariantData vd
        ON vd.variant_id = q.variant_id
           AND vd.loadingDate = q.loadingDate
  ) q
  JOIN propertychangelogentries log
    ON log.variant_id = q.variant_id
       AND log.property = 'price'
GROUP BY
  q.variant_id
