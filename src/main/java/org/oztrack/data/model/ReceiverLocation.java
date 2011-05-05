package org.oztrack.data.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiverLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receiverlocatid_seq")
    @SequenceGenerator(name = "receiverlocatid_seq", sequenceName = "receiverlocatid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    private String locationName;
    private String locationDescription;
    private String receiverArrayName;
    private String latitude;
    private String longitude;

}
