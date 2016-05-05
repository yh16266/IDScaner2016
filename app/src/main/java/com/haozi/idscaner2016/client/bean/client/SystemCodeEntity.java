package com.haozi.idscaner2016.client.bean.client;

        import com.haozi.idscaner2016.client.bean.BaseEntity;

/**
 * Created by Haozi on 2016/5/5.
 */
public class SystemCodeEntity extends BaseEntity{

    private String type;
    private String code;
    private String name;
    private int order;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
