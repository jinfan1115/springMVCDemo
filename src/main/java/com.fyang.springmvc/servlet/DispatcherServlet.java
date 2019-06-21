package com.fyang.springmvc.servlet;

import com.fyang.springmvc.annotation.Controller;
import com.fyang.springmvc.annotation.Qualifier;
import com.fyang.springmvc.annotation.Repository;
import com.fyang.springmvc.annotation.RequestMapping;
import com.fyang.springmvc.annotation.Service;
import com.fyang.springmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "dispatcherServlet", urlPatterns = "/*",loadOnStartup = 1,
    initParams = {@WebInitParam( name="base-package",value = "com.fyang.springmvc")})
public class DispatcherServlet extends HttpServlet {

    private String basePackage = "";//扫描的基包
    private List<String> packageName = new ArrayList<String>();//基包下面所有的带包路径权限定类名
    private Map<String,Object> instanceMap = new HashMap<String,Object>(); //注解实例化  注解上名称：实例化对象
    private Map<String,String> nameMap = new HashMap<String,String>();//带包路径的权限定名称：注解上的名称
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();//URL地址和方法的映射关系
    private Map<Method,String> methodPackageMap = new HashMap<Method,String>();//Method和权限定类名映射表  主要是为了通过Method找到该方法的对象利用反射执行

    @Override
    public void init(ServletConfig config) {
        basePackage = config.getInitParameter( "base-package" );

        try {
            //1.扫描基包得到全部的带路径权限定名
            scanBasePackage(basePackage);
            //2.把带有@Controller/@Service/@Repository的类实例化放入Map中，Key为注解上的名称
            instance(packageName);
            //3.Spring IOC注入
            springIOC();
            //4.完成URL地址与方法的映射
            handlerURLMethodMap();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    private void handlerURLMethodMap() throws ClassNotFoundException {
        if(packageName.size()<1){
            return;
        }

        for (String str:packageName) {
            Class c = Class.forName( str );

            if(c.isAnnotationPresent( Controller.class )){
                Method[] methods = c.getMethods();
                StringBuffer sb = new StringBuffer(  );
                if(c.isAnnotationPresent( RequestMapping.class )){
                    RequestMapping requestMapping = (RequestMapping) c.getAnnotation( RequestMapping.class );
                    sb.append( requestMapping.value() );
                }

                for (Method method: methods) {
                    if(method.isAnnotationPresent( RequestMapping.class )){
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation( RequestMapping.class );
                        sb.append( requestMapping.value() );
                        urlMethodMap.put( sb.toString(),method);
                        methodPackageMap.put( method,str );
                    }
                }
            }
        }
    }

    private void springIOC() throws IllegalAccessException {
        for (Map.Entry<String,Object> entry: instanceMap.entrySet()) {
            Field[] fileds = entry.getValue().getClass().getDeclaredFields();

            for (Field field:fileds) {
                if(field.isAnnotationPresent( Qualifier.class )){
                    String name = field.getAnnotation( Qualifier.class ).value();
                    field.setAccessible( true );
                    field.set( entry.getValue(),instanceMap.get( name ) );
                }

            }

        }
    }

    private void instance(List<String> packageName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //完成了被注解的类的实例化，以及和注解名称的映射
        if(packageName.size() <1){
            return;
        }

        for (String str:packageName) {
            Class c = Class.forName( str );
            if(c.isAnnotationPresent( Controller.class )){
                Controller controller = (Controller) c.getAnnotation( Controller.class );
                String controllerName = controller.value();

                instanceMap.put( controllerName, c.newInstance() );
                nameMap.put( str,controllerName );
                System.out.println("Controller::"+str +";value::"+controllerName);
            } else if(c.isAnnotationPresent( Service.class )){
                Service service = (Service) c.getAnnotation( Service.class );
                String serviceName = service.value();

                instanceMap.put( serviceName, c.newInstance() );
                nameMap.put( str,serviceName );
                System.out.println("Service::"+str +";value::"+serviceName);
            } if(c.isAnnotationPresent( Repository.class )){
                Repository repository = (Repository) c.getAnnotation( Repository.class );
                String repositoryName = repository.value();

                instanceMap.put( repositoryName, c.newInstance() );
                nameMap.put( str,repositoryName );
                System.out.println("Repository::"+str +";value::"+repositoryName);
            }
        }
    }

    private void scanBasePackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource( basePackage.replaceAll( "\\.","/" ) );
        File basePackageFile = new File( url.getPath() );
        System.out.println("scan::" +basePackageFile);

        File[] childFiles = basePackageFile.listFiles();
        for (File file :childFiles){
            if(file.isDirectory()){
                scanBasePackage( basePackage +"."+file.getName() );
            }else if(file.isFile()){
                //去掉文件后缀名，只要文件全路径
                packageName.add( basePackage +"."+ file.getName().split( "\\." )[0] );
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost( req, resp );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll( contextPath,"" );

        Method method = urlMethodMap.get( path );//通过path找到对应的method
        if(method != null){
            String packageName = methodPackageMap.get( method);
            String controllerName = nameMap.get( packageName );

            UserController userController = (UserController) instanceMap.get( controllerName );
            try {
                method.setAccessible( true );
                method.invoke( userController );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
