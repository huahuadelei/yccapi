package cn.ycc.api.admin.commons.utils;

import cn.ycc.api.admin.commons.enums.JxlsTempType;
import cn.ycc.api.admin.commons.jxls.command.GroupRowCommand;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.09 13:50
 */
public class JxlsExcelExportHelper {

    static {
        XlsCommentAreaBuilder.addCommandMapping("groupRow", GroupRowCommand.class);
    }

    private File templateDir;


    public JxlsExcelExportHelper(String templateDir) {
        if(ObjectUtils.isEmpty(templateDir)){
            throw new IllegalArgumentException("templateDir is null or empty");
        }
        File file = new File(templateDir);
        if(!file.exists()){
            throw new IllegalArgumentException("templateDir ("+templateDir+") is not exists");
        }
        this.templateDir = file;
    }


    public void exportExcel(JxlsTempType tempType, OutputStream toOutPutStream, Context context)throws IOException {
        File file = new File(templateDir,tempType.getTempName());
        if(!file.exists()){
            throw new IllegalArgumentException("tempFileName from("+file.getPath()+") not exists");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        Transformer transformer = JxlsHelper.getInstance().createTransformer(fileInputStream, toOutPutStream);
        try {
            JxlsHelper.getInstance().processTemplate(context,transformer);
        }finally {
            if(fileInputStream!=null){
                fileInputStream.close();
            }
        }
    }

    public void renderExcelToDownLoad(HttpServletResponse response, String downloadFile, JxlsTempType tempType, Context context,boolean ziped) throws IOException {

        OutputStream outputStream = response.getOutputStream();
        if(ziped){
            outputStream = new ZipOutputStream(outputStream);
            ((ZipOutputStream)outputStream).putNextEntry(new ZipEntry(downloadFile));
            downloadFile = downloadFile.replace(downloadFile.substring(downloadFile.lastIndexOf(".")),".zip");
        }

        downloadFile = URLEncoder.encode(downloadFile,"UTF-8");

        response.addHeader("Content-Disposition", "attachment;filename="
                + downloadFile);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        exportExcel(tempType,outputStream ,context);

        if(ziped){
            ((ZipOutputStream)outputStream).closeEntry();
            ((ZipOutputStream)outputStream).finish();
        }
    }

}
