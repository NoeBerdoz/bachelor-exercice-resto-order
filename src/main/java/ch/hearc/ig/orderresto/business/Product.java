package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "PRODUIT"
)
public class Product {

    @Id
    @GeneratedValue
    @Column(name = "NUMERO")
    private Long id;

    @Column(name = "NOM", nullable = false)
    private String name;

    @Column(name = "PRIX_UNITAIRE", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

   @ManyToMany(mappedBy = "products")
    private Set<Order> orders;

    @ManyToOne
    @JoinColumn(name = "FK_RESTO", nullable = false)
    private Restaurant restaurant;

    public Product() {}

    public Product(Long id, String name, BigDecimal unitPrice, String description, Restaurant restaurant) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.description = description;
        this.orders = new HashSet<>();
        this.restaurant = restaurant;
        restaurant.registerProduct(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %.2f de chez %s: %s",
                this.getName(),
                this.getUnitPrice(),
                this.getRestaurant().getName(),
                this.getDescription()
        );
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public static class Builder {
        private Long id;
        private String name;
        private BigDecimal unitPrice;
        private String description;
        private Restaurant restaurant;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Product build() {
            return new Product(this.id, this.name, this.unitPrice, this.description, this.restaurant);
        }

    }
}