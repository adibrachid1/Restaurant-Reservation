package fr.eurecom.restaurantv3;

public class Reservation_class {
    String date;
    String deposit;
    String table_places;
    String table_x;
    String table_y;
    String time;
    String resto_id;
    String resto_image;
    String resto_name;

    public Reservation_class(String date, String deposit, String table_places, String table_x, String table_y, String time, String resto_id, String resto_image, String resto_name){
        this.date = date;
        this.deposit = deposit;
        this.table_places = table_places;
        this.table_x = table_x;
        this.table_y = table_y;
        this.time = time;
        this.resto_id = resto_id;
        this.resto_image = resto_image;
        this.resto_name = resto_name;
    }
}
