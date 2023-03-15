package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Client {
    @Id
    @Column(name = "CLIENT_ID")
    @Setter(value = AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
}
