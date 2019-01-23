package cn.wzvtcsoft.bosdomain.util;

import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import cn.wzvtcsoft.bosdomain.exceptions.BostypeNotExistException;
import cn.wzvtcsoft.bosdomain.exceptions.BostypeRepeatedException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class BostypeUtils {


    /**
     * 缓存所有 Entity 中 id 的后缀，key 为 Class 中的全名， value 为 id 的后缀
     */
    private static final Map<String, String> CLASS_ID_CACHE = new HashMap<>();

    /**
     * 根据k来过滤的断言， TODO 可以挪到stream的工具中去
     *
     * @param keyExtractor 拿到key的function
     * @param <T>
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * 通过 Class 获得对应的 id 后缀
     */
    public static String getIdByClass(Class<?> entityClz) {
        if (BostypeUtils.CLASS_ID_CACHE.containsKey(entityClz.getCanonicalName())) {
            return BostypeUtils.CLASS_ID_CACHE.get(entityClz.getCanonicalName());
        }

        String value;
        if (entityClz.isAnnotationPresent(Bostype.class)) {
            Bostype bostype = entityClz.getAnnotation(Bostype.class);
            value = bostype.value();
        } else if (entityClz.getSimpleName().length() >= 3) {
            value = entityClz.getSimpleName().substring(0, 3);

        } else {
            throw new BostypeNotExistException("实体: " + entityClz.getSimpleName() + "没有 @Bostype注解");
        }
        predicateRepeated(entityClz, value);
        BostypeUtils.CLASS_ID_CACHE.put(entityClz.getCanonicalName(), value);
        return value;
    }

    /**
     * 判断生成的 id 的后缀是否重复，若重复则抛出异常
     */
    private static void predicateRepeated(Class<?> entityClz, String value) {
        Optional<Map.Entry<String, String>> any = CLASS_ID_CACHE.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .findAny();
        if (any.isPresent()) {
            throw new BostypeRepeatedException(any.get().getKey(), entityClz.getCanonicalName());
        }
    }


    /**
     * 通过 id 来寻找其 Class类型,准确的来说是通过 id 的后缀。
     * <p>
     * 若为 null 则表明该 id 为非法id
     */
    public static Class<?> findById(String id) {
        if (id.length() < 3) {
            throw new RuntimeException("id : " + id + "\t 不符合规范");
        }
        String value = id.substring(id.length() - 3);
        Optional<Map.Entry<String, String>> any = CLASS_ID_CACHE.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .findAny();
        if (any.isPresent()) {
            try {
                return Class.forName(any.get().getKey());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 主要是考虑id是否需要在url、
     * json字符串中、
     * sql字符串中、
     * javascript字符串中、
     * 其他语言字符串中
     * 是否需要进行转义编码。
     * 因此去掉了一些特殊字符如=/$/?/%/+/&//空格/+等。最终采用26+26+10+2（-、_)的字符集。
     *
     * 而且考虑到最后一个字符必须为数字，好与后面的bostype分隔开，所以采用的是0，1，2，3
     * @return
     */
    //TODO 用缓存优化，先生成好100个，到40个的时候再去加到100个，
    public static String getMiniuuid(String uid) {
        String id = null;
        int i=0;
        do {
            i++;
            id = getInternalMiniuuid(uid);
        }
        while (id.contains("-") || new HashSet(Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})).contains(id.substring(0, 1)));
        //System.out.println(i+":"+id);

        return id;
    }

    private static String getInternalMiniuuid(String uid) {
        if((uid==null || "".equals(uid.trim())) || (uid.trim().length()!=32)){
            uid = UUID.randomUUID().toString().replaceAll("-", "");
        }

        //System.out.println(uid);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int a = Integer.valueOf(uid.substring(3 * i, 3 * i + 1), 16)
                    .intValue();
            int b = Integer.valueOf(uid.substring(3 * i + 1, 3 * i + 2), 16)
                    .intValue();
            int c = Integer.valueOf(uid.substring(3 * i + 2, 3 * i + 3), 16)
                    .intValue();

            int m = ((a << 2) & 0x3c) + ((b >> 2) & 0x03);
            int n = ((b << 4) & 0x30) + (c & 0x0f);
            sb.append(getchar(m));
            sb.append(getchar(n));
        }
        int a = Integer.valueOf(uid.substring(30, 31), 16).intValue();
        int b = Integer.valueOf(uid.substring(31, 32), 16).intValue();

        int m = ((a << 2) & 0x3c) + ((b >> 2) & 0x03);

        sb.append(getchar(m));
        int n = b & 0x03;

        sb.append(getlastchar(n));

        // 将ID编码成更友好的类似于变量标识符的ID，包括首字不能为数字。将字符串中的java转意
        return sb.toString();
    }

    private static char getlastchar(int n) {
        if(n==0){
            return '0';//35
        }else if(n==1){
            return '1';//(char)36;
        }else if(n==2){
            return '2';//(char)37;
        }else if(n==3){
            return '3';//(char)38;
        } else {
            throw new RuntimeException("hhhh!!!");
        }
    }

    private static char getchar(int x) {
        int charint = 0;
        if ((x >= 0) && (x <= 9)) {//'0'-'9'
            charint = 48 + x;
        } else if (x == 10) {
            charint = 95;//'_'
        } else if (x == 11) {
            charint = 45;//'-'
        } else if ((x >= 12) && (x <= 37)) {
            charint = ((x - 12) + 65);//'A'-'Z'
        } else if (x >= 38 && x <= 63) {
            charint = ((x - 38) + 97);//'a'-'z'
        } else {
            throw new RuntimeException("hhhh!!!");
        }
        return (char) charint;
    }
}
