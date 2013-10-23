package org.jasoft.tools.war.patcher;

/**
 *
 * @author jgin
 */
public class StringUtil {
    
    public static String serialize(Iterable<String> args) {
        StringBuilder sb=new StringBuilder();
        for (String str : args) {
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
