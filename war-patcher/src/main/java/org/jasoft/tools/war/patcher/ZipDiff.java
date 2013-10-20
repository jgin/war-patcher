package org.jasoft.tools.war.patcher;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 *
 * @author jgin
 */
public class ZipDiff {

    private String srcZip;
    
    private String targetZip;
    
    private String destinyResultFolder;
    
    static final Logger log=Logger.getLogger(ZipDiff.class.getName());
    
    public void diff() {
//        TreeSet<ZipArchiveEntry> zipArchiveEntrys=new TreeSet<ZipArchiveEntry>(new ZipArchiveEntryComparator());
        Map<Long, ZipArchiveEntry> srcFiles=loadFiles(getSrcZip());
        Map<Long, ZipArchiveEntry> targetFiles=loadFiles(getTargetZip());
        
        Map<Long, ZipArchiveEntry> newFiles=new HashMap<Long, ZipArchiveEntry>();
        
        Iterator<Long> targetKeys=targetFiles.keySet().iterator();
        while (targetKeys.hasNext()) {
            Long targetCrc=targetKeys.next();
            if (!srcFiles.containsKey(targetCrc)) {
                newFiles.put(targetCrc, targetFiles.get(targetCrc));
            }
            srcFiles.remove(targetCrc);
        }
        /**
         * Al teminar en srcFiles quedan los archivos eliminados
         * (Aquellos que estánn en el origen pero no en el destino)
         */
        
        System.out.println("-- Modificados y cambiados:");
        for (ZipArchiveEntry zae : newFiles.values()) {
            System.out.println(zae.getName());
        }
        System.out.println("\n-- Eliminados:");
        for (ZipArchiveEntry zae : srcFiles.values()) {
            System.out.println(zae.getName());
        }
        
    }
    
    private Map<Long, ZipArchiveEntry> loadFiles(String filepath) {
        Map<Long, ZipArchiveEntry> result=new HashMap<Long, ZipArchiveEntry>();
        
        try {
            ZipFile zf=new ZipFile(filepath);
            Enumeration<ZipArchiveEntry> e=zf.getEntries();
            while (e.hasMoreElements()) {
                ZipArchiveEntry zae=e.nextElement();
                result.put(zae.getCrc(), zae);
            }
            zf.close();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "", ioe);
        }
        
        return result;
    }

    /**
     * @return the srcZip
     */
    public String getSrcZip() {
        return srcZip;
    }

    /**
     * @param srcZip the srcZip to set
     */
    public void setSrcZip(String srcZip) {
        this.srcZip = srcZip;
    }

    /**
     * @return the targetZip
     */
    public String getTargetZip() {
        return targetZip;
    }

    /**
     * @param targetZip the targetZip to set
     */
    public void setTargetZip(String targetZip) {
        this.targetZip = targetZip;
    }

    /**
     * @return the destinyResultFolder
     */
    public String getDestinyResultFolder() {
        return destinyResultFolder;
    }

    /**
     * @param destinyResultFolder the destinyResultFolder to set
     */
    public void setDestinyResultFolder(String destinyResultFolder) {
        this.destinyResultFolder = destinyResultFolder;
    }
    
}
