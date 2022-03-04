package cn.ycc.api.admin.commons.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SendMailEntity implements Serializable {

    @NotBlank
    private String subject;
    @NotBlank
    @Email
    private String[] to;
    private String from;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isHtml;

}
