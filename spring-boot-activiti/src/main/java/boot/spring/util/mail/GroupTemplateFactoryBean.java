package boot.spring.util.mail;

import org.beetl.core.GroupTemplate;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 17:49
 * @Version 1.0
 */
public class GroupTemplateFactoryBean implements FactoryBean<GroupTemplate> {
    @Override
    public GroupTemplate getObject() throws Exception {
        return new GroupTemplate();
    }

    @Override
    public Class<?> getObjectType() {
        return GroupTemplate.class;
    }


    //true 单例，false 多例
    @Override
    public boolean isSingleton() {
        return true;
    }
}
