package br.com.kimae.fixturecreator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.kimae.fixturecreator.in.Xpto;

public class Creator {

    private static final Class<?> clazz = Xpto.class;
    private static final String className = clazz.getSimpleName();
    private static final String fixtureClassName = clazz.getSimpleName() + "Fixture";
    private static final Map<Class, String> randomStrategy = Maps.builder(HashMap<Class, String>::new)
        .put(LocalDate.class, "LocalDate.new()")
        .put(String.class, "randomAlphanumeric(20)")
        .put(Integer.class, "nextInt()")
        .put(Long.class, "nextLong()")
        .put(BigDecimal.class, "new BigDecimal(nextDouble())")
        .put(Boolean.class, "nextBoolean()")
        .unmodifiable(true)
        .build();
    private static final String DEFAULT_RANDOM_STRING = "{{CLASS_NAME}}Fixture.get().random().build()";

    public static void main(String[] args) {

        print("public class %s {", fixtureClassName);

        print("\tprivate %sBuilder builder = %s.builder();", className, className);

        print("\tpublic %s build() { return builder.build(); }", className);

        print("\tpublic static %s get() { return new %s(); }", fixtureClassName, fixtureClassName);

        Arrays.stream(clazz.getDeclaredFields())
            .forEach(Creator::buildBuilderMethods);

        print("\tpublic %s random() {", fixtureClassName);

        print("\t\treturn");

        Arrays.stream(clazz.getDeclaredFields())
            .forEach(Creator::buildRandom);
        print("\t\t;");
        print("\t}");

        print("}");
    }

    private static void print(String text, String... params) {
        System.out.println(String.format(text, params));
    }

    private static void buildBuilderMethods(final Field field) {
        String fieldType = field.getType().getName();
        String fieldName = field.getName();
        print("\tpublic %s %s(final %s %s) {", fixtureClassName, fieldName, fieldType, fieldName);
        print("\t\tbuilder.%s(%s);", fieldName, fieldName);
        print("\t\treturn this;");
        print("\t}");
    }

    private static void buildRandom(final Field field) {
        Class fieldType = field.getType();
        String fieldName = field.getName();
        String fieldClassName = fieldType.getSimpleName();
        String randomString = randomStrategy
            .getOrDefault(fieldType, DEFAULT_RANDOM_STRING.replace("{{CLASS_NAME}}", fieldClassName));
        print("\t\t\t%s(%s).", fieldName, randomString);
    }
}
