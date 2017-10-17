package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

@DatabaseTable(tableName = "order")
public class TableOrder {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "orderid")
    private String orderId;

    @DatabaseField(columnName = "ordertype")
    private int orderType;

    @DatabaseField(columnName = "ordervalue")
    private String orderValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
    }
}
