package fr.eurecom.restaurantv3;
import android.widget.ImageView;

public class Restaurant {
    public String id;
    public String name;
    public String rating;
    public String opening_time;
    public String closing_time;
    public String type;
    public String price;
    public String logo;
    public String menu;
    public String indoor;
    public String outdoor;
    String parking;
    public Restaurant(String name){
        this.name = name;
    }
    public Restaurant(String id, String name, String rating, String opening_time, String closing_time, String type, String price, String logo){
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.type = type;
        this.price = price;
        this.logo = logo;
    }
    public Restaurant(String id, String name, String rating, String opening_time, String closing_time, String type, String price, String logo, String menu){
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.type = type;
        this.price = price;
        this.logo = logo;
        this.menu = menu;
    }
    public Restaurant(String id, String name, String rating, String opening_time, String closing_time, String type, String price, String logo, String menu, String parking,  String outdoor, String indoor){
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.type = type;
        this.price = price;
        this.logo = logo;
        this.menu = menu;
        this.outdoor = outdoor;
        this.parking = parking;
        this.indoor = indoor;
    }
}
