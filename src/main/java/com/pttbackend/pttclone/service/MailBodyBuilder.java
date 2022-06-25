package com.pttbackend.pttclone.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;


/**
 * @see <a href="https://github.com/thymeleaf/thymeleaf/blob/3.0-master/src/main/java/org/thymeleaf/TemplateEngine.java">
 *      templateEngine </a>  
 */
@Service
@AllArgsConstructor // for TemplateEngine
public class MailBodyBuilder {
    private final TemplateEngine templateEngine;
    
    /** 
     * @param content HTML made
     * @return a String containing the result 
     *         of evaluating the specified template 
     *         with the provided context.
     */
    public String BuilderHTMLContent(String content){
            Context context = new Context();
            context.setVariable("content", content);
            return templateEngine.process("mailTemplate", context);
    }
}
