package cn.ycq.api.admin;

import cn.ycc.api.admin.commons.jxls.command.GroupRowCommand;
import cn.ycc.api.admin.commons.jxls.util.CustomTools;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.expression.ExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JxlsExportTest {

    @Test
    public void testExport() throws IOException {
        String userDir = System.getProperty("user.dir");
        String templateDir = userDir+"/config/temp";

        XlsCommentAreaBuilder.addCommandMapping("groupRow", GroupRowCommand.class);

        JxlsHelper instance = JxlsHelper.getInstance();

        Context context = new Context();

        HashMap<String, Object> sheet1 = new HashMap<>();
        sheet1.put("sheetName","sheetA");
        sheet1.put("test","111111111111111");
        sheet1.put("list",new ArrayList<Object>(){{add("1");add(1);add(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));}});
        sheet1.put("date",new Date());


        HashMap<String, Object> sheet2 = new HashMap<>();
        sheet2.put("sheetName","sheetB");
        sheet2.put("test","2222222222222");
        sheet2.put("list",new ArrayList<Object>(){{add("2");add(2);}});
        sheet2.put("date",new Date());

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","test01");
        jsonObject.put("array",Arrays.asList(1,2,3));
        jsonArray.add(jsonObject);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("name","test02");
        jsonArray.add(jsonObject2);

        sheet1.put("jsons",jsonArray);
        sheet2.put("jsons",jsonArray);

        context.putVar("sheetDatas",Arrays.asList(sheet1,sheet2));

        context.putVar("utils",
                new CustomTools());

        FileInputStream fileInputStream = new FileInputStream(templateDir+"/export.xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream("c:/Users/chaoq/Desktop/export111111.xlsx");

        Transformer transformer = instance.createTransformer(fileInputStream, fileOutputStream);
        instance.processTemplate(context,transformer);

    }
}
