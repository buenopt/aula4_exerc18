package aula4.exerc18;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int code;
    private String email, desc, address;
    List<Integer> prodIds;

    public Order(int code, String email, String desc, String address) {
        this.code = code;
        this.email = email;
        this.desc = desc;
        this.address = address;
        prodIds = new ArrayList<Integer>();
    }

    public int getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public String getDesc() {
        return desc;
    }

    public String getAddress() {
        return address;
    }

    public List<Integer> getProdIds() {
        return prodIds;
    }
}
