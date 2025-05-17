// MeasurementResponse.java
package erdalguda.main.dto;

import erdalguda.main.model.Measurement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementResponse {
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

    public MeasurementResponse(Measurement m) {
        if (m == null) return;
        this.id = m.getId();
        this.chest = m.getChest();
        this.waist = m.getWaist();
        this.hip = m.getHip();
        this.shoulder = m.getShoulder();
        this.neck = m.getNeck();
        this.leftArm = m.getLeftArm();
        this.rightArm = m.getRightArm();
        this.leftThigh = m.getLeftThigh();
        this.rightThigh = m.getRightThigh();
        this.leftCalf = m.getLeftCalf();
        this.rightCalf = m.getRightCalf();
        this.elbowLength = m.getElbowLength();
    }
}