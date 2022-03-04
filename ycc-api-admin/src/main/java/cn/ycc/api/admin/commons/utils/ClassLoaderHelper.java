package cn.ycc.api.admin.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Function;

public class ClassLoaderHelper {
    public static final ClassLoaderHelper DEFAULT = new ClassLoaderHelper();

    private ClassLoader defaultClassLoader;

    public void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        this.defaultClassLoader = defaultClassLoader;
    }

    public InputStream getResourceAsStream(String resource) {
        return iterClassLoaders(null, cl -> cl.getResourceAsStream(resource));
    }

    public URL getResource(String resource) {
        return iterClassLoaders(null, cl -> cl.getResource(resource));
    }

    public Enumeration<URL> getResources(String resource) {
        return getResources(resource, null);
    }

    public Enumeration<URL> getResources(String resource, ClassLoader classLoader) {
        return iterClassLoaders(classLoader, cl -> {
            try {
                return cl.getResources(resource);
            } catch (IOException e) {
                return null;
            }
        });
    }

    private <T> T iterClassLoaders(ClassLoader cl, Function<ClassLoader, T> handler) {
        ClassLoader[] cls = getClassLoaders(cl);
        for (ClassLoader c : cls) {
            if (c == null) {
                continue;
            }
            T apply = handler.apply(c);
            if (apply != null) {
                return apply;
            }
        }
        return null;
    }

    private ClassLoader[] getClassLoaders(ClassLoader cl) {
        return new ClassLoader[]{
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader(),
                cl
        };
    }
}
