package boot.spring.util.mail;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 11:25
 * @Version 1.0
 */
@Component
public class BeetHelper {

    @Autowired
    private GroupTemplate groupTemplate;

    public String getContent(String templatePath, Map<String, Object> param) {
        StringWriter writer = new StringWriter();
        Template template = groupTemplate.getTemplate(templatePath);
        template.binding(param);
        template.renderTo(writer);
        return writer.toString();
    }

}
