package com.cema.administration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "establishment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaEstablishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @Column(name = "cuig")
    private String cuig;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "owner_username")
    private String ownerUserName;
    @Column(name = "creation_date")
    private Date creationDate;

    @OneToMany(
            mappedBy = "cemaEstablishment",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CemaSubscription> subscriptions;

    @Override
    public String toString() {
        return "CemaEstablishment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", cuig='" + cuig + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", ownerUserName='" + ownerUserName + '\'' +
                ", creationDate=" + creationDate +
                ", subscriptions=" + subscriptions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CemaEstablishment that = (CemaEstablishment) o;
        return Objects.equals(name, that.name) && Objects.equals(location, that.location) && Objects.equals(cuig, that.cuig) && Objects.equals(phone, that.phone) && Objects.equals(email, that.email) && Objects.equals(ownerUserName, that.ownerUserName) && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, cuig, phone, email, ownerUserName, creationDate);
    }
}
