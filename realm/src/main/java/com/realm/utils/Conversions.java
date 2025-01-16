package com.realm.utils;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Conversions {

    public static SimpleDateFormat sdfUserDisplayDate = new SimpleDateFormat("dd-MMM-yyyy");//=null;
    public static SimpleDateFormat sdfUserDisplayTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm");//=null;
    public static SimpleDateFormat sdfUserDisplayTimeOnly = new SimpleDateFormat("HH:mm");//=null;
    public static SimpleDateFormat sdf_user_friendly_date = new SimpleDateFormat("dd-MM-yyyy");//=null;
    public static SimpleDateFormat sdf_db_date = new SimpleDateFormat("yyyy-MM-dd");//=null;
    public static SimpleDateFormat sdf_db_date_unseparated = new SimpleDateFormat("yyyyMMdd");//=null;
    public static SimpleDateFormat sdf_user_friendly_time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//=null;
    public static SimpleDateFormat sdf_db_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//=null;

    public static String sqlTableFilterFromUserFriendlyDate(String from, String to) {
        from = from.replace("DD-MM-YYYY", "");
        to = to.replace("DD-MM-YYYY", "");
        from = from == null || from.length() < 2 ? "01-01-2011" : from;
        to = to == null || to.length() < 2 ? sdf_user_friendly_date.format(Calendar.getInstance().getTime()) + "" : to;

        try {
            from = from.replace(" ", "");
            from = from + " 00:00:00";
            from = sdf_user_friendly_date.parse(from).getTime() + "";
            from = ((int) (Math.floor(Double.parseDouble(from) / 1000))) + "";
        } catch (Exception ex) {
        }
        try {
            to = to.replace(" ", "");
            to = to + " 23:59:00";
            to = sdf_user_friendly_time.parse(to).getTime() + "";
            to = ((int) (Math.floor(Double.parseDouble(to) / 1000))) + "";

        } catch (Exception ex) {
        }
        return ("CAST( strftime('%s', reg_time) as INTEGER)<" + to + " AND CAST( strftime('%s', reg_time) as INTEGER)>" + from);

    }

    public static String getUserDisplayDateFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayDateFromUserTime(String db_date) {


        try {
            Date time1 = sdf_user_friendly_time.parse(db_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayTime.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeOnlyFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayTimeOnly.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeOnlyFromUserTime(String db_date) {


        try {
            Date time1 = sdf_user_friendly_time.parse(db_date);

            return sdfUserDisplayTimeOnly.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayDateFromUserDate(String user_date) {


        try {
            Date time1 = sdf_user_friendly_date.parse(user_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return user_date;
        }

    }


    public static String getUserDisplayTime() {


        try {
            return sdfUserDisplayTime.format(Calendar.getInstance().getTime());
        } catch (Exception ex) {
            return Calendar.getInstance().getTime() + "";
        }

    }

    public static String getUserDisplayDistanceMeter(String distanceInMeters) {
        Double meters = Double.parseDouble(distanceInMeters);
        if (meters > 1000) {
            return Math.round(meters / 1000) + " km";

        } else {
            return Math.round(meters) + " m";

        }

    }

    public static String getUserDisplayDistanceMeterSQ(String distanceInMetersSQ) {
        try {
            Double meters = Double.parseDouble(distanceInMetersSQ);
            if (meters > 1000000) {
                return Math.round(meters / 1000000) + " km²";

            } else {
                return Math.round(meters) + " m²";

            }
        } catch (Exception ex) {
        }
        return distanceInMetersSQ;
    }

    public static double computeDistanceBetween(LatLng from, LatLng to) {
        return computeAngleBetween(from, to) * 6371009.0;
    }

    public static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(Math.toRadians(from.latitude), Math.toRadians(from.longitude), Math.toRadians(to.latitude), Math.toRadians(to.longitude));
    }

    public static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    public static double arcHav(double x) {
        return 2.0 * Math.asin(Math.sqrt(x));
    }

    public static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2);
    }

    public static double hav(double x) {
        double sinHalf = Math.sin(x * 0.5);
        return sinHalf * sinHalf;
    }
}
