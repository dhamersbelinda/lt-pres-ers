package be.uclouvain.lt.pres.ers.core.persistence.model.evidence;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecordID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Table(name = "POID_LOC")
@IdClass(TemporaryRecordID.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class POIDLoc {
    @Id
    @OneToOne //add stuff here ?
    //see how to make a PK a FK (if other annotations are necessary) -> somewhere in my browser there was a link
    @JoinColumn(name = "POID", referencedColumnName = "POID")
    private POID poid;

    //do not forget to add fk to TS Nnode/or TSID in SQL schema (forgotten before)
    //need to change the string type to whatever will represent the TS
    //how to represent a TS as a Java class ?
    //FK to representing table
    private String TSID;

    //FK to hashnode table
    //only a single node, the node will contain the already concatenated value
    //how to express that it is 1to1 but optional on the side of HashNode
    private HashNode hashNode;

    @OneToOne //add stuff here, can be nullable because some POIDs are comprised of only a single value
    private ADOGroup adoGroup;



}
