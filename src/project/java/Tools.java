import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.WalaException;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Tools {

    public static void readClassFilesIntoAnalysisScope(String rootDir, AnalysisScope scope) throws Exception {

        File file=new File(rootDir);
        if(!file.exists() || !file.isDirectory()){
            throw new Exception("文件定位错误");
        }else{
            String[] names=file.list();
            if(names==null){
                return;
            }
            for(String name:names){
                String fullPath=rootDir.concat("/").concat(name);
                if(name.endsWith(".class")){
                    scope.addClassFileToScope(ClassLoaderReference.Application,new File(fullPath));
                    System.out.println(fullPath);
                }else{
                    if(new File(fullPath).isDirectory()){
                        readClassFilesIntoAnalysisScope(fullPath,scope);
                    }
                }
            }
        }
    }

    /*
     * 生成.dot文件
     */
    public static void makeDot(Hashtable<String,Set<String>> table,String projectName,boolean isClass) throws IOException {
        String path="src/report";
        File f=new File(path);
        if(!f.exists()) f.mkdir();
        path=path.concat("/"+String.format("%s-%s.dot",isClass?"class":"method",projectName));
        f=new File(path);
        if(f.exists()) {
            f.delete();
            f.createNewFile();
        }
        BufferedWriter writer=new BufferedWriter(new FileWriter(f));
        writer.append(String.format("digraph %s_%s {\n",projectName,isClass?"class":"method"));
        for(String k:table.keySet()){
            for(String s:table.get(k)){
                writer.append(String.format("    \"%s\"->\"%s\";\n",s,k));
                writer.flush();
            }
        }
        writer.append("}");
        writer.flush();
        writer.close();
    }

    public static void writeSelectionFile(String dest,Set<String> set) throws IOException {
        File f=new File(dest);
        if(f.exists()){
            f.delete();
            f.createNewFile();
        }
        BufferedWriter writer=new BufferedWriter(new FileWriter(f));
        for(String s:set){
            writer.append(s+"\n");
            writer.flush();
        }
        writer.close();
    }

    public static List<Pattern> readExclusions(File file) throws IOException {
        List<Pattern> patterns=new ArrayList<Pattern>();
        BufferedReader reader= new BufferedReader(new FileReader(file));
        String c;
        while((c = reader.readLine())!=null){
            patterns.add(Pattern.compile(c.trim().replace("\\","")));
        }
        return patterns;
    }

    public static List<String> readChangeInfo(File file) throws IOException {
        List<String> list=new ArrayList<String>();
        BufferedReader reader= new BufferedReader(new FileReader(file));
        String c;
        while((c = reader.readLine())!=null){
            list.add(c.trim().split(" ")[1]);
        }
        return list;
    }


    public static String readScopePath(){
        try {
            Properties properties= WalaProperties.loadProperties();
            return properties.getProperty("scopePath")!=null?properties.getProperty("scopePath"):"scope.txt";
        } catch (WalaException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readExPath() throws WalaException {
        try {
            Properties properties= WalaProperties.loadProperties();
            return properties.getProperty("exclusionPath")!=null?properties.getProperty("exclusionPath"):"exclusion.txt";
        } catch (WalaException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getPropertyByName(String fileName,String propertyName) {
        Properties properties=new Properties();
        InputStream inputStream=Object.class.getResourceAsStream("/"+fileName);
        InputStreamReader reader= null;
        try {
            reader = new InputStreamReader(inputStream,"utf8");
            properties.load(reader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(propertyName);
    }

    private static void clearFile(File f){
        if(f.exists()&&f.isFile()){
            try {
                FileWriter writer=new FileWriter(f);
                writer.write("");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
