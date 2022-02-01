package com.cema.administration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subscription_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaSubscriptionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Long price;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @OneToMany(
            mappedBy = "cemaSubscriptionType",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CemaSubscription> subscriptions;

    public boolean isExpired(){
        if (expirationDate != null){
            LocalDateTime expirationTime = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return expirationTime.isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CemaSubscriptionType that = (CemaSubscriptionType) o;
        return Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(duration, that.duration) && Objects.equals(description, that.description) && Objects.equals(creationDate, that.creationDate) && Objects.equals(expirationDate, that.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, duration, description, creationDate, expirationDate);
    }
}
