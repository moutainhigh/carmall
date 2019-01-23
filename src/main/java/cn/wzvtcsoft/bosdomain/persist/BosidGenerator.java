package cn.wzvtcsoft.bosdomain.persist;

import cn.wzvtcsoft.bosdomain.ICoreObject;
import cn.wzvtcsoft.bosdomain.util.BostypeUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class BosidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        if (object instanceof ICoreObject) {
            String value = BostypeUtils.getIdByClass(object.getClass());
            return getBostypeid(value);
        } else {
            throw new RuntimeException("Can't generate id and persistent for those NOT ICoreObject!!!");
        }
    }


    /**
     * 获取到的id只可能是[a-zA-Z0-9]另外还有-这63个字符。
     */
    private static String getBostypeid(String idAppend) {
        String miniuuid = getMiniuuid(null);
        return miniuuid + idAppend;
    }

    /**
     * 主要是考虑id是否需要在url、
     * json字符串中、
     * sql字符串中、
     * javascript字符串中、
     * 其他语言字符串中
     * 是否需要进行转义编码。
     * 因此去掉了一些特殊字符如=/$/?/%/+/&//空格/+等。最终采用26+26+10+2（-、_)的字符集。
     * <p>
     * 而且考虑到最后一个字符必须为数字，好与后面的bostype分隔开，所以采用的是0，1，2，3
     *
     * @return
     */
    //TODO 用缓存优化，先生成好100个，到40个的时候再去加到100个，
    private static String getMiniuuid(String uid) {
        String id;
        int i = 0;
        do {
            i++;
            id = getInternalMiniuuid(uid);
        }
        while (id.contains("-") || Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                .contains(id.substring(0, 1)));
        return id;
    }


    private static String getInternalMiniuuid(String uid) {
        if ((uid == null || "".equals(uid.trim())) || (uid.trim().length() != 32)) {
            uid = UUID.randomUUID().toString().replaceAll("-", "");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int a = Integer.valueOf(uid.substring(3 * i, 3 * i + 1), 16);
            int b = Integer.valueOf(uid.substring(3 * i + 1, 3 * i + 2), 16);
            int c = Integer.valueOf(uid.substring(3 * i + 2, 3 * i + 3), 16);

            int m = ((a << 2) & 0x3c) + ((b >> 2) & 0x03);
            int n = ((b << 4) & 0x30) + (c & 0x0f);
            sb.append(getchar(m));
            sb.append(getchar(n));
        }
        int a = Integer.valueOf(uid.substring(30, 31), 16);
        int b = Integer.valueOf(uid.substring(31, 32), 16);

        int m = ((a << 2) & 0x3c) + ((b >> 2) & 0x03);

        sb.append(getchar(m));
        int n = b & 0x03;

        sb.append(getlastchar(n));

        // 将ID编码成更友好的类似于变量标识符的ID，包括首字不能为数字。将字符串中的java转意
        return sb.toString();


    }

    private static char getlastchar(int n) {
        //35
        if (n == 0) {
            return '0';
        } else if (n == 1) {
            //(char)36;
            return '1';
        } else if (n == 2) {
            //(char)37;
            return '2';
        } else if (n == 3) {
            //(char)38;
            return '3';
        } else {
            throw new RuntimeException("hhhh!!!");
        }
    }

    private static char getchar(int x) {
        int charint = 0;
        //'0'-'9'
        if ((x >= 0) && (x <= 9)) {
            charint = 48 + x;
        } else if (x == 10) {
            //'_'
            charint = 95;
        } else if (x == 11) {
            //'-'
            charint = 45;
        } else if ((x >= 12) && (x <= 37)) {
            //'A'-'Z'
            charint = ((x - 12) + 65);
        } else if (x >= 38 && x <= 63) {
            //'a'-'z'
            charint = ((x - 38) + 97);
        } else {
            throw new RuntimeException("hhhh!!!");
        }
        return (char) charint;
    }
}
