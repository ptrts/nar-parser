SELECT
  q.variant_id,
  q.postDate,
  q.loadingDate,
  q.rooms,
  q.address,
  q.area,
  q.floor,
  q.layout,
  group_concat(log.newValue ORDER BY (log.loadingDate) SEPARATOR ', ') AS prices,
  group_concat(date_format(log.loadingDate,'%d-%m-%Y') ORDER BY (log.loadingDate) SEPARATOR ', ') AS dates,
  q.description
FROM
  (
    SELECT
      q.variant_id,
      vd.loadingDate,
      vd.postDate,
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
