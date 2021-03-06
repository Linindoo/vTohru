package cn.vtohru.orm.util;

import cn.vtohru.orm.exception.ClassAccessException;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);
    public static boolean hasDefaultConstructor(Class<?> cls) {
        try {
            cls.getDeclaredConstructor(new Class[0]);
            return true;
        } catch (NoSuchMethodException e) {
            LOGGER.warn(e);
            return false;
        } catch (SecurityException e) {
            throw new ClassAccessException(e);
        }
    }

    /**
     * Get a defined {@link Constructor} of the given class with the arguments
     *
     * @param cls
     *          the class to be examined
     * @param arguments
     *          the arguments of the contructor
     * @return a fitting constructor
     */
    @SuppressWarnings("rawtypes")
    public static Constructor getConstructor(Class<?> cls, Class<?>... arguments) {
        try {
            return cls.getDeclaredConstructor(arguments);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ClassAccessException(e);
        }
    }

    /**
     * Get a list of all methods declared in the supplied class, and all its superclasses (except java.lang.Object),
     * recursively.
     *
     * @param type
     *          the class for which we want to retrieve the Methods
     * @return an array of all declared and inherited fields
     */
    public static List<Method> getDeclaredAndInheritedMethods(final Class<?> type) {
        return getDeclaredAndInheritedMethods(type, new ArrayList<Method>());
    }

    private static List<Method> getDeclaredAndInheritedMethods(final Class<?> type, final List<Method> methods) {
        if ((type == null) || (type == Object.class)) {
            return methods;
        }

        final Class<?> parent = type.getSuperclass();
        final List<Method> list = getDeclaredAndInheritedMethods(parent,
                methods == null ? new ArrayList<Method>() : methods);

        for (final Method m : type.getDeclaredMethods()) {
            if (!Modifier.isStatic(m.getModifiers())) {
                list.add(m);
            }
        }

        return list;
    }

    /**
     * Retrive the type arguments of the given class
     *
     * @param clazz
     *          the class to be examined
     * @param tv
     *          a generic type variable
     * @return the type definition or null
     */
    public static <T> Class<?> getTypeArgument(final Class<? extends T> clazz,
                                               final TypeVariable<? extends GenericDeclaration> tv) {
        final Map<Type, Type> resolvedTypes = new HashMap<>();
        Type type = clazz;
        // start walking up the inheritance hierarchy until we hit the end
        while (type != null && !getClass(type).equals(Object.class)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just
                // keep going.
                type = ((Class<?>) type).getGenericSuperclass();
            } else {
                final ParameterizedType parameterizedType = (ParameterizedType) type;
                final Class<?> rawType = (Class<?>) parameterizedType.getRawType();

                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (typeParameters[i].equals(tv)) {
                        final Class<?> cls = getClass(actualTypeArguments[i]);
                        if (cls != null) {
                            return cls;
                        }
                        // We don't know that the type we want is the one in the map, if this argument has been
                        // passed through multiple levels of the hierarchy. Walk back until we run out.
                        Type typeToTest = resolvedTypes.get(actualTypeArguments[i]);
                        while (typeToTest != null) {
                            final Class<?> classToTest = getClass(typeToTest);
                            if (classToTest != null) {
                                return classToTest;
                            }
                            typeToTest = resolvedTypes.get(typeToTest);
                        }
                    }
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(Object.class)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        return null;
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable type.
     *
     * @param type
     *          the type
     * @return the underlying class
     */
    public static Class<?> getClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof WildcardType) {
            return (Class<?>) ((WildcardType) type).getUpperBounds()[0];
        } else if (type instanceof GenericArrayType) {
            final Type componentType = ((GenericArrayType) type).getGenericComponentType();
            final Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                LOGGER.debug("************ ClassUtil.getClass 1st else");
                LOGGER.debug("************ type = " + type);
                return null;
            }
        } else {
            LOGGER.debug("************ ClassUtil.getClass final else");
            LOGGER.debug("************ type = " + type);
            return null;
        }
    }

    /**
     * Generate a class definition from the given {@link Type}
     *
     * @param t
     *          the {@link Type} to be examined
     * @return a class definition for the given {@link Type}
     */
    public static Class<?> toClass(final Type t) {
        Class<?> returnClass;
        if (t == null) {
            returnClass = null;
        } else if (t instanceof Class) {
            returnClass = (Class<?>) t;
        } else if (t instanceof GenericArrayType) {
            final Class<?> type = (Class<?>) ((GenericArrayType) t).getGenericComponentType();
            returnClass = Array.newInstance(type, 0).getClass();
        } else if (t instanceof ParameterizedType) {
            returnClass = (Class<?>) ((ParameterizedType) t).getRawType();
        } else if (t instanceof WildcardType) {
            returnClass = (Class<?>) ((WildcardType) t).getUpperBounds()[0];
        } else
            throw new UnsupportedOperationException("Generic TypeVariable not supported!");

        return returnClass;
    }

    /**
     * Tries to get the class of a paramtrization like List<String>
     *
     * @param field
     *          the field to be exmined
     * @param index
     *          the position to examine
     * @return a {@link Type} definition for the given {@link Type}
     */
    public static Type getParameterizedType(final Field field, final int index) {
        if (field != null) {
            if (field.getGenericType() instanceof ParameterizedType) {
                final ParameterizedType type = (ParameterizedType) field.getGenericType();
                if ((type.getActualTypeArguments() != null) && (type.getActualTypeArguments().length <= index)) {
                    return null;
                }
                final Type paramType = type.getActualTypeArguments()[index];
                if (paramType instanceof GenericArrayType) {
                    return paramType;
                } else {
                    if (paramType instanceof ParameterizedType) {
                        return paramType;
                    } else {
                        if (paramType instanceof TypeVariable) {
                            // TODO: Figure out what to do... Walk back up the to
                            // the parent class and try to get the variable type
                            // from the T/V/X
                            return paramType;
                        } else if (paramType instanceof WildcardType) {
                            return paramType;
                        } else if (paramType instanceof Class) {
                            return paramType;
                        } else {
                            throw new ClassAccessException("Unknown type... pretty bad... call for help, wave your hands... yeah!");
                        }
                    }
                }
            }

            // Not defined on field, but may be on class or super class...
            return getParameterizedClass(field.getType());
        }

        return null;
    }

    /**
     * Tries to get the class of a paramtrization like List<String>
     *
     * @param c
     *          the class to be examined
     * @return the parametrization or null, if impossible
     */
    public static Class<?> getParameterizedClass(final Class<?> c) {
        return getParameterizedClass(c, 0);
    }

    /**
     * Tries to get the class of a parametrization like List<String>
     *
     * @param c
     *          the class to be examined
     * @param index
     *          the position to read,
     * @return the parametrization or null, if impossible
     */
    public static Class<?> getParameterizedClass(final Class<?> c, final int index) {
        final TypeVariable<?>[] typeVars = c.getTypeParameters();
        if (typeVars.length > 0) {
            final TypeVariable<?> typeVariable = typeVars[index];
            final Type[] bounds = typeVariable.getBounds();

            final Type type = bounds[0];
            if (type instanceof Class) {
                return (Class<?>) type; // broke for EnumSet, cause bounds contain
                // type instead of class
            } else {
                return null;
            }
        } else {
            final Type superclass = c.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                final Type[] actualTypeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
                return actualTypeArguments.length > index ? (Class<?>) actualTypeArguments[index] : null;
            } else if (!Object.class.equals(superclass)) {
                return getParameterizedClass((Class<?>) superclass);
            } else {
                return null;
            }
        }
    }

    /**
     * Retrive the defined {@link Field} with the given name of the class
     *
     * @param c
     *          the class to be examined
     * @param fieldName
     *          the field name to check
     * @return the Field, represented by the field name or null, if none found
     */
    public static final Field getDeclaredField(final Class<?> c, final String fieldName) {
        Field[] df = c.getDeclaredFields();
        for (Field field : df) {
            if (field.getName().equals(fieldName))
                return field;
        }
        Class sc = c.getSuperclass();
        if (!sc.isInterface() && sc != Object.class) {
            return getDeclaredField(sc, fieldName);
        }
        return null;
    }
}
