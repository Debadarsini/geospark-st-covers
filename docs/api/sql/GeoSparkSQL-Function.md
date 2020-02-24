## ST_Distance

Introduction: Return the Euclidean distance between A and B

Format: `ST_Distance (A:geometry, B:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Distance(polygondf.countyshape, polygondf.countyshape)
FROM polygondf
```

## ST_ConvexHull

Introduction: Return the Convex Hull of polgyon A

Format: `ST_ConvexHull (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_ConvexHull(polygondf.countyshape)
FROM polygondf
```

## ST_Envelope

Introduction: Return the envelop boundary of A

Format: `ST_Envelope (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```
SELECT ST_Envelope(polygondf.countyshape)
FROM polygondf
```

## ST_Length

Introduction: Return the perimeter of A

Format: ST_Length (A:geometry)

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Length(polygondf.countyshape)
FROM polygondf
```

## ST_Area

Introduction: Return the area of A

Format: `ST_Area (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Area(polygondf.countyshape)
FROM polygondf
```

## ST_Centroid

Introduction: Return the centroid point of A

Format: `ST_Centroid (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Centroid(polygondf.countyshape)
FROM polygondf
```



## ST_Transform

Introduction:

Transform the Spatial Reference System / Coordinate Reference System of A, from SourceCRS to TargetCRS

!!!note
	By default, ==ST_Transform== assumes Longitude/Latitude is your coordinate X/Y. If this is not the case, set UseLongitudeLatitudeOrder as "false".

!!!note
	If ==ST_Transform== throws an Exception called "Bursa wolf parameters required", you need to disable the error notification in ST_Transform. You can append a boolean value at the end.

Format: `ST_Transform (A:geometry, SourceCRS:string, TargetCRS:string, [Optional] UseLongitudeLatitudeOrder:Boolean, [Optional] DisableError)`

Since: `v1.0.0`

Spark SQL example (simple):
```SQL
SELECT ST_Transform(polygondf.countyshape, 'epsg:4326','epsg:3857') 
FROM polygondf
```

Spark SQL example (with optional parameters):
```SQL
SELECT ST_Transform(polygondf.countyshape, 'epsg:4326','epsg:3857',true, false)
FROM polygondf
```

!!!note
	The detailed EPSG information can be searched on [EPSG.io](https://epsg.io/).

## ST_Intersection

Introduction: Return the intersection geometry of A and B

Format: `ST_Intersection (A:geometry, B:geometry)`

Since: `v1.1.0`

Spark SQL example:

```SQL
SELECT ST_Intersection(polygondf.countyshape, polygondf.countyshape)
FROM polygondf
```

## ST_IsValid

Introduction: Test if a geometry is well formed

Format: `ST_IsValid (A:geometry)`

Since: `v1.2.0`

Spark SQL example:

```SQL
SELECT ST_IsValid(polygondf.countyshape)
FROM polygondf
```

## ST_MakeValid

Introduction: Given an invalid polygon or multipolygon and removeHoles boolean flag,
 create a valid representation of the geometry.

Format: `ST_MakeValid (A:geometry, removeHoles:Boolean)`

Since: `v1.2.0`

Spark SQL example:

```SQL
SELECT geometryValid.polygon
FROM table
LATERAL VIEW ST_MakeValid(polygon, false) geometryValid AS polygon
```

!!!note
    Might return multiple polygons from a only one invalid polygon
    That's the reason why we need to use the LATERAL VIEW expression
    
!!!note
    Throws an exception if the geometry isn't polygon or multipolygon

## ST_PrecisionReduce

Introduction: Reduce the decimals places in the coordinates of the geometry to the given number of decimal places. The last decimal place will be rounded.

Format: `ST_PrecisionReduce (A:geometry, B:int)`

Since: `v1.2.0`

Spark SQL example:

```SQL
SELECT ST_PrecisionReduce(polygondf.countyshape, 9)
FROM polygondf
```
The new coordinates will only have 9 decimal places.

## ST_IsSimple

Introduction: Test if geometry's only self-intersections are at boundary points.

Format: `ST_IsSimple (A:geometry)`

Since: `v1.2.0`

Spark SQL example:

```SQL
SELECT ST_IsSimple(polygondf.countyshape)
FROM polygondf
```

## ST_Buffer

Introduction: Returns a geometry/geography that represents all points whose distance from this Geometry/geography is less than or equal to distance.

Format: `ST_Buffer (A:geometry, buffer: Double)`

Since: `v1.2.0`

Spark SQL example:
```SQL
SELECT ST_Buffer(polygondf.countyshape, 1)
FROM polygondf
```

## ST_AsText

Introduction: Return the Well-Known Text string representation of a geometry

Format: `ST_AsText (A:geometry)`

Since: `v1.2.0`

Spark SQL example:
```SQL
SELECT ST_AsText(polygondf.countyshape)
FROM polygondf
```

## ST_NPoints

Introduction: Return points of the geometry

Since: `v1.2.1`

Format: `ST_NPoints (A:geometry)`

```SQL
SELECT ST_NPoints(polygondf.countyshape)
FROM polygondf
```

## ST_SimplifyPreserveTopology

Introduction: Simplifies a geometry and ensures that the result is a valid geometry having the same dimension and number of components as the input,
              and with the components having the same topological relationship.

Since: `v1.2.1`

Format: `ST_SimplifyPreserveTopology (A:geometry, distanceTolerance: Double)`

```SQL
SELECT ST_SimplifyPreserveTopology(polygondf.countyshape, 10.0)
FROM polygondf
```

## ST_GeometryType

Introduction: Returns the type of the geometry as a string. EG: 'ST_Linestring', 'ST_Polygon' etc.

Format: `ST_GeometryType (A:geometry)`

Since: `v1.2.1`

Spark SQL example:
```SQL
SELECT ST_GeometryType(polygondf.countyshape)
FROM polygondf
````
