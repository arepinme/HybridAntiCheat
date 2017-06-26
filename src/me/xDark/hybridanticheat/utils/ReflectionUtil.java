package me.xDark.hybridanticheat.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class ReflectionUtil {

	public static enum ReflectionType {
		NORMAL, STATIC, ALL;
	}

	public static Field[] getFields(Class<?> clazz, ReflectionType type) {
		HashSet<Field> fields = new HashSet<>();
		for (Field f : clazz.getDeclaredFields()) {
			if (!checkMember(f, type))
				continue;
			fields.add(f);
		}
		Field[] array = fields.toArray(new Field[fields.size()]).clone();
		fields.clear();
		return array;
	}

	public static Method[] getMethods(Class<?> clazz, ReflectionType type) {
		HashSet<Method> methods = new HashSet<>();
		for (Method m : clazz.getDeclaredMethods()) {
			if (!checkMember(m, type))
				continue;
			methods.add(m);
		}
		Method[] array = methods.toArray(new Method[methods.size()]).clone();
		methods.clear();
		return array;
	}

	private static boolean checkMember(Member member, ReflectionType fieldType) {
		if (fieldType == ReflectionType.ALL)
			return true;
		if (fieldType == ReflectionType.NORMAL && Modifier.isStatic(member.getModifiers()))
			return false;
		if (fieldType == ReflectionType.STATIC && !Modifier.isStatic(member.getModifiers()))
			return false;
		return true;
	}

	public static Field findField(Class<?> clazz, String fieldName) {
		Field returnment = null;
		try {
			do {
				returnment = clazz.getDeclaredField(fieldName);
				if (returnment != null)
					break;
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		} catch (Throwable t) {
		}
		return setAccessible(returnment);
	}

	@Deprecated
	public static Field findFieldFromClass(Class<?> clazz, String fieldClassName) {
		Field returnment = null;
		try {
			do {
				for (Field field : clazz.getDeclaredFields())
					if (field.getClass().getName().contains(fieldClassName)) {
						returnment = field;
						break;
					}
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		} catch (Throwable t) {
		}
		return setAccessible(returnment);
	}

	public static Field findFieldFromClass(Class<?> clazz, Class<?> fieldType) {
		Field returnment = null;
		try {
			do {
				for (Field field : clazz.getDeclaredFields())
					if (field.getType() == fieldType) {
						returnment = field;
						break;
					}
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		} catch (Throwable t) {
		}
		return setAccessible(returnment);
	}

	public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] arguments) {
		Method returnment = null;
		try {
			do {
				returnment = clazz.getDeclaredMethod(methodName, arguments);
				if (returnment != null)
					break;
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		} catch (Throwable t) {
		}
		return setAccessible(returnment);
	}

	public static Method findMethod(Class<?> clazz, Class<?>[] arguments) {
		Method returnment = null;
		try {
			do {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getParameterCount() == arguments.length && checkMethod(m.getParameterTypes(), arguments)) {
						returnment = m;
						break;
					}
				}
				if (returnment != null)
					break;
			} while ((clazz = clazz.getSuperclass()) != Object.class);
		} catch (Throwable t) {
		}
		return setAccessible(returnment);
	}

	private static boolean checkMethod(Class<?>[] parameterTypes, Class<?>[] arguments) {
		int errors = 0;
		for (int i = 0; i < parameterTypes.length; i++)
			if (parameterTypes[i] != arguments[i])
				errors++;
		return (errors == 0);
	}

	@SuppressWarnings("unchecked")
	public static <T> T setAccessible(Object obj) {
		if (obj == null)
			return (T) obj;
		if (CastUtil.canBeCasted(obj, AccessibleObject.class)) {
			AccessibleObject accessible = (AccessibleObject) obj;
			accessible.setAccessible(true);
		}
		if (!(obj instanceof Method)) {
			Field modifiers;
			try {
				modifiers = obj.getClass().getDeclaredField("modifiers");
				modifiers.setInt(obj,
						(int) invoke(findMethod(obj.getClass(), "getModifiers", new Class[0]), obj, new Object[0])
								& ~Modifier.FINAL);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}
		return (T) obj;

	}

	public static boolean isStatic(Member handle) {
		return Modifier.isStatic(handle.getModifiers());
	}

	public static <T> T getValue(Field field, Object handle) {
		if (field == null)
			return null;
		try {
			if (!field.isAccessible())
				field = setAccessible(field);
			return CastUtil.cast(field.get(isStatic(field) ? handle.getClass() : handle));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}

	public static void setValue(Field field, Object handle, Object value) {
		try {
			if (!field.isAccessible())
				field = setAccessible(field);
			field.set(isStatic(field) ? handle.getClass() : handle, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
		}
	}

	public static <T> T invoke(Method m, Object handle, Object... args) {
		try {
			if (!m.isAccessible())
				m = setAccessible(m);
			return CastUtil.cast(m.invoke(isStatic(m) ? handle.getClass() : handle, args));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	public static void setStaticFinalField(Field field, Object newValue)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		((Field) setAccessible(Field.class.getDeclaredField("modifiers"))).setInt(field,
				field.getModifiers() & ~Modifier.FINAL);
		((Field) setAccessible(Field.class.getDeclaredField("root"))).set(field, null);
		((Field) setAccessible(Field.class.getDeclaredField("overrideFieldAccessor"))).set(field, null);
		((Field) setAccessible(field)).set(null, newValue);
	}

	private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... classes) {
		try {
			return clazz.getDeclaredConstructor(classes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	public static Object createInstance(Class<?> clazz, Object... args) {
		try {
			return ReflectionUtil.getConstructor(clazz, swap(args)).newInstance(args);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?>[] swap(Object[] objects) {
		Class<?>[] classes = new Class[objects.length];
		for (int i = 0; i < classes.length; i++)
			classes[i] = objects[i].getClass();
		return classes;
	}

}
