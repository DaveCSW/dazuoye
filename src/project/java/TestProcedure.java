import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.annotations.Annotation;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class TestProcedure {
    public AnalysisScope makeAnalysisScope(String path) throws Exception {
        String exPath=Tools.readExPath();
        String scopePath=Tools.readScopePath();
        AnalysisScope scope=AnalysisScopeReader.readJavaScope(scopePath,new File(exPath),TestProcedure.class.getClassLoader());
        Tools.readClassFilesIntoAnalysisScope(path,scope);
        return scope;
    }

    public ClassHierarchy makeClassHierarchy(AnalysisScope scope) throws ClassHierarchyException {
        return ClassHierarchyFactory.makeWithRoot(scope);
    }

    public Set<Entrypoint> makeEntryPoint(AnalysisScope scope, ClassHierarchy hierarchy){
        return new AllApplicationEntrypoints(scope,hierarchy);
    }

    public CHACallGraph makeCHACallGraph(ClassHierarchy hierarchy,Iterable<Entrypoint> entryPoints) throws CancelException {
        CHACallGraph graph=new CHACallGraph(hierarchy);
        graph.init(entryPoints);
        return graph;
    }

    public CallGraph make0CFACallGraph(AnalysisScope scope) throws ClassHierarchyException, CancelException {
        ClassHierarchy cha=ClassHierarchyFactory.makeWithRoot(scope);
        AllApplicationEntrypoints entryPoints=new AllApplicationEntrypoints(scope,cha);
        AnalysisOptions option=new AnalysisOptions(scope, entryPoints);
        SSAPropagationCallGraphBuilder builder= Util.makeZeroCFABuilder(Language.JAVA, option, new AnalysisCacheImpl(), cha, scope);
        return builder.makeCallGraph(option);
    }

    private Hashtable<String,Set<String>> analyzeCHACallGraphToTable(CHACallGraph callGraph) throws InvalidClassFileException {
        Hashtable<String,Set<String>> hashtable= new Hashtable<String, Set<String>>();
        HashSet<String> customClasses=new HashSet<String>();

        for(CGNode node:callGraph){
            if(node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method=(ShrikeBTMethod)node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    customClasses.add(method.getDeclaringClass().getName().toString());
                }
            }
        }
//        for(String customClass:customClasses){
//            System.out.println(customClass);
//        }

        for(CGNode node:callGraph){
            if(node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method=(ShrikeBTMethod)node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    //需要进行分析的方法
                    String classInnerName=method.getDeclaringClass().getName().toString();
                    if(!hashtable.containsKey(classInnerName)){
                        hashtable.put(classInnerName,new HashSet<String>());
                    }

                    //获取方法的调用者
                    for(CallSiteReference reference:method.getCallSites()){
                        //进行过滤去掉java内置类
                        if(customClasses.contains(reference.getDeclaredTarget().getDeclaringClass().getName().toString())){
                            //System.out.println(method.getSignature()+" "+classInnerName+" "+reference.getDeclaredTarget().getDeclaringClass().getName().toString());
                            hashtable.get(classInnerName).add(reference.getDeclaredTarget().getDeclaringClass().getName().toString());
                        }
                    }

                }
            }else{
                //System.out.println(String.format("'%s'不是一个ShrikeBTMethod：%s", node.getMethod(),node.getMethod().getClass()));
            }
        }
        return hashtable;
    }

    /*
     * CHACallGraph转类依赖图
     */
    public void analyzeCHACallGraphToCDG(CHACallGraph callGraph,String projectName) throws InvalidClassFileException, IOException {
        Hashtable<String,Set<String>> hashtable=analyzeCHACallGraphToTable(callGraph);
        Tools.makeDot(hashtable,projectName,true);
    }

    private Hashtable<String,Set<String>> analyzeCallGraphToTable(CallGraph callGraph) throws InvalidClassFileException {
        Hashtable<String, Set<String>> hashtable = new Hashtable<String, Set<String>>();
        String prefix=null;
        for (CGNode node : callGraph) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    //获得命名空间
                    if(prefix==null){
                        prefix=method.getDeclaringClass().getName().toString().substring(1);
                        String[] s=prefix.split("/");
                        if(s.length<=2){
                            System.out.println("命名空间错误");
                        }
                        prefix=s[0]+"."+s[1];
                    }
                    //需要进行分析的方法
                    String signature=method.getSignature();
                    if(!hashtable.containsKey(signature)){
                        hashtable.put(signature,new HashSet<String>());
                    }

                    //获取方法的调用者
                    for (CallSiteReference reference : method.getCallSites()) {
                        //过滤
                        Pattern pattern=Pattern.compile("[^$]+\\(.*\\).*");
                        if(pattern.matcher(reference.getDeclaredTarget().getSignature()).matches()&&reference.getDeclaredTarget().getSignature().startsWith(prefix)){
                            hashtable.get(signature).add(reference.getDeclaredTarget().getSignature());
                        }

                    }

                }
            }
        }
        return hashtable;
    }

    /*
     * CallGraph转方法依赖图
     */
    public void analyzeCallGraphToMDG(CallGraph callGraph,String projectName) throws InvalidClassFileException, IOException {
        Hashtable<String,Set<String>> hashtable=analyzeCallGraphToTable(callGraph);
        Tools.makeDot(hashtable,projectName,false);
    }

    /*
     * 变更方法选择类级测试方法
     */
    public void chooseTestCaseOnClassLevel(String changeInfo,CHACallGraph callGraph,String dest) throws IOException, InvalidClassFileException {
        File file=new File(changeInfo);
        List<String> changedMethodSignatures=Tools.readChangeInfo(file);
        Hashtable<String,Set<String>> hashtable=calClosure(analyzeCHACallGraphToTable(callGraph));
        Set<String> classesNeedToChange=new HashSet<String>();
        Set<String> results=new HashSet<String>();

        for(CGNode node:callGraph){
            if(node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method=(ShrikeBTMethod)node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    //System.out.println(method.getSignature());
                   if(changedMethodSignatures.contains(method.getSignature())){
                       //是变更的方法
                       //classesNeedToChange.add(method.getDeclaringClass().getName().toString());
                       for(String k:hashtable.keySet()){
                           for(String v:hashtable.get(k)){
                               if(v.equals(method.getDeclaringClass().getName().toString())){
                                   classesNeedToChange.add(k);
                               }
                           }
                       }
                   }
                }
            }
        }

        for (CGNode node : callGraph) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if(classesNeedToChange.contains(method.getDeclaringClass().getName().toString())&&isTest(method)){
                    results.add(method.getDeclaringClass().getName().toString()+" "+method.getSignature());
                }
            }

        }

        Tools.writeSelectionFile(dest,results);
    }

    /*
     * 变更方法选择方法级测试方法
     */
    public void chooseTestCaseOnMethodLevel(String changeInfo,CallGraph callGraph,String dest) throws IOException, InvalidClassFileException {
        File file=new File(changeInfo);
        List<String> changedMethodSignatures=Tools.readChangeInfo(file);
        Hashtable<String,Set<String>> hashtable=calClosure(analyzeCallGraphToTable(callGraph));
        Set<String> methodsNeedToChange=new HashSet<String>();
        Set<String> results=new HashSet<String>();



        for (CGNode node : callGraph) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    if(changedMethodSignatures.contains(method.getSignature())){
                        //是变更的方法
                        for(String k:hashtable.keySet()){
                            for(String v:hashtable.get(k)){
                                if(v.equals(method.getSignature())){
                                   methodsNeedToChange.add(k);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (CGNode node : callGraph) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if(methodsNeedToChange.contains(method.getSignature())&& isTest(method)){
                    results.add(method.getDeclaringClass().getName().toString()+" "+method.getSignature());
                }
            }
        }
        Tools.writeSelectionFile(dest,results);
    }

    /*
     * 判断方法是否为测试方法
     * todo
     */
    private static boolean isTest(ShrikeBTMethod method){
        Collection<Annotation> annotations=method.getAnnotations();
        for(Annotation annotation:annotations){
            if(new String(annotation.getType().getName().getClassName().getValArray()).equals("Test")){return true;}
        }
        return false;
    }

    private static Hashtable<String,Set<String>> calClosure(Hashtable<String,Set<String>> hashtable){
        //对标签进行标号
        Hashtable<String,Set<String>> results=hashtable;
        List<String> tags=new ArrayList<String>();
        for(String k:hashtable.keySet()){
            if(!tags.contains(k)){
                tags.add(k);
            }
            for(String v:hashtable.get(k)){
                if(!tags.contains(v)){
                    tags.add(v);
                }
            }
        }

        //建立矩阵,默认初始化为0
        int[][] m=new int[tags.size()][tags.size()];
        for(String k:hashtable.keySet()){
            for(String v:hashtable.get(k)){
                m[tags.indexOf(k)][tags.indexOf(v)]=1;
            }
        }

        //费舍尔算法
        for(int i=0;i<m.length;i+=1){
            //对每一个0位检查
            for(int j=0;j<m.length;j+=1){
                for(int k=0;k<m.length;k+=1){
                    if(m[j][k]==0){
                        for(int q=0;q<m.length;q+=1){
                            if(m[j][q]==1&&m[q][k]==1){
                                m[j][k]=1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        for(String k:results.keySet()){
            for(int i=0;i<m.length;i+=1){
                if(m[tags.indexOf(k)][i]==1){
                    results.get(k).add(tags.get(i));
                }
            }
        }
        return results;

    }

}
