package com.chris.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeoUtil {

    private static final int EARTH_RADIUS = 6_371_000; // in meters

    private static final GeometryFactory GF = new GeometryFactory();

    public static Point makePoint(Double x, Double y) {
        Point p = GF.createPoint(new Coordinate(x, y));
        p.setSRID(4326);
        return p;
    }

    public static Double distance(Double lng, Double lat, Point point) {
        // radians
        double radLat1 = Math.toRadians(lat);
        double radLat2 = Math.toRadians(point.getY());
        double deltaLat = Math.toRadians(point.getY() - lat);
        double deltaLng = Math.toRadians(point.getX() - lng);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public static final Point ORIGIN = makePoint(0.0, 0.0);

}
