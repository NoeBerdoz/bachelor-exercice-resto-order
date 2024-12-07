package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "RESTAURANT"
)
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name="NOM", nullable = false)
    private String name;

    @OneToMany(mappedBy="restaurant")
    private Set<Order> orders;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "restaurant")
    private Set<Product> productsCatalog;

    public Restaurant () {}

    public Restaurant(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.orders = new HashSet<>();
        this.productsCatalog = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Address getAddress() {
        return address;
    }

    public Set<Product> getProductsCatalog() {
        return productsCatalog;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setProductsCatalog(Set<Product> productsCatalog) {
        this.productsCatalog = productsCatalog;
    }

    public void registerProduct(Product p) {
        if (p.getRestaurant() != this) {
            throw new RuntimeException("Restaurant mismatch!");
        }
        this.productsCatalog.add(p);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public static class Builder {
        private Long id;
        private String name;
        private Address address;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this.id, this.name, this.address);
        }
    }

}