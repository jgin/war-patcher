package org.jasoft.tools.war.patcher;

import java.util.Arrays;

/**
 *
 * @author jgin
 */
public class StringUtil {
    
    public static String serialize(String[] args) {
        String res=Arrays.deepToString(args);
        return res.substring(1, res.length()-1);
    }
}
