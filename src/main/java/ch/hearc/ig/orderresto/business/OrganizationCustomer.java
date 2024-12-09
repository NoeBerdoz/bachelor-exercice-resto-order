package ch.hearc.ig.orderresto.business;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("O")
@Entity
public class OrganizationCustomer extends Customer {

    @Column(name = "NOM", nullable = false)
    private String name;

    @Column(name = "FORME_SOCIAL", nullable = false)
    private String legalForm;

    public OrganizationCustomer() {}

    public OrganizationCustomer(Long id, String phone, String email, Address address, String name, String legalForm) {
        super(id, phone, email, address);
        this.name = name;
        this.legalForm = legalForm;
    }

    public String getName() {
        return name;
    }

    public String getLegalForm() {
        return legalForm;
    }
}