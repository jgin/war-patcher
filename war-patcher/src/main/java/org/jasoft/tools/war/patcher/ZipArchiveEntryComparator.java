package org.jasoft.tools.war.patcher;

import java.util.Comparator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 *
 * @author jgin
 */
public class ZipArchiveEntryComparator implements Comparator<ZipArchiveEntry>{

    public int compare(ZipArchiveEntry o1, ZipArchiveEntry o2) {
        return (int)(o2.getCrc()-o1.getCrc());
    }
    
}
