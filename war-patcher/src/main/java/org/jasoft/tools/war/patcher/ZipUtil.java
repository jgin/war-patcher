package org.jasoft.tools.war.patcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

/**
 * Utility to Zip and Unzip nested directories recursively.
 *
 * @author Robin Spark
 * @author jgin
 */
public class ZipUtil {

    static final Logger log=Logger.getLogger(ZipUtil.class.getName());
    
    /**
     * Creates a zip file at the specified path with the contents of the
     * specified directory. NB:
     *
     * @param directoryPath The path of the directory where the archive will be
     * created. eg. c:/temp
     * @param zipPath The full path of the archive to create. eg.
     * c:/temp/archive.zip
     * @throws IOException If anything goes wrong
     */
    public static void createZip(String directoryPath, String zipPath) throws IOException {
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        ZipArchiveOutputStream tOut = null;

        try {
            fOut = new FileOutputStream(new File(zipPath));
            bOut = new BufferedOutputStream(fOut);
            tOut = new ZipArchiveOutputStream(bOut);
            addFileToZip(tOut, directoryPath, "");
        } finally {
            tOut.finish();
            tOut.close();
            bOut.close();
            fOut.close();
        }

    }

    /**
     * Creates a zip entry for the path specified with a name built from the
     * base passed in and the file/directory name. If the path is a directory, a
     * recursive call is made such that the full directory is added to the zip.
     *
     * @param zOut The zip file's output stream
     * @param path The filesystem path of the file/directory being added
     * @param base The base prefix to for the name of the zip file entry
     *
     * @throws IOException If anything goes wrong
     */
    private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String base) throws IOException {
        File f = new File(path);
        String entryName = base + f.getName();
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);

        zOut.putArchiveEntry(zipEntry);

        if (f.isFile()) {
            FileInputStream fInputStream = null;
            try {
                fInputStream = new FileInputStream(f);
                IOUtils.copy(fInputStream, zOut);
                zOut.closeArchiveEntry();
            } finally {
                IOUtils.closeQuietly(fInputStream);
            }

        } else {
            zOut.closeArchiveEntry();
            File[] children = f.listFiles();

            if (children != null) {
                for (File child : children) {
                    addFileToZip(zOut, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }
    
    public static void extract(String srcZip, String targetFolder) {
        extract(srcZip, targetFolder, null);
    }
    
    public static void extract(String srcZip, String targetFolder, Set<String> filesToUnzip) {
        ZipFile zipFile = null;
        
        try {
            zipFile=new ZipFile(srcZip);
            byte[] buf = new byte[65536];

            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zae=entries.nextElement();
                
                if (filesToUnzip!=null) {
                    if (!filesToUnzip.contains(zae.getName())) continue;
                }
                
                File targetFile=new File(targetFolder, zae.getName());
                if (zae.isDirectory()) {
                    if (!targetFile.mkdirs())
                        log("no se pudo crear la carpeta "+targetFile.getAbsolutePath());
                } else {
//                    System.out.println(targetFile.createNewFile());
                    if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs())
                        log("no se pudo crear la carpeta "+targetFile.getAbsolutePath());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(targetFile);
                        int n;
                        InputStream entryContent = zipFile.getInputStream(zae);
                        while ((n = entryContent.read(buf)) != -1) {
                            if (n > 0) {
                                fos.write(buf, 0, n);
                            }
                        }
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                }
                
            }
        } catch (IOException ioe) {
            log.log(Level.SEVERE, srcZip, ioe);
        }
    }

    private static void log(String msg) {
        log.log(Level.WARNING, msg);
    }

}
