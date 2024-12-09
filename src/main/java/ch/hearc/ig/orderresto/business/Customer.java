package ch.hearc.ig.orderresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "TYPE",
        discriminatorType = DiscriminatorType.CHAR
)
@DiscriminatorValue("C")
@Entity
@Table(
        name = "CLIENT"
)
public abstract class Customer {

    @Id
    @GeneratedValue
    @Column(name = "NUMERO")
    private Long id;

    @Column(name = "TELEPHONE", nullable = false)
    private String phone;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @OneToMany(mappedBy="customer")
    private Set<Order> orders;

    @Embedded
    private Address address;

    public Customer() {}

    protected Customer(Long id, String phone, String email, Address address) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.orders = new HashSet<>();
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Address getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}