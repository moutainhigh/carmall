package cn.wzvtcsoft.bosdomain.annotations;


import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 实体 Id 的指定后缀。
 * <p>
 * 为了能够通过 实体Id 得知实体的类型，则为每个实体的id 生成时添加后缀。
 * 默认的后缀为 实体名称的前三位字母，若存在实体的前三位字母相等的情况则会抛出异常，则需要该注解指定其后缀加以区分
 *
 * @author zzk
 * @date 2018/11/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public @interface Bostype {

    String value();


}
