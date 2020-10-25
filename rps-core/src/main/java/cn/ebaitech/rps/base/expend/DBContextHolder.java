package cn.ebaitech.rps.base.expend;

public class DBContextHolder {
    public static ThreadLocal<DBType> local = new ThreadLocal();

    public enum DBType {
        master, slave;
    }

    public static DBType getDBType() {
        DBType dbType = local.get();
        if (dbType == null) {
            return DBType.master;
        }
        return dbType;
    }

    public static void setDBType(DBType dbType) {
        if (dbType == null) {
            local.set(DBType.master);
        } else {
            local.set(dbType);
        }
    }
}
