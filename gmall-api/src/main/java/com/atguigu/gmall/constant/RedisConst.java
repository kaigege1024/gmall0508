package com.atguigu.gmall.constant;

public class RedisConst {

    public static final String SKU_PREFIX = "sku:";

    public static final String SKU_SUFFIX = ":info";

    public static final String SKULOCK_SUFFIX = ":lock";

    public static final Integer SKULOCK_EXPIRE_PX = 10000;

    public static final String USER_PREFIX = "user:";

    public static final String USER_SUFFIX = ":info";

    public static final Integer USER_TIME_OUT = 1000*60*60*24*7;

}
