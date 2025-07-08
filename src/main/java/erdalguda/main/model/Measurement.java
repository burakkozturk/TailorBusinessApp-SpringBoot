package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "measurements")
@Getter
@Setter
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double chest;
    private Double waist;
    private Double hip;
    private Double shoulder;
    private Double neck;
    private Double leftArm;
    private Double rightArm;
    private Double leftThigh;
    private Double rightThigh;
    private Double leftCalf;
    private Double rightCalf;
    private Double elbowLength;

    @OneToOne
    private Customer customer;

    // Manual getter/setter methods (Lombok not working)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Double getChest() { return chest; }
    public void setChest(Double chest) { this.chest = chest; }
    
    public Double getWaist() { return waist; }
    public void setWaist(Double waist) { this.waist = waist; }
    
    public Double getHip() { return hip; }
    public void setHip(Double hip) { this.hip = hip; }
    
    public Double getShoulder() { return shoulder; }
    public void setShoulder(Double shoulder) { this.shoulder = shoulder; }
    
    public Double getNeck() { return neck; }
    public void setNeck(Double neck) { this.neck = neck; }
    
    public Double getLeftArm() { return leftArm; }
    public void setLeftArm(Double leftArm) { this.leftArm = leftArm; }
    
    public Double getRightArm() { return rightArm; }
    public void setRightArm(Double rightArm) { this.rightArm = rightArm; }
    
    public Double getLeftThigh() { return leftThigh; }
    public void setLeftThigh(Double leftThigh) { this.leftThigh = leftThigh; }
    
    public Double getRightThigh() { return rightThigh; }
    public void setRightThigh(Double rightThigh) { this.rightThigh = rightThigh; }
    
    public Double getLeftCalf() { return leftCalf; }
    public void setLeftCalf(Double leftCalf) { this.leftCalf = leftCalf; }
    
    public Double getRightCalf() { return rightCalf; }
    public void setRightCalf(Double rightCalf) { this.rightCalf = rightCalf; }
    
    public Double getElbowLength() { return elbowLength; }
    public void setElbowLength(Double elbowLength) { this.elbowLength = elbowLength; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
