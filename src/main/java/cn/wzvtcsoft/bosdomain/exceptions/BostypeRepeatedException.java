package cn.wzvtcsoft.bosdomain.exceptions;

/**
 * BostypeRepeatedException
 *
 * @author zzk
 * @date 2018/11/06
 */
public class BostypeRepeatedException extends RuntimeException {

    public BostypeRepeatedException(String existClz, String targetClz) {
        super("Bostype is repeated with " + existClz + " and " + targetClz);
    }
}
