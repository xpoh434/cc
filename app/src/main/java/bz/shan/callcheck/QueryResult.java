package bz.shan.callcheck;

/**
 * Created by shan on 7/5/16.
 */
public class QueryResult {
    public QueryResult(String entity_, boolean junk_) {
        entity = entity_;
        junk = junk_;
    }
    public String entity;
    public boolean junk;

    public String toString() {
        return String.format("%s,%s", entity, junk);
    }
}