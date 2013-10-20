package org.jasoft.tools.war.patcher;

import java.io.IOException;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
//        String filepath="D:\\dev\\prj\\sisprod2\\app\\src\\master\\sisprod-webapp\\target\\sisprod-webapp-0.7.0.war";
        String srcFile="D:\\LVH\\dev\\prj\\war-patcher\\tmp\\psx.zip";
        String targetFile="D:\\LVH\\dev\\prj\\war-patcher\\tmp\\psx2.zip";
//        listFiles(filepath);
        
        ZipDiff zd=new ZipDiff();
        zd.setSrcZip(srcFile);
        zd.setTargetZip(targetFile);
        
        zd.diff();
        
    }
    
    private static void listFiles(String filepath) throws IOException {
        ZipFile zf=new ZipFile(filepath);
        zf.close();
        Enumeration<ZipArchiveEntry> files=zf.getEntries();
        while (files.hasMoreElements()) {
            ZipArchiveEntry zae=files.nextElement();
            System.out.println(zae.getCrc() + " : " + zae.getName());
        }
    }
}
