package org.jasoft.tools.war.patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;

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
        Map<String, ZipArchiveEntry> srcFiles=loadFiles(getSrcZip());
        Map<String, ZipArchiveEntry> targetFiles=loadFiles(getTargetZip());
        
        Map<String, ZipArchiveEntry> newFiles=new HashMap<String, ZipArchiveEntry>();
        
        Iterator<String> targetKeys=targetFiles.keySet().iterator();
        while (targetKeys.hasNext()) {
            String targetFilename=targetKeys.next();
            if (!srcFiles.containsKey(targetFilename)
                    || targetFiles.get(targetFilename).getCrc()!=srcFiles.get(targetFilename).getCrc()) {
                newFiles.put(targetFilename, targetFiles.get(targetFilename));
            }
            srcFiles.remove(targetFilename);
        }
        /**
         * Al teminar en srcFiles quedan los archivos eliminados
         * (Aquellos que estánn en el origen pero no en el destino)
         */
        
//        System.out.println("-- Modificados y cambiados:");
//        for (ZipArchiveEntry zae : newFiles.values()) {
//            System.out.println(zae.getName());
//        }
//        System.out.println("\n-- Eliminados:");
//        for (ZipArchiveEntry zae : srcFiles.values()) {
//            System.out.println(zae.getName());
//        }
        
        String tmpFolderPath=targetZip+".diff";
        File tmpFolder=new File(tmpFolderPath);
        
        if (!tmpFolder.mkdir()) {
            throw new RuntimeException("Error al crear la carpeta: "+tmpFolderPath);
        }
        
        TreeSet<String> filesToUnzip=new TreeSet<String>(newFiles.keySet());
        ZipUtil.extract(targetZip, tmpFolderPath, filesToUnzip);
        
        try {
            ZipUtil.createZip(tmpFolderPath, tmpFolderPath+".zip", true);
            FileUtils.deleteDirectory(tmpFolder);
        } catch (IOException ioe) {
            log.log(Level.SEVERE, tmpFolderPath, ioe);
        }
        
        Properties info=new Properties();
        info.put("deletedFiles", serializeZipArchiveEntryNames(srcFiles.values()));
        
        String infoFilePathName=targetZip+".diff.info";
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(infoFilePathName);
            info.store(fos, "Parche de sisprod2");
        } catch (IOException ioe) {
            log.log(Level.SEVERE, infoFilePathName, ioe);
        } finally {
            if (fos!=null) try {
                fos.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Map<String, ZipArchiveEntry> loadFiles(String filepath) {
        Map<String, ZipArchiveEntry> result=new HashMap<String, ZipArchiveEntry>();
        
        try {
            ZipFile zf=new ZipFile(filepath);
            Enumeration<ZipArchiveEntry> e=zf.getEntries();
            while (e.hasMoreElements()) {
                ZipArchiveEntry zae=e.nextElement();
                result.put(zae.getName(), zae);
            }
            zf.close();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "", ioe);
        }
        
        return result;
    }
    
    private String serializeZipArchiveEntryNames(Iterable<ZipArchiveEntry> entrys) {
        StringBuilder sb=new StringBuilder();
        for (ZipArchiveEntry zae : entrys) {
            sb.append(zae.getName()).append(",");
        }
        if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
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
