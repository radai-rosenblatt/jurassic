package net.radai.bob.codegen;

import net.radai.bob.model.Namespace;
import net.radai.bob.model.xdr.*;
import net.radai.bob.runtime.model.XdrEnum;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Radai Rosenblatt
 */
public class OncRpcGen {
    /**
     * @param namespace an oncrpc namespace to generate code for
     * @return a map of relative paths to file contents under each path
     */
    public Map<Path, String> generate(Namespace namespace) {

        List<JavaType> generatedTypes = new ArrayList<>();

        Map<String, XdrConstant> constants = namespace.getConstants();
        if (constants != null && !constants.isEmpty()) {
            JavaInterfaceSource constsClass = generateConstantsClass(constants, namespace.getName() + "Consts");
            generatedTypes.add(constsClass);
        }

        Map<String, XdrDeclaration> types = namespace.getTypes();
        if (types != null && !types.isEmpty()) {
            generatedTypes.addAll(generateTypeClasses(types));
        }

        return render(generatedTypes);
    }

    private JavaInterfaceSource generateConstantsClass(Map<String, XdrConstant> constants, String fqcn) {
        List<Map.Entry<String, XdrConstant>> sortedConstants = new ArrayList<>(constants.entrySet());
        Collections.sort(sortedConstants, (e1, e2) -> e1.getKey().compareTo(e2.getKey())); //by name

        JavaInterfaceSource constsClass = Roaster.create(JavaInterfaceSource.class);
        constsClass.setName(fqcn);
        for (Map.Entry<String, XdrConstant> constantEntry : sortedConstants) {
            String constantName = constantEntry.getKey();
            XdrConstant constant = constantEntry.getValue();
            FieldSource<JavaInterfaceSource> field = constsClass.addField().setName(constantName);
            if (constant.fitsInt()) {
                field.setType(int.class).setLiteralInitializer(constant.asInt() + "");
            } else if (constant.fitsLong()) {
                field.setType(long.class).setLiteralInitializer(constant.asLong() + "L");
            } else {
                field.setType(BigInteger.class).setLiteralInitializer("new BigInteger(\"" + constant.asBigInteger().toString() + "\")");
            }
        }

        return constsClass;
    }

    private List<JavaType> generateTypeClasses(Map<String, XdrDeclaration> types) {

        List<JavaType> results = new ArrayList<>(types.size());

        for (Map.Entry<String, XdrDeclaration> typeEntry : types.entrySet()) {
            String typeName = typeEntry.getKey();
            XdrDeclaration declaration = typeEntry.getValue();
            XdrType type = declaration.getType();
            switch (type.getType()) {
                case ENUM:
                    results.add(generateEnumClass(declaration));
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported declaration of type " + type.getType() + ": " + type);
            }
        }

        return results;
    }

    private JavaEnumSource generateEnumClass(XdrDeclaration declaration) {
        XdrEnumType enumType = (XdrEnumType) declaration.getType();

        JavaEnumSource enumClass = Roaster.create(JavaEnumSource.class);
        enumClass.setName(declaration.getIdentifier());
        enumClass.implementInterface(XdrEnum.class);
        enumClass.addField().setName("value").setFinal(true);
        enumClass.addMethod().setConstructor(true).setPrivate()
                .setParameters("int value")
                .setBody("this.value = value;");
        enumClass.getMethod("value").setBody("return value;"); //method exists because of the interface

        for (Map.Entry<String, XdrValue> entry : enumType.getValues().entrySet()) {
            String identifier = entry.getKey();
            BigInteger instanceValue = resolve(entry.getValue());
            int intValue;
            try {
                intValue = instanceValue.intValueExact();
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("instance " + identifier + " of enum " + declaration.getIdentifier()
                        + " has value " + instanceValue + " which does not fit a signed integer");
            }
            enumClass.addEnumConstant(identifier).setConstructorArguments(intValue + "");
        }

        return enumClass;
    }

    private BigInteger resolve(XdrValue value) {
        if (value instanceof XdrConstantValue) {
            return ((XdrConstantValue) value).getValue();
        }
        if (value instanceof XdrRefValue) {
            throw new UnsupportedOperationException("resolving ref values not supported yet");
        }
        throw new IllegalStateException("unhandled value type " + value);
    }

    private Map<Path, String> render(List<JavaType> generatedTypes) {
        Map<Path, String> javaFiles = new HashMap<>();

        for (JavaType generatedType : generatedTypes) {
            String fqcn = generatedType.getCanonicalName();
            String[] pathBits = fqcn.split("\\.");
            String first = pathBits[0];
            if (pathBits.length > 1) {
                pathBits = Arrays.copyOfRange(pathBits, 1, pathBits.length); //[1,len]
            } else {
                pathBits = null;
            }
            Path relativePath = pathBits != null ? Paths.get(first, pathBits) : Paths.get(first);
            String sourceCode = Roaster.format(generatedType.toUnformattedString());
            javaFiles.put(relativePath, sourceCode);
        }

        return javaFiles;
    }
}
