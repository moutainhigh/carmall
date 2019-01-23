package cn.wzvtcsoft.bosdomain.util;

import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import cn.wzvtcsoft.bosdomain.exceptions.BostypeNotExistException;
import cn.wzvtcsoft.bosdomain.exceptions.BostypeRepeatedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
}
