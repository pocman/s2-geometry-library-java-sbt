# s2-geometry-library-java
This library can be used to create GeoHashes for fast querying. 
The Java version is used by AWS for GeoSpatial queries in DynamoDB.

[What algorithms is Google using in the geocoding and searching?](http://www.quora.com/What-algorithms-is-Google-using-in-the-geocoding-and-searching)

The geocode it uses in a nutshell:
* start with a [cube map](https://en.wikipedia.org/wiki/Cube_mapping)
* create a [quadtree](https://en.wikipedia.org/wiki/Quadtree) with 30 levels on each face
* label both leaves and internal nodes with 64 bits
* use 3 bits for the face index
* use n*2 bits to identify a node at depth n
* add a single 1 bit
* pad with 0 bits as necessary

This code has several nice properties, e.g. the depth of a node can be computed from the index of the least set bit in its code. It's also pretty amazing that when S2 is used to geocode the surface of Earth, the leaf nodes are smaller than 1 cm^2.

Make sure to check the code out though, it has many clever details, such as using the [Hilbert curve](https://en.wikipedia.org/wiki/Hilbert_curve) ordering for labels so that neighboring nodes get close codes.

Areas are represented as sets of nodes, so most operations on areas are simple set operations implemented in a way that eliminates redundancy (ensures that parent nodes absorb their children).

See more [here](https://docs.com.google.com/presentation/d/1Hl4KapfAENAOf4gv-pSngKwvS_jwNVHRPZTTDzXXn6Q/view#slide=id.i0)

===

```Java
    /** S2LatLng we want to match **/
    S2LatLng citadelle_lille = S2LatLng.fromDegrees(50.640944, 3.044538);
    
    /** Return the leaf cell containing the given S2LatLng. */
    S2CellId cellId = S2CellId.fromLatLng(citadelle_lille);
    S2CellId cellIdParent = cellId.parent(25);
    
    /** Creates Regions **/
    S2Cell cell = new S2Cell(citadelle_lille);
    S2Cell cellParent = new S2Cell(cellIdParent);
    
    /** Get data **/
    cell.getRectBound(); //[Lo=(0.8838512089120377, 0.053137211406054784), Hi=(0.8838512103322981, 0.05313721316313653)]
    cell.approxArea(); //1.4091693487441163E-18
    
    cellParent.getRectBound(); //[Lo=(0.8838512027278979, 0.05313718669443818), Hi=(0.8838512481762079, 0.053137242921029955)]
    cellParent.approxArea(); //1.4429895668456382E-15
```

* Niveau 20 : [Lo=(0.8838507148710442, 0.05313564224309559), Hi=(0.8838521692167257, 0.053137441491653885)]
            1.4776207732504918E-12
* Niveau 15 : [Lo=(0.8838292003968686, 0.0530954868291146), Hi=(0.8838757392079908, 0.05315306109676593)]
            1.513066365555524E-9
* Niveau 10 : [Lo=(0.8838042345232255, 0.052410974192230446), Hi=(0.8852946040223499, 0.05425673180718569)]
            1.5514691921513971E-6
* Niveau 5 :  [Lo=(0.8702613730181155, 0.05119745649882483), Hi=(0.9190714186995642, 0.11571999434369849)]
            0.001660047454160006
* Niveau 2 :  [Lo=(0.7454194762741578, -4.440892098500626E-16), Hi=(1.1760052070951357, 0.7853981633974487)]
            0.1241338394849343

===

An **S2RegionCoverer** is a class that allows arbitrary regions to be approximated as unions of cells (S2CellUnion).

An **S2Region** represents a two-dimensional region over the unit sphere. It is an abstract interface with various concrete subtypes :
* An **S2Cap** represents a [spherical cap](http://en.wikipedia.org/wiki/Spherical_cap), i.e. a portion of a sphere cut off by a plane.
* An **S2Cell** is an object that represents a cell.
* An **S2CellUnion** is a region consisting of cells of various sizes. Typically a cell union is used to approximate some other shape.
* An **S2LatLngRect** represents a latitude-longitude rectangle.
* An **S2Loop** represents a simple spherical polygon. It consists of a single chain of vertices
* An **S2Polygon** is an S2Region object that represents a polygon. A polygon consists of zero or more {@link S2Loop loops} representing "shells" and "holes".
* An **S2Polyline** represents a sequence of zero or more vertices connected by straight edges (geodesics).
