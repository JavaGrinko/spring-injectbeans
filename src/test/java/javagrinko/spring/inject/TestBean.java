package javagrinko.spring.inject;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestBean {
    @InjectBeans
    List<Foo> listWithInject;

    @InjectBeans
    Foo listWithoutInject;

    @Override
    public String toString() {
        return "TestBean{" +
                "listWithInject=" + listWithInject +
                ", listWithoutInject=" + listWithoutInject +
                '}';
    }
}
