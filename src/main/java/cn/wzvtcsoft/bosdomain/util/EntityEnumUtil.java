package cn.wzvtcsoft.bosdomain.util;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * EntityEnumUtil
 *
 * @author zzk
 * @date 2018/10/18
 */
public class EntityEnumUtil {


    public static Map<String, String> entityEnumMap(Class<? extends BosEnum> clz) {
        try {
            Object[] enumConstants = clz.getEnumConstants();
            Map<String, String> entityEnums = new HashMap<>(enumConstants.length);
            for (Object enumConstant : enumConstants) {
                Field field = clz.getDeclaredField(enumConstant.toString());

                if (field.isAnnotationPresent(EntityEnum.class)) {
                    EntityEnum entityEnum = field.getAnnotation(EntityEnum.class);
                    entityEnums.put(entityEnum.value(), entityEnum.alias());
                } else {
                    entityEnums.put(enumConstant.toString(), enumConstant.toString());
                }
            }
            return entityEnums;
        } catch (Exception e) {
            throw new RuntimeException("指定的类不是枚举类型");
        }
    }


    public static Optional<BosEnum> getEnumByString(String string, Class<? extends BosEnum> clz) {
        BosEnum[] enumConstants = clz.getEnumConstants();
        return Arrays.stream(enumConstants)
                .filter(bosEnum -> bosEnum.toString().equals(string))
                .findAny();
    }


    public static String getValueByEnum(@Nullable BosEnum bosEnum) {
        try {
            if (bosEnum == null) {
                return null;
            }
            Field field = bosEnum.getClass().getDeclaredField(bosEnum.toString());
            EntityEnum annotation = field.getAnnotation(EntityEnum.class);
            return annotation.value();
        } catch (Exception e) {
            throw new RuntimeException("指定的类不是枚举类型");
        }

    }

    public static String getAliasByEnum(BosEnum bosEnum) {
        try {
            Field field = bosEnum.getClass().getDeclaredField(bosEnum.toString());
            EntityEnum annotation = field.getAnnotation(EntityEnum.class);
            return annotation.alias();
        } catch (Exception e) {
            throw new RuntimeException("指定的类不是枚举类型");
        }

    }
}
