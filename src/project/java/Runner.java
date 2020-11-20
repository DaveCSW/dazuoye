import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

import java.io.IOException;
import java.util.Set;

public class Runner {
    private static TestProcedure procedure=new TestProcedure();
    public static void main(String[] args) {
        String path1="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\0-CMD\\target";
        String path2="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\0-CMD\\data\\change_info.txt";

        //获取命令行输入的参数
        if(args.length<3 && args[0].length()!=2){
            System.out.println("参数格式错误");
            System.exit(-1);
        }
        String pt=args[1];
        String ci=args[2];

        CHACallGraph callGraph=null;
        CallGraph graph=null;
        try {
            AnalysisScope scope=procedure.makeAnalysisScope(pt);
            ClassHierarchy hierarchy=procedure.makeClassHierarchy(scope);

            Set<Entrypoint> entryPoints=procedure.makeEntryPoint(scope,hierarchy);
            callGraph=procedure.makeCHACallGraph(hierarchy,entryPoints);
            graph=procedure.make0CFACallGraph(scope);
//            procedure.analyzeCHACallGraphToCDG(callGraph);
//            procedure.analyzeCallGraphToMDG(graph);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassHierarchyException e) {
            e.printStackTrace();
        }catch (WalaException e) {
            e.printStackTrace();
        } catch (CancelException e) {
            e.printStackTrace();
        } catch (InvalidClassFileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (args[0].charAt(1)){
            case 'c' :{
                try {
                    chooseClassTestCase(pt,ci,callGraph);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidClassFileException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 'm':{
                try {
                    chooseMethodTestCase(pt,ci,graph);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidClassFileException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:{
                System.out.println("参数格式错误");
                System.exit(-1);
            }

        }
    }

    private static void chooseClassTestCase(String projectTarget,String changeInfo,CHACallGraph callGraph) throws IOException, InvalidClassFileException {
        procedure.chooseTestCaseOnClassLevel(changeInfo,callGraph,"./selection-class.txt");
    }

    private static void chooseMethodTestCase(String projectTarget,String changeInfo,CallGraph graph) throws IOException, InvalidClassFileException {
        procedure.chooseTestCaseOnMethodLevel(changeInfo,graph,"./selection-method.txt");
    }

    public static void makeDots(){
        String path1="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\1-ALU\\target";
        String path2="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\2-DataLog\\target";
        String path3="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\3-BinaryHeap\\target";
        String path4="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\4-NextDay\\target";
        String path5="C:\\Users\\Administrator\\Desktop\\ClassicAutomatedTesting\\5-MoreTriangle\\target";

        String p1="ALU";
        String p2="DataLog";
        String p3="BinaryHeap";
        String p4="NextDay";
        String p5="MoreTriangle";

        String[] paths=new String[]{path1,path2,path3,path4,path5};
        String[] names=new String[]{p1,p2,p3,p4,p5};

        for(int i=0;i<paths.length;i+=1){
            try {
                AnalysisScope scope=procedure.makeAnalysisScope(paths[i]);
                ClassHierarchy hierarchy=procedure.makeClassHierarchy(scope);

                Set<Entrypoint> entryPoints=procedure.makeEntryPoint(scope,hierarchy);
                CHACallGraph callGraph=procedure.makeCHACallGraph(hierarchy,entryPoints);
                CallGraph graph=procedure.make0CFACallGraph(scope);
                procedure.analyzeCHACallGraphToCDG(callGraph,names[i]);
                procedure.analyzeCallGraphToMDG(graph,names[i]);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassHierarchyException e) {
                e.printStackTrace();
            }catch (WalaException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
