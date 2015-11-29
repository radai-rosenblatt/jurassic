package net.radai.bob.codegen;

import net.radai.bob.model.Identifiable;
import net.radai.bob.model.Namespace;
import net.radai.bob.model.Scope;
import net.radai.bob.model.xdr.*;
import net.radai.bob.runtime.model.XdrEnum;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Radai Rosenblatt
 */
public class OncRpcGen {
    private Namespace namespace;
    private List<JavaType> generatedTypes = new ArrayList<>();
    private IdentityHashMap<XdrConstant, FieldSource> resolvedConstants = new IdentityHashMap<>();
    private IdentityHashMap<XdrDeclaration, JavaType> resolvedTypes = new IdentityHashMap<>();
    private JavaInterfaceSource constsClass;

    /**
     * @return a map of relative paths to file contents under each path
     */
    public Map<Path, String> generate(Namespace namespace) {

        reset(namespace);

        Map<String, XdrConstant> constants = namespace.getConstants();
        if (constants != null && !constants.isEmpty()) {
            constsClass = generateConstantsClass(constants, namespace.getName() + "Consts");
            generatedTypes.add(constsClass);
        }

        Map<String, XdrDeclaration> types = namespace.getTypes();
        if (types != null && !types.isEmpty()) {
            generatedTypes.addAll(generateTypeClasses(types, namespace));
        }

        return render(generatedTypes);
    }

    private void reset(Namespace namespace) {
        this.generatedTypes.clear();
        this.resolvedConstants.clear();
        this.constsClass = null;
        this.namespace = namespace;
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
            resolvedConstants.put(constant, field);
        }

        return constsClass;
    }

    private List<JavaType> generateTypeClasses(Map<String, XdrDeclaration> types, Scope scope) {

        List<JavaType> results = new ArrayList<>(types.size());

        for (Map.Entry<String, XdrDeclaration> typeEntry : types.entrySet()) {
            XdrDeclaration declaration = typeEntry.getValue();
            XdrType type = declaration.getType();
            switch (type.getType()) {
                case ENUM:
                    results.add(generateEnumClass(declaration, scope));
                    break;
                case STRUCT:
                    results.add(generateStructClass(declaration, scope));
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported declaration of type " + type.getType() + ": " + type);
            }
        }

        return results;
    }

    private JavaEnumSource generateEnumClass(XdrDeclaration declaration, Scope scope) {
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
            XdrValue value = entry.getValue();
            if (value instanceof XdrConstantValue) {
                BigInteger instanceValue = ((XdrConstantValue)value).getValue();
                int intValue;
                try {
                    intValue = instanceValue.intValueExact();
                } catch (ArithmeticException e) {
                    throw new IllegalArgumentException("instance " + identifier + " of enum " + declaration.getIdentifier()
                            + " has value " + instanceValue + " which does not fit a signed integer");
                }
                enumClass.addEnumConstant(identifier).setConstructorArguments(intValue + "");
            } else if (value instanceof XdrRefValue) {
                String refName = ((XdrRefValue) value).getRefName();
                XdrConstant resolvedTo = resolveConstant(refName, scope);
                FieldSource constField = resolvedConstants.get(resolvedTo);
                if (constField == null) {
                    throw new IllegalStateException("unknown constant " + resolvedTo);
                }
                assert constsClass == constField.getOrigin();
                if (!constsClass.isDefaultPackage()) {
                    enumClass.addImport(constsClass);
                }
                enumClass.addEnumConstant(identifier).setConstructorArguments(constsClass.getName() + "." + constField.getName());
            } else {
                throw new IllegalStateException("unhandled value type " + value);
            }
        }

        resolvedTypes.put(declaration, enumClass);
        return enumClass;
    }

    private JavaClassSource generateStructClass(XdrDeclaration declaration, Scope scope) {
        XdrStructType structType = (XdrStructType) declaration.getType();

        JavaClassSource structClass = Roaster.create(JavaClassSource.class);
        structClass.setName(declaration.getIdentifier());

        for (XdrDeclaration fieldDec : structType.getFields()) {
            XdrType xdrType = fieldDec.getType();
            if (xdrType instanceof XdrBasicType) {
                Class<?> propertyType = translateBasicType((XdrBasicType) xdrType, fieldDec.isOptional(), fieldDec.isArray());
                PropertySource<JavaClassSource> property = structClass.addProperty(propertyType, fieldDec.getIdentifier());
                property.setMutable(true); //getters and setters
                //TODO - if type is non-primitive and non-optional add some sort of @NotNull
                continue;
            }
            throw new IllegalStateException("unhandled field type " + xdrType);
        }
        resolvedTypes.put(declaration, structClass);
        return structClass;
    }

    private Class<?> translateBasicType(XdrBasicType type, boolean optional, boolean array) {
        //TODO - configurable support for Collections vs Arrays
        //TODO - configurable support for Primitives vs Wrappers
        switch (type.getType()) {
            case UNSIGNED_INT:
            case INT:
                if (array) {
                    return int[].class;
                }
                return optional ? Integer.class : int.class;
            case UNSIGNED_HYPER:
            case HYPER:
                if (array) {
                    return long[].class;
                }
                return optional ? Long.class : long.class;
            case FLOAT:
                if (array) {
                    return float[].class;
                }
                return optional ? Float.class : float.class;
            case DOUBLE:
                if (array) {
                    return double[].class;
                }
                return optional ? Double.class : double.class;
            case QUADRUPLE:
                return array ? BigDecimal[].class : BigDecimal.class;
            case BOOL:
                if (array) {
                    return boolean[].class;
                }
                return optional ? Boolean.class : boolean.class;
            case OPAQUE:
                assert array;
                return byte[].class;
            case STRING:
                return array ? String[].class : String.class;
            default:
                throw new IllegalStateException("unhandled xdr basic type " + type.getType());
        }
    }

    private XdrConstant resolveConstant(String constName, Scope scope) {
        Identifiable resolvedTo = null;
        Scope s = scope;
        while (s != null) {
            if ((resolvedTo = s.resolve(constName)) != null) {
                break;
            }
            s = s.getParent();
        }
        if (resolvedTo == null) {
            throw new IllegalArgumentException("unable to resolve " + constName + " starting with scope " + scope);
        }
        if (!(resolvedTo instanceof XdrConstant)) {
            throw new IllegalStateException(constName + " expected to be a constant, instead was " + resolvedTo);
        }
        return (XdrConstant) resolvedTo;
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
