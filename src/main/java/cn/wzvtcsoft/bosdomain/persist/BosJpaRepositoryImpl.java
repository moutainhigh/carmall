package cn.wzvtcsoft.bosdomain.persist;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.Entry;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

@NoRepositoryBean
public class BosJpaRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements Serializable {

    public BosJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public BosJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> S save(S entity) {
        if (entity instanceof BosEntity) {
            resetEntriesSeqAndParent((BosEntity) entity);
        }

        return super.save(entity);
    }

    private void resetEntriesSeqAndParent(BosEntity entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            boolean isEntry = field.isAnnotationPresent(OneToMany.class) &&
                    Entry.class.isAssignableFrom((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
            if (isEntry) {
                try {
                    String methodName = "get" + getMethodName(field.getName());
                    Method method = entity.getClass().getMethod(methodName);
                    Collection<Entry> entries = (Collection<Entry>) method.invoke(entity);

                    if (entries != null && entries.size() > 0) {
                        entries.forEach(it -> it.setParent(entity));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /* 暂时保留该代码
        List<Field> fieldList = new ArrayList();
        Class clz = coreObject.getClass();
        while (!clz.equals(Object.class)) {
            Field[] fields = clz.getDeclaredFields();
            if (fields != null) {
                fieldList.addAll(Arrays.asList(fields));
            }
            clz = clz.getSuperclass();
        }

        fieldList.stream()
                .filter(field ->
                        (field.getAnnotation(OneToMany.class) != null)
                                && Set.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    if (Entry.class.isAssignableFrom((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])) {
                        field.setAccessible(true);
                        Set<Entry> set;
                        try {
                            set = (Set<Entry>) field.get(coreObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new RuntimeException("CoreObject.resetEntriesSeqAndParent");
                        }
                        set.stream()
                                .forEach(entry -> {
                                    try {
                                        Class clazz = entry.getClass();
                                        while (!Entry.class.equals(clazz)) {
                                            clazz = clazz.getSuperclass();
                                        }
                                        Field parentField = clazz.getDeclaredField("parent");
                                        parentField.setAccessible(true);
                                        parentField.set(entry, coreObject);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot set parent value");
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot find parent field");
                                    }
                                    resetEntriesSeqAndParent(entry);
                                });
                    }
                });
         */
    }

    private static String getMethodName(String fildeName) {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);

    }

}

