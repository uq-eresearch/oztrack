package org.oztrack.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 1:53 PM
 */
@Entity(name = "ReceiverDeployment")
public class ReceiverDeployment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receiverdeployid_seq")
    @SequenceGenerator(name = "receiverdeployid_seq", sequenceName = "receiverdeployid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    private String originalId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deploymentDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date retrievalDate;

    @ManyToOne
    private Project project;

    private String receiverName;
    private String receiverDescription;

    @ManyToOne
    private ReceiverLocation receiverLocation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public Date getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Date deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public Date getRetrievalDate() {
        return retrievalDate;
    }

    public void setRetrievalDate(Date retrievalDate) {
        this.retrievalDate = retrievalDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverDescription() {
        return receiverDescription;
    }

    public void setReceiverDescription(String receiverDescription) {
        this.receiverDescription = receiverDescription;
    }

    public ReceiverLocation getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(ReceiverLocation receiverLocation) {
        this.receiverLocation = receiverLocation;
    }

}
