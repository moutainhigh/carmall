package cn.wzvtcsoft.bosdomain.persist;


import cn.wzvtcsoft.bosdomain.ICoreObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by liutim on 2017/11/25.
 */


@MappedSuperclass
@Access(AccessType.FIELD)
public class CoreObject implements ICoreObject, Serializable {

    private String id;

    private static final String PATH = "cn.wzvtcsoft.bosdomain.persist.BosidGenerator";

    @Id
    @GeneratedValue(generator = "bosidgenerator")
    @GenericGenerator(name = "bosidgenerator", strategy = PATH)
    @Column(name = "id", nullable = false, updatable = false, length = 25)
    @Access(AccessType.PROPERTY)
    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    //version 乐观锁 TODO

    @Override
    final public boolean equals(Object obj) {
        if (this.id == null || obj == null || !(obj instanceof ICoreObject)) {
            return false;
        } else {
            return Objects.equals(this.id, ((ICoreObject) obj).getId());
        }
    }

    @Override
    final public int hashCode() {
        return (this.id == null) ? 13 : Objects.hash(this.id);
    }


}
