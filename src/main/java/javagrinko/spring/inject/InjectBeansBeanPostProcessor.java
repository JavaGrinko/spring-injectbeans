package javagrinko.spring.inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class InjectBeansBeanPostProcessor implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware{
    protected final Log logger = LogFactory.getLog(getClass());
    private int order = Ordered.LOWEST_PRECEDENCE - 2;
    private BeanFactory beanFactory;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        injectIntoFields(bean, beanName);
        return bean;
    }

    private void injectIntoFields(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectBeans.class)){
                int needBeansCount = getBeansCount(field);
                Class<?> type = field.getType();
                Object injectionValue;
                boolean isWithoutGenerics = ResolvableType.forField(field).resolveGenerics().length == 0;
                if (type == List.class) {
                    injectionValue = new ArrayList();
                    List injectionList = (List) injectionValue;
                    Class<?> genericType = ResolvableType.forField(field).resolveGeneric();
                    for (int i = 0; i < needBeansCount; i++) {
                        Object dependsBean = getBeanByClass(genericType);
                        injectionList.add(dependsBean);
                    }
                } else if (isWithoutGenerics){
                    if (needBeansCount > 1) {
                        throw new BeanCreationException("Can't inject > 1 beans into non collection field");
                    }
                    injectionValue = getBeanByClass(type);
                } else {
                    throw new BeanCreationException("Unsupported field type with InjectBeans annotation");
                }

                ReflectionUtils.makeAccessible(field);
                try {
                    field.set(bean, injectionValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object getBeanByClass(Class<?> type) {
        Object bean = beanFactory.getBean(type);
        if (bean == null){
            throw new BeanCreationException("No beans with type " + type.getName());
        }
        return bean;
    }

    private int getBeansCount(Field field) {
        InjectBeans annotation = field.getAnnotation(InjectBeans.class);
        return annotation.count();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
