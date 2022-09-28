package cn.vtohru.orm;

public abstract class Condition {
    private boolean and;
    public Condition(boolean and) {
        this.and = and;
    }

    public boolean isAnd() {
        return and;
    }

    public void setAnd(boolean and) {
        this.and = and;
    }
}
