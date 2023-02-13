//package be.uclouvain.lt.pres.ers.core.persistence.model.evidence;
//
//import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
//import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecordID;
//import jdk.jfr.Name;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.*;
//
//@NamedEntityGraph(name = "adog-entity-graph",
//        attributeNodes = {
//            @NamedAttributeNode(value = "poid"),
//            @NamedAttributeNode(value = "digNum"),
//            @NamedAttributeNode(value = "digVal")
//        }
//)
//
//@Entity
//@Table(name = "ADOG")
//@IdClass(TemporaryRecordID.class)
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//public class ADOGroup {
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY) //check if lazy is overridden by entity graph
//    @JoinColumn(name = "POID", referencedColumnName = "POID")
//    //make this a FK to POID_LOC and not POID
//    // possible ?
//    private POID poid;
//
//    @Id
//    @Column(name = "DIG_NUM") //add length and nullable properties
//    //make foreign key out of this ? necessary or useful ? (probably not)
//    private Integer digNum;
//
//    @Column(name = "DigVal") //add length and nullable properties
//    //should this be the binarized form (decoded) ?
//    // what is the most efficient (and correct way to store this value)
//    // considering whether or not we rehash (should this be a FK ?)
//    //what is the resulting type in postgresql ?
//    private byte[] digVal;
//
//    @ManyToOne // complete stuff here ?
//    @JoinColumn(name = "POID_LOC_ID", referencedColumnName = "POID")
//    private POIDLoc poidLoc;
//
//}
