package com.chris.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeoUtil {

    private static final GeometryFactory GF = new GeometryFactory();

    public static Point makePoint(Double x, Double y) {
        Point p = GF.createPoint(new Coordinate(x, y));
        p.setSRID(4326);
        return p;
    }

    public static final Point ORIGIN = makePoint(0.0, 0.0);

}
