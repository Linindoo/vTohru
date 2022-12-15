package cn.vtohru.orm;

public class OrderCondition {
    private String order;
    private boolean reverse;

    public OrderCondition(String order, boolean reverse) {
        this.order = order;
        this.reverse = reverse;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
