SELECT
  vd.variant_id,
  q.code,
  vd.district,
  vd.rooms,
  concat_ws(', ', vd.street, vd.building)                AS address,
  concat_ws('/', vd.area, vd.livingArea, vd.kitchenArea) AS area,
  concat_ws('/', vd.floor, vd.floors)                    AS floor,
  vd.type,
  vd.price,
  sc.open
FROM
  (
    SELECT
      vd.variant_id,
      max(v.code)         AS code,
      max(vd.loadingDate) AS loadingDate
    FROM
      Variant v
      JOIN VariantData vd
        ON vd.variant_id = v.id
    WHERE
      v.project_id = :project_id
    GROUP BY
      vd.variant_id
  ) q
  JOIN VariantData vd
    ON vd.variant_id = q.variant_id
       AND vd.loadingDate = q.loadingDate
  JOIN
  (
    SELECT
      sc.variant_id,
      max(sc.loadingDate) AS loadingDate
    FROM
      Variant v
      JOIN VariantStatusChange sc
        ON sc.variant_id = v.id
    WHERE
      v.project_id = :project_id
    GROUP BY
      sc.variant_id
  ) q2
    ON q.variant_id = q2.variant_id
  JOIN VariantStatusChange sc
    ON sc.variant_id = q2.variant_id
       AND sc.loadingDate = q2.loadingDate