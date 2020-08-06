package edu.illinois.cs.cs125.spring2019.mp0.lib;

import java.util.Random;

/**
 * A class that runs implements several helper functions on location data.
 *
 * You are expected to understand and complete the functions as declared and described below.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/0/">MP0 Documentation</a>
 */
public class Locator {

    /** Maximum valid latitude. */
    public static final double MAX_LATITUDE = 90.0;

    /** Minimum valid latitude. */
    public static final double MIN_LATITUDE = -90.0;

    /** Maximum valid longitude. */
    public static final double MAX_LONGITUDE = 180.0;

    /** Minimum valid longitude. */
    public static final double MIN_LONGITUDE = -180.0;

    /** Random number generator for use by this class. */
    private static Random random = new Random();

    /**
     * Determine which of a set of positions is the furthest north.
     * <p>
     * The passed arrays contain latitude and longitude measurements, with each index storing one of a pair of
     * measurements, along with an array of booleans indicating which measurements are valid. So, for example,
     * latitude[0] and longitudes[0] comprise one location measurement. If validLocations[0] is true the location is
     * valid, otherwise it is not.
     * <p>
     * Note that you must return the <i>index</i> of the valid measurement that is furthest north, not the value
     * itself. If multiple valid locations are equally far north you should return the index of the <i>first</i> one in
     * the arrays. If the array contains no valid locations you should return -1.
     * <p>
     * This method is used by the MP0 location app to place a special pin on the map in the location that is the
     * furthest north. Will you make it all the way to a visit with Santa?
     *
     * @param latitudes array of previous latitude measurements
     * @param longitudes array of previous longitude measurements
     * @param validLocations the array containing whether the location at an index is valid or not
     * @return the index in the array containing the position that is furthest north.
     */
    public static int farthestNorth(final double[] latitudes, final double[] longitudes,
                                    final boolean[] validLocations) {
        int index =   -1;
        double maximum =    MIN_LATITUDE;
        for (int i  =   0; i    <   latitudes.length;  i++) {
            if (validLocations[i] && latitudes[i] > maximum) {
                maximum = latitudes[i];
                index = i;
            }
        }
        return index;
    }





    /**
     * Determine whether you've already been in this exact spot.
     * <p>
     * This function is passed the index of the current location along with arrays of latitude and longitude values,
     * and an array that indicates whether the location at an index is valid or not. (When the app starts up the
     * location arrays are initially filled with invalid measurements.)
     *
     * @param currentIndex the index of the current location to compare against
     * @param latitudes the array containing previous latitude measurements
     * @param longitudes the array containing previous longitude measurements
     * @param validLocations the array containing whether the location at an index is valid or not
     * @return true if the array contains another valid location equal to the current one
     */


    public static boolean beenHere(final int currentIndex,
                            final double[] latitudes, final double[] longitudes,
                            final boolean[] validLocations) {

        for (int i = 0; i < longitudes.length; i++) {
            if (latitudes[i] == latitudes[currentIndex] && longitudes[i] == longitudes[currentIndex]) {
                if (i   !=  currentIndex)   {
                    if  (validLocations[i]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }




    /**
     * Generate a new random location based on the current position and a transition probability.
     * <p>
     * This function is passed the latitude and longitude of the current position and a transition probability. With
     * probability equal to the passed value you should modify the latitude and longitude by the passed parameters.
     * In other cases you should not modify the latitude and longitude.
     * <p>
     * You should return an array of doubles of size 2 with the first index equal to the returned latitude and the
     * second equal to the returned longitude.
     * <p>
     * Also note that you should ensure that the values that you return are valid using the constants defined above.
     * If a change would cause a location value to become invalid, you should correct it so that it is valid.
     * For example, a latitude of 89.0 cannot be increased by 2.0, since 91.0 is not a valid latitude. In that case
     * you should return 90.0, the result bounded to within the valid range.
     *
     * @param currentLatitude the current latitude to possibly modify
     * @param currentLongitude the current longitude to possibly modify
     * @param transitionProbability the probability that the latitude and longitude will change
     * @param latitudeChange the amount to change the latitude if it changes
     * @param longitudeChange the amount to change the longitude if it changes
     * @return an array of two doubles with the first value equal the returned latitude and the second value equal to
     * the returned longitude
     */
    public static double[] nextRandomLocation(final double currentLatitude, final double currentLongitude,
                                       final double transitionProbability,
                                       final double latitudeChange, final double longitudeChange) {
        double reLatitude;
        double reLongitude;
        double r    =   random.nextDouble();
        double reLa =   currentLatitude + latitudeChange;
        double reLo =   currentLongitude + longitudeChange;
        if (r > transitionProbability) {
            reLatitude = currentLatitude;
        }   else {
            if (reLa >= MAX_LATITUDE) {
                reLatitude = MAX_LATITUDE;
            } else if (reLa <= MIN_LATITUDE) {
                reLatitude = MIN_LATITUDE;
            } else {
                reLatitude = reLa;
            }
        }
        if (r   > transitionProbability) {
            reLongitude = currentLongitude;
        }   else    {
            if (reLo >= MAX_LONGITUDE) {
                reLongitude = MAX_LONGITUDE;
            } else if (reLo <=  MIN_LONGITUDE) {
                reLongitude = MIN_LONGITUDE;
            } else {
                reLongitude = reLo;
            }
        }



        return new double[] {reLatitude, reLongitude};
    }
}
