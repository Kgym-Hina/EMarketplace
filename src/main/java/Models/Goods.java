package Models;

public class Goods {
    private String id;
    private String name;
    private double price;
    private int number;
    private String brand;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
}