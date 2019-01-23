package cn.wzvtcsoft.bosdomain.enums;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 针对 EntityEnum 的注解处理工具(APT),生成为 hibernate 适配的泛型枚举
 *
 * @author zzk
 * @date 2018/10/17
 */
@AutoService(Processor.class)
public class EntityEnumProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String path = EntityEnum.class.getCanonicalName();
        TypeElement targetAnn = elementUtils.getTypeElement(path);
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(targetAnn)) {
            if (annotatedElement.getKind() == ElementKind.ENUM) {
                TypeElement typeElement = (TypeElement) annotatedElement;
                generateCode(elementUtils, filer, typeElement);
            }
        }
        return true;
    }

    private void generateCode(Elements elementUtils, Filer filer, TypeElement annotatedClassElement) {
        String targetClassName = annotatedClassElement.getSimpleName().toString();
        String createClassName = targetClassName + "Converter";

        PackageElement pkg = elementUtils.getPackageOf(annotatedClassElement);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();


        TypeName variableName = TypeVariableName.get(targetClassName);
        ClassName bosEnumConverter = ClassName.get(BosEnumConverter.class);
        ClassName annName = ClassName.get(Converter.class);


        AnnotationSpec annotationSpec = AnnotationSpec.builder(annName)
                .addMember("autoApply", "true")
                .build();

        TypeSpec typeSpec = TypeSpec
                .classBuilder(createClassName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(bosEnumConverter, variableName))
                .addAnnotation(annotationSpec)
                .build();
        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>(1);
        set.add(EntityEnum.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
