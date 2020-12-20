package gameClient;

import api.geo_location;

/**
 * A class that represents a location/position on a graph implemented by the given geo_location api.
 * Contains a distance function.
 */
class GeoLocation implements geo_location {
    double x, y, z, distance; //coordinates on a graph

    //Constructors
    public GeoLocation() {
        this(0,0,0);
        this.distance = 0;
    }

    public GeoLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GeoLocation(GeoLocation p) {
        this(p.x(), p.y(), p.z());
    }
    //-----------

    /**
     * Returns the X of the location
     * @return double
     */
    @Override
    public double x() {
        return this.x;
    }

    /**
     * Returns the Y of the location
     * @return double
     */
    @Override
    public double y() {
        return this.y;
    }

    /**
     * Returns the Z of the location
     * @return double
     */
    @Override
    public double z() {
        return this.z;
    }

    /**
     * Returns a distance from a location to another
     * @param g
     * @return double
     */
    @Override
    public double distance(geo_location g) {
        double x = this.x() - g.x();
        double y = this.y() - g.y();
        double z = this.z() - g.z();
        double t = (x * x + y * y + z * z);
        return Math.sqrt(t);
    }

    @Override
    public String toString(){
        return String.format("%f, %f, %f",this.x,this.y,this.z);
    }

}
