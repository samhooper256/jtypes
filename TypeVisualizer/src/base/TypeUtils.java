package base;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * @author Sam Hooper
 *
 */
public class TypeUtils {
	
	private static Set<Class<?>> classes = new LinkedHashSet<>();
	private static Map<Class<?>, Set<Class<?>>> directSubtypes = new HashMap<>();
	private static Map<Class<?>, Set<Class<?>>> directSupertypes = new HashMap<>();
	private static final Set<String> CONSIDERED_MODULES = Set.of("java.base");
	
	static {
		findClasses();
	}
	
	private static void findClasses() {
		File root = new File(System.getProperty("java.home"));
		File lib = new File(root, "lib");
		final ZipFile srczip;
		try {
			srczip = new ZipFile(new File(lib, "src.zip"));
		}
		catch (IOException e) {
			System.err.println("Could not find the src.zip file");
			return;
		}
		Iterator<? extends ZipEntry> itr = srczip.entries().asIterator();
		while(itr.hasNext())
			extractFromEntry(itr.next());
	}
	
	private static void extractFromEntry(ZipEntry entry) {
		String name = entry.getName();
		if(name.endsWith(".java")) {
			int firstSlash = name.indexOf('/');
			String moduleName = name.substring(0, firstSlash);
			if(!CONSIDERED_MODULES.contains(moduleName))
				return;
			String fullyQualifiedName = name.substring(firstSlash + 1, name.length() - 5).replace('/', '.');
			if(fullyQualifiedName.endsWith("-info"))
				return; //catches module-info or package-info files
			try {
				Class<?> clazz = Class.forName(fullyQualifiedName);
				classes.add(clazz);
			}
			catch (ClassNotFoundException e) {
				//ignore
			}
		}
	}
	
	public synchronized Set<Class<?>> getAllClasses() {
		if(classes == null)
			findClasses();
		return Collections.unmodifiableSet(classes);
	}
	
	public static Set<Class<?>> directSubtypesOf(Class<?> clazz) {
		Objects.requireNonNull(clazz);
		if(directSubtypes.containsKey(clazz))
			return directSubtypes.get(clazz);
		if(!classes.contains(clazz)) {
			Set<Class<?>> result = Collections.emptySet();
			directSubtypes.put(clazz, result);
			return result;
		}
		Set<Class<?>> result = new HashSet<>();
		if(clazz.isInterface()) {
			for(Class<?> potentialSubtype : classes)
				if(isDirectSupertype(clazz, potentialSubtype))
					result.add(potentialSubtype);
		}
		else {
			for(Class<?> potentialSubtype : classes)
				if(potentialSubtype.getSuperclass() == clazz)
					result.add(potentialSubtype);
		}
		directSubtypes.put(clazz, result);
		return Collections.unmodifiableSet(result);
	}
	
	public static Set<Class<?>> directSupertypesOf(Class<?> clazz) {
		Objects.requireNonNull(clazz);
		if(directSupertypes.containsKey(clazz))
			return directSupertypes.get(clazz);
		Set<Class<?>> result = new HashSet<>();
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null)
			result.add(superClass);
		Class<?>[] interfaces = clazz.getInterfaces();
		outer:
		for(Class<?> supertype : interfaces) {
			for(Iterator<Class<?>> itr = result.iterator(); itr.hasNext();) {
				Class<?> c = itr.next();
				if(supertype.isAssignableFrom(c)) continue outer;
				if(c.isAssignableFrom(supertype)) itr.remove();
			}
			result.add(supertype);
		}
		
		directSupertypes.put(clazz, result);
		return Collections.unmodifiableSet(result);
	}
	
	/** Returns {@code true} if {@code directSubtype} is a direct subtype of {@code supertype}, {@code false} otherwise.
	 * Arguments must not be {@code null}.
	 */
	public static boolean isDirectSubtype(Class<?> directSubtype, Class<?> supertype) {
		Objects.requireNonNull(supertype);
		Objects.requireNonNull(directSubtype);
		return directSupertypesOf(directSubtype).contains(supertype);
	}
	
	/** Returns {@code true} if {@code directSupertype} is a direct supertype of {@code subtype}, {@code false}
	 * otherwise. Arguments must not be {@code null}.
	 */
	public static boolean isDirectSupertype(Class<?> directSupertype, Class<?> subtype) {
		Objects.requireNonNull(directSupertype);
		Objects.requireNonNull(subtype);
		return directSupertypesOf(subtype).contains(directSupertype);
	}
	
}
