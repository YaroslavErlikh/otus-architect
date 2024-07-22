package erlikh.yaroslav.hw06.generator.adapter

import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import org.joor.Reflect

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class AdapterGeneratorImpl implements AdapterGenerator {

    private static final String NAME_TEMPLATE = 'erlikh.yaroslav.hw06.generator.adapter.%sAdapter'
    private static final String CONTENT_TEMPLATE = '' +
            'package erlikh.yaroslav.hw06.generator.adapter; \n' +
            'import erlikh.yaroslav.hw06.generator.ioc.IoC;\n' +
            'import erlikh.yaroslav.hw4.model.basemodel.BaseModel;\n' +
            'import %2$s;\n' +
            'public class %1$sAdapter implements %1$s {\n' +
            '   private BaseModel model;\n' +
            '   public %1$sAdapter(BaseModel model) {\n' +
            '       this.model = model;\n' +
            '   }\n'
    private static final String GETTER_TEMPLATE = '' +
            '   public %1$s get%2$s() {\n' +
            '       return (%1$s) IoC.resolve(\"%3$s:%4$s.get\", model);\n' +
            '   }\n'
    private static final String SETTER_TEMPLATE = '' +
            '   public void set%2$s(%1$s newValue) {\n' +
            '       IoC.resolve(\"%3$s:%4$s.set\", model, newValue);\n' +
            '   }\n'
    private static final Pattern GETTER_PATTERN = Pattern.compile("get(.*)")
    private static final Pattern SETTER_PATTERN = Pattern.compile("set(.*)")

    private final Map<String, Reflect> adapters = new ConcurrentHashMap<>()

    @Override
    <T> T resolve(Class<T> interfaceType, BaseModel baseModel) {
        try {
            Reflect adapter = adapters.get(interfaceType.getName())
            if (adapter == null) {
                adapter = generate(interfaceType)
            }
            return adapter.create(baseModel).get() // getClass().getConstructor(baseModel.getClass()).newInstance(baseModel)
        } catch (Throwable e) {
            throw new RuntimeException(e)
        }
    }

    private Reflect generate(Class<?> interfaceType) {
        try {
            String name = String.format(NAME_TEMPLATE, interfaceType.getSimpleName())
            String content = String.format(CONTENT_TEMPLATE, interfaceType.getSimpleName(), interfaceType.getName())
            StringBuilder builder = new StringBuilder(content)
            for (Method method : interfaceType.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    Matcher getterMatcher = GETTER_PATTERN.matcher(method.getName())
                    Matcher setterMatcher = SETTER_PATTERN.matcher(method.getName())
                    String methodCode = null
                    if (getterMatcher.matches() && !method.getReturnType().equals(Void.TYPE)) {
                        methodCode = generateGetter(method.getReturnType(), getterMatcher.group(1), interfaceType)
                    } else if (setterMatcher.matches() && method.getParameterCount() > 0) {
                        methodCode = generateSetter(method.getParameters()[0].getType(), setterMatcher.group(1), interfaceType)
                    }
                    if (methodCode != null) {
                        builder.append(methodCode)
                    }
                }
            }
            builder.append("}\n")
            Reflect adapter = Reflect.compile(name, builder.toString())
            adapters.put(interfaceType.getName(), adapter)
            return adapter
        } catch (Throwable e) {
            throw new RuntimeException(e)
        }
    }

    private String generateGetter(Class<?> returnType, String attribute, Class<?> interfaceType) {
        return String.format(GETTER_TEMPLATE, returnType.getName(), attribute, interfaceType.getSimpleName(), attribute.toLowerCase())
    }

    private String generateSetter(Class<?> parameterType, String attribute, Class<?> interfaceType) {
        return String.format(SETTER_TEMPLATE, parameterType.getName(), attribute, interfaceType.getSimpleName(), attribute.toLowerCase())
    }
}