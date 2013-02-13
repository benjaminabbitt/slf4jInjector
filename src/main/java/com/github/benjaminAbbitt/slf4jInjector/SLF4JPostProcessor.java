package com.github.benjaminAbbitt.slf4jInjector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import com.github.benjaminAbbitt.logging.injectLogger.InjectLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SLF4JPostProcessor implements BeanPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(SLF4JPostProcessor.class);

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        List<Field> fields = Arrays.asList(bean.getClass().getDeclaredFields());

        for (Field field : fields) {
            if (Logger.class.isAssignableFrom(field.getType()) && field.getAnnotation(InjectLogger.class) != null) {

                logger.debug("Attempting to inject a SLF4J logger on bean: " + bean.getClass());

                if (field != null && (field.getModifiers() & Modifier.STATIC) == 0) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, LoggerFactory.getLogger(bean.getClass()));
                        logger.debug("Successfully injected a SLF4J logger on bean: " + bean.getClass());
                    } catch (IllegalArgumentException e) {
                        logger.warn("Could not inject logger for class: " + bean.getClass(), e);
                    } catch (IllegalAccessException e) {
                        logger.warn("Could not inject logger for class: " + bean.getClass(), e);
                    }
                }
            }
        }

        return bean;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}