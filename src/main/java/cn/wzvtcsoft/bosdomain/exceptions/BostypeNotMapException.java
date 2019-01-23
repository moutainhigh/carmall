package cn.wzvtcsoft.bosdomain.exceptions;

/**
 * BostypeNotMapException
 *
 * @author zzk
 * @date 2018/11/13
 */
public class BostypeNotMapException extends RuntimeException {

    public BostypeNotMapException() {
        super("The given id can't map target class");
    }
}
