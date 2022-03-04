package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.dto.SendMailEntity;
import cn.ycc.api.admin.commons.utils.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("notice")
@RestController
public class NoticeController {

    @Autowired
    private MailHelper mailHelper;

    @PostMapping("/sendMail")
    @LogInfo("发送邮件")
    public ResultBean sendEmail(@RequestBody SendMailEntity sendMailEntity){
        for (String to : sendMailEntity.getTo()) {
            mailHelper.sendSimplMailAsync(sendMailEntity.getFrom(),to,sendMailEntity.getSubject(),sendMailEntity.getContent(),sendMailEntity.getIsHtml());
        }
        return ResultBean.builder().code("200").msg("发送成功").build();
    }
}
