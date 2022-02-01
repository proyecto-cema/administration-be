package com.cema.administration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "subscription")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;

    @Column(name = "starting_date")
    private Date startingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    private CemaEstablishment cemaEstablishment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_type_id")
    private CemaSubscriptionType cemaSubscriptionType;

    @Override
    public String toString() {
        return "CemaSubscription{" +
                "id=" + id +
                ", startingDate=" + startingDate +
                ", cemaEstablishment=" + cemaEstablishment.getCuig() +
                ", cemaSubscriptionType=" + cemaSubscriptionType.getName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CemaSubscription that = (CemaSubscription) o;
        return Objects.equals(startingDate, that.startingDate) && Objects.equals(cemaEstablishment, that.cemaEstablishment) && Objects.equals(cemaSubscriptionType, that.cemaSubscriptionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingDate, cemaEstablishment, cemaSubscriptionType);
    }
}
