package cn.wzvtcsoft.bosdomain.enums;

import cn.wzvtcsoft.bosdomain.util.EntityEnumUtil;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

public abstract class BosEnumConverter<T extends BosEnum> implements AttributeConverter<T, String> {
    private Class<T> type;

    public BosEnumConverter() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }


    @Override
    public String convertToDatabaseColumn(T attribute) {
        return EntityEnumUtil.getValueByEnum(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            return (T) EntityEnumUtil.getEnumByString(dbData, type).get();
        }
    }
}


