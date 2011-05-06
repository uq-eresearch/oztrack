package org.oztrack.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 6/05/11
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */

//@Table(name = "AcousticDetection",uniqueConstraints = { @UniqueConstraint(columnNames= {"detectiontime","receiverdeployment","animal"})})
@Entity(name="AcousticDetection")
public class AcousticDetection  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acousticdetectionid_seq")
    @SequenceGenerator(name = "acousticdetectionid_seq", sequenceName = "acousticdetectionid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date detectionTime;

    @ManyToOne
    private ReceiverDeployment receiverDeployment;

    @ManyToOne
    private Animal animal;

    @ManyToOne
    private DataFile dataFile;

    private String sensor1Description;
    private Double sensor1Value;
    private String sensor1Units;
    private String sensor2Description;
    private Double sensor2Value;
    private String sensor2Units;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(Date detectionTime) {
        this.detectionTime = detectionTime;
    }

    public ReceiverDeployment getReceiverDeployment() {
        return receiverDeployment;
    }

    public void setReceiverDeployment(ReceiverDeployment receiverDeployment) {
        this.receiverDeployment = receiverDeployment;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public String getSensor1Description() {
        return sensor1Description;
    }

    public void setSensor1Description(String sensor1Description) {
        this.sensor1Description = sensor1Description;
    }

    public Double getSensor1Value() {
        return sensor1Value;
    }

    public void setSensor1Value(Double sensor1Value) {
        this.sensor1Value = sensor1Value;
    }

    public String getSensor1Units() {
        return sensor1Units;
    }

    public void setSensor1Units(String sensor1Units) {
        this.sensor1Units = sensor1Units;
    }

    public String getSensor2Description() {
        return sensor2Description;
    }

    public void setSensor2Description(String sensor2Description) {
        this.sensor2Description = sensor2Description;
    }

    public Double getSensor2Value() {
        return sensor2Value;
    }

    public void setSensor2Value(Double sensor2Value) {
        this.sensor2Value = sensor2Value;
    }

    public String getSensor2Units() {
        return sensor2Units;
    }

    public void setSensor2Units(String sensor2Units) {
        this.sensor2Units = sensor2Units;
    }




}