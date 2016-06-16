package bz.shan.callcheck;

/**
 * Created by shan on 6/14/16.
 */
public class CallDBSchema {

    public static final class CallTable {
        public static final String NAME = "calls";


        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String NUMBER = "number";
            public static final String DATE = "date";
            public static final String JUNK= "junk";
        }
    }

    public static final class ConfigTable {
        public static final String NAME = "config";

        public static final class Cols {
            public static final String NOT_ID = "not_id";
        }
    }

}
