#Описание
Аннотация @InjectBeans(count = xx) создает коллекцию prototype-бинов одного типа и внедряет ее в поле, где xx - целое число, количество бинов в коллекции.

#Примеры
```
@Component
@Scope("prototype")
public class Foo {
}
```
```
@Component
public class TestBean {
    @InjectBeans(count = 3)
    List<Foo> listInject;

    @InjectBeans
    Foo singleInject;

    @Override
    public String toString() {
        return "TestBean{" +
                "listInject=" + listInject +
                ", singleInject=" + singleInject +
                '}';
    }
}
```
```
@Configuration
@ComponentScan
public class Config {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        TestBean bean = context.getBean(TestBean.class);
        System.out.println(bean);
    }
}
```
В консоль будет выведено следующее:
```
TestBean{
listInject=[javagrinko.spring.inject.Foo@2f490758, 
            javagrinko.spring.inject.Foo@101df177,
            javagrinko.spring.inject.Foo@166fa74d]
singleInject=javagrinko.spring.inject.Foo@40f08448}
```
#Установка
1) Необходимо добавить зависимость в build.gradle
```
repositories {
    maven {
        url  "http://dl.bintray.com/javagrinko/maven"
    }
}
dependencies {
    compile 'javagrinko:spring-injectbeans:0.1'
    ...
}
```
