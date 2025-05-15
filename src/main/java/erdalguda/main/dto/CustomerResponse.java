package erdalguda.main.dto;


import erdalguda.main.model.Customer;
import erdalguda.main.model.Measurement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private Double height;
    private Double weight;
    private Measurement measurement;

    public CustomerResponse(Customer c) {
        this.id = c.getId();
        this.firstName = c.getFirstName();
        this.lastName = c.getLastName();
        this.address = c.getAddress();
        this.phone = c.getPhone();
        this.height = c.getHeight();
        this.weight = c.getWeight();
        this.measurement = c.getMeasurement();
    }
}
