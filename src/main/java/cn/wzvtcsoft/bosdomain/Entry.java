package cn.wzvtcsoft.bosdomain;


import cn.wzvtcsoft.bosdomain.persist.CoreObject;
import cn.wzvtcsoft.bosdomain.persist.EntryParentType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by liutim on 2017/11/25.
 */


@MappedSuperclass
@Access(AccessType.FIELD)
@TypeDef(name = "EntryParentType", typeClass = EntryParentType.class)
public abstract class Entry extends CoreObject {


    @Column(name = "parent_id", length = 25)
    @Type(type = "EntryParentType")
    private ICoreObject parent;

    public void setParent(ICoreObject parent) {
        this.parent = parent;
    }
}
