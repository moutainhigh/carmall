package cn.wzvtcsoft.validator.core;

import cn.wzvtcsoft.validator.exceptions.StringLogicException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MutationValidation 的解析器
 */
public class RuleParser {

    /**
     * 属性获取的正则表达式
     */
    private static final String PROPERTY_REGEX = "[a-z][a-zA-Z0-9_$]*";


    /**
     * 根据校验规则缓存属性名
     */
    private static final Map<String, List<String>> rulePropertyCache = new HashMap<>();


    /**
     * spEL 解析器，用来解析 String 并执行String语法，这里用来解析并执行 String 的逻辑判断
     */
    public static final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 解析 校验规则中的属性或参数
     */
    public static List<String> getProperties(String rule) {

        if (rulePropertyCache.containsKey(rule)) {
            return rulePropertyCache.get(rule);
        }


        Pattern pattern = Pattern.compile(PROPERTY_REGEX);
        Matcher matcher = pattern.matcher(rule);

        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        checkStringLogic(rule, list);
        rulePropertyCache.put(rule, list);

        return list;
    }


    private static void checkStringLogic(String rule, List<String> properties) {
        if (!"".equals(rule)) {
            for (String property : properties) {
                rule = rule.replace(property, "true");
            }
            try {
                parser.parseRaw(rule).getValue(boolean.class);
            } catch (Exception ex) {
                throw new StringLogicException("字符串逻辑异常 : " + rule);
            }
        }

    }


    /**
     * 对校验规则中的属性或参数 进行逻辑判断
     *
     * @param rule       校验规则
     * @param properties 要校验的属性
     * @param booleans   目标属性的boolean 值
     * @return 返回逻辑判断的结果
     */
    public static boolean executeStringValid(String rule, List<String> properties, List<Boolean> booleans) {
        String result = rule;
        for (int i = 0; i < properties.size(); i++) {
            result = result.replace(properties.get(i), booleans.get(i).toString());
        }

        return RuleParser.parser.parseExpression(result).getValue(boolean.class);
    }


}
