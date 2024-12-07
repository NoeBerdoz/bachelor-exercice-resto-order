package ch.hearc.ig.orderresto.business;

import ch.hearc.ig.orderresto.persistence.helper.BooleanConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "COMMANDE"
)
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_CLIENT", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "FK_RESTO", nullable = false)
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(
            name = "PRODUIT_COMMANDE",
            joinColumns = @JoinColumn(name = "FK_COMMANDE"),
            inverseJoinColumns = @JoinColumn(name = "FK_PRODUIT")
    )
    private Set<Product> products;

    @Convert(converter = BooleanConverter.class)
    private Boolean takeAway;

    // Since Java 8, the new Java Date and Time API is available for dealing with temporal values.
    @Column(name = "QUAND", nullable = false)
    private LocalDateTime when;

    private BigDecimal totalAmount;

    public Order() {}

    public Order(Long id, Customer customer, Restaurant restaurant, Boolean takeAway, LocalDateTime when) {
        this.id = id;
        this.customer = customer;
        this.restaurant = restaurant;
        this.products = new HashSet<>();
        this.takeAway = takeAway;
        this.totalAmount = new BigDecimal(0);
        this.when = when;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Boolean getTakeAway() {
        return takeAway;
    }

    public LocalDateTime getWhen() {
        return when;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setTakeAway(Boolean takeAway) {
        this.takeAway = takeAway;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        this.totalAmount = this.totalAmount.add(product.getUnitPrice());
    }

    public static class Builder {
        private Long id;
        private Customer customer;
        private Restaurant restaurant;
        private Boolean takeAway;
        private LocalDateTime when;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder withRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Builder withTakeAway(Boolean takeAway) {
            this.takeAway = takeAway;
            return this;
        }

        public Builder withWhen(LocalDateTime when) {
            this.when = when;
            return this;
        }

        public Order build() {
            return new Order(this.id, this.customer, this.restaurant, this.takeAway, this.when);
        }
    }

}