package org.apache.markt;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.WebResource;
import org.apache.catalina.util.LifecycleBase;

public class IsolatingManager extends LifecycleBase implements Manager {

    private Context context = null;

    private String managerClassName = null;
    private Manager manager = null;

    private ClassLoader isolatingClassLoader = null;


    public String getManagerClassName() {
        return managerClassName;
    }


    public void setManagerClassName(String managerClassName) {
        this.managerClassName = managerClassName;
    }


    @Override
    protected void initInternal() throws LifecycleException {
        // Resources should be started by this point
        Context context = getContext();

        // Pick up JARs from WEB-INF/lib/<fully-qualified-class-name>
        WebResource[] resources = context.getResources().listResources("/WEB-INF/lib/" + this.getClass().getName());
        URL[] urls = new URL[resources.length];
        for (int i = 0; i < resources.length; i++) {
            urls[i] = resources[i].getURL();
        }
        isolatingClassLoader = new NonDelegatingClassLoader(urls, Thread.currentThread().getContextClassLoader());

        Class<?> managerClazz;
        try {
            managerClazz = isolatingClassLoader.loadClass(managerClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new LifecycleException(cnfe);
        }

        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);

            try {
                manager = (Manager) managerClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new LifecycleException(e);
            }

            manager.setContext(context);

            if (manager instanceof Lifecycle) {
                ((Lifecycle) manager).init();
            }
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    protected void startInternal() throws LifecycleException {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);

            if (manager instanceof Lifecycle) {
                ((Lifecycle) manager).start();
            }
        } finally {
            unbindClassLoader(tccl);
        }

        setState(LifecycleState.STARTING);
    }


    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);

        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);

            if (manager instanceof Lifecycle) {
                ((Lifecycle) manager).stop();
            }
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    protected void destroyInternal() throws LifecycleException {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);

            if (manager instanceof Lifecycle) {
                ((Lifecycle) manager).destroy();
            }
        } finally {
            unbindClassLoader(tccl);
        }
    }


    private ClassLoader bindClassLoader(ClassLoader newTccl) {
        if (newTccl == null) {
            return null;
        }

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newTccl);
        return tccl;
    }


    private void unbindClassLoader(ClassLoader newTccl) {
        if (newTccl != null) {
            Thread.currentThread().setContextClassLoader(newTccl);
        }
    }


    @Override
    public Context getContext() {
        return context;
    }


    @Override
    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public SessionIdGenerator getSessionIdGenerator() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionIdGenerator();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setSessionIdGenerator(sessionIdGenerator);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public long getSessionCounter() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionCounter();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setSessionCounter(long sessionCounter) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setSessionCounter(sessionCounter);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getMaxActive() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getMaxActive();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setMaxActive(int maxActive) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setMaxActive(maxActive);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getActiveSessions() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getActiveSessions();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public long getExpiredSessions() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getExpiredSessions();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setExpiredSessions(long expiredSessions) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setExpiredSessions(expiredSessions);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getRejectedSessions() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getRejectedSessions();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getSessionMaxAliveTime() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionMaxAliveTime();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setSessionMaxAliveTime(int sessionMaxAliveTime) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setSessionMaxAliveTime(sessionMaxAliveTime);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getSessionAverageAliveTime() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionAverageAliveTime();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getSessionCreateRate() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionCreateRate();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public int getSessionExpireRate() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.getSessionExpireRate();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void add(Session session) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.add(session);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.addPropertyChangeListener(listener);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    @Deprecated
    public void changeSessionId(Session session) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.changeSessionId(session);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void changeSessionId(Session session, String newId) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.changeSessionId(session, newId);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public Session createEmptySession() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.createEmptySession();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public Session createSession(String sessionId) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.createSession(sessionId);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public Session findSession(String id) throws IOException {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.findSession(id);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public Session[] findSessions() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.findSessions();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void load() throws ClassNotFoundException, IOException {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.load();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void remove(Session session) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.remove(session);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void remove(Session session, boolean update) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.remove(session, update);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.removePropertyChangeListener(listener);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void unload() throws IOException {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.unload();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void backgroundProcess() {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.backgroundProcess();
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public boolean willAttributeDistribute(String name, Object value) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            return manager.willAttributeDistribute(name, value);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setNotifyBindingListenerOnUnchangedValue(boolean notifyBindingListenerOnUnchangedValue) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setNotifyBindingListenerOnUnchangedValue(notifyBindingListenerOnUnchangedValue);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    @Override
    public void setNotifyAttributeListenerOnUnchangedValue(boolean notifyAttributeListenerOnUnchangedValue) {
        ClassLoader tccl = null;
        try {
            tccl = bindClassLoader(isolatingClassLoader);
            manager.setNotifyAttributeListenerOnUnchangedValue(notifyAttributeListenerOnUnchangedValue);
        } finally {
            unbindClassLoader(tccl);
        }
    }


    private static class NonDelegatingClassLoader extends URLClassLoader {

        public NonDelegatingClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> clazz= null;
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException cnfe) {
                // Ignore
            }

            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            clazz = Class.forName(name, false, getParent());
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }

        @Override
        public URL getResource(String name) {
            URL url;
            url = findResource(name);
            if (url == null) {
                url = getParent().getResource(name);
            }
            return url;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            // Not the most efficient way to do this - see WebappClassLoaderBase.CombinedEnumeration
            List<URL> result = new ArrayList<>();

            Enumeration<URL> urls = findResources(name);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                result.add(url);
            }

            urls = getParent().getResources(name);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                result.add(url);
            }

            return Collections.enumeration(result);
        }
    }
}
