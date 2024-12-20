package ch.hearc.ig.orderresto.business;

import ch.hearc.ig.orderresto.persistence.converters.GenderConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("P")
@Entity
public class PrivateCustomer extends Customer {

    @Convert(converter = GenderConverter.class)
    @Column(name = "EST_UNE_FEMME")
    private String gender;

    @Column(name = "PRENOM")
    private String firstName;

    @Column(name = "NOM", nullable = false)
    private String lastName;

    public PrivateCustomer() {}

    public PrivateCustomer(Long id, String phone, String email, Address address, String gender, String firstName, String lastName) {
        super(id, phone, email, address);
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}