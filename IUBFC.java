import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;


public class IUBFC{
    
/*
 * public void set_threshold(double t)
{
    this.t =t;
}
 */


    //Edge pruning
    public static void edge_pruning(String filename)
    {
       
       try(BufferedReader nameStream = new BufferedReader(new FileReader(filename)))
        {
            String line;
            int edge_count=0, total_edges=0;
            //UBFC.max_val =0.0;
            //t=.20;
            

            while((line =nameStream.readLine())!=null)
            {
                total_edges++;
                if(line.isEmpty())
                {
                    continue;
                }
                String[] tokens =line.split("\t");
                
                int v1 =Integer.parseInt(tokens[0]);
                int v2 =Integer.parseInt(tokens[1]);
                double p =Double.parseDouble(tokens[2]);
                //System.out.println("The value of p is: " + p);
                //System.out.println("The value of t is: " + t);
                if(p<UBFC.t)
                {
                    continue;
                }
                
                UBFC.Node n1 = UBFC.node_data.computeIfAbsent(v1, k -> new UBFC.Node());
                UBFC.Node n2 = UBFC.node_data.computeIfAbsent(v2, k -> new UBFC.Node());

                n1 = UBFC.node_data.get(v1);
                n2 =UBFC.node_data.get(v2);
                /* 
                 * if(n1 ==null || n2 ==null)
                {
                    System.out.println("Error in edge.txt file");
                    System.exit(4);
                }
                */
                
                n1.neighbors.add(new Pair<>(v2,p));
                n2.neighbors.add(new Pair<>(v1,p));

                UBFC.Edge e =new UBFC.Edge();
                e.n1 =v1;
                e.n2=v2;
                e.prob =p;
                UBFC.edges.add(e);

                if (p>UBFC.max_val)
                {
                    UBFC.max_val =p;
                }
                edge_count++;

                
            }
            System.out.println("Edges: " +total_edges);
            //int deleted_edges =total_edges-edgeCount;
            System.out.println("The number of edges satisfying the threshold: " + edge_count);

        }
        catch (IOException e)
        {
            System.out.println("No Edge file exists");
            System.exit(3);
        } 
    }



    public static int gt_binarySearch(List<Double> wedges, int left, int right, double target) 
    {
        if(left>right)
        {
            return -1;
        }

        int mid = (left+right)/2;

        if(wedges.get(mid)>=target)
        {
            if(mid==0 || wedges.get(mid-1)<target)
            {
                return mid;
            }
            else
            {
                return gt_binarySearch(wedges, left, mid-1, target);
            }
        }
        else
        {
            return gt_binarySearch(wedges, mid+1, right, target);
        }
    }




    public static int calculate_improved_list(List<Double> wedge_list)
    {
        int size=wedge_list.size();
        if(size<2)  //for constructing a butterfly, we need at least 2 wedges
        {
            return 0;
        }
        Collections.sort(wedge_list,(a,b) ->Double.compare(b,a));
        //System.out.println("WedgeList: "+ wedgeList);
        int count=0;
        int idx1 =0,idx2=1;

        while (idx2<size)
        {
            if(wedge_list.get(idx1)*wedge_list.get(idx2)<UBFC.t)
            {
               double min_threshold =UBFC.t/wedge_list.get(idx2); 

               int left =0, right =idx1-1;

               int temp_res =gt_binarySearch(wedge_list, left, right, min_threshold);

               if(idx1==-1)
                {
                    return count;
                }
                idx1=temp_res;
            }
            count =count+idx1+1;
            idx1 =idx1+1;
            idx2 =idx2+1;
        }

        return count;
    }
    
  
    //adding_probability to wedge map
    public static void add_prob_wedgemap(Map<Integer,List<Double>> wedge_map, int target_node,double prob)  
    {
        wedge_map.computeIfAbsent(target_node, x ->new ArrayList<>()).add(prob);

    }

    public static int counting_wedge_map(Map<Integer, List<Double>> wedge_map)
    {
        int count =0;
        for (Map.Entry<Integer, List<Double>> entry: wedge_map.entrySet())
        {
            count += calculate_improved_list((entry.getValue()));
        }
        return count;
    }

    //Butterfly counting with improved list count  (IUBFC) using vertex priority
    public int butterfly_counting_improved_VP()
    {
        int ib_count =0;
        //t =.50;
        double mod_threshold = UBFC.t/(UBFC.max_val*UBFC.max_val);

        for (int ux :UBFC.vertex_id)
        {
            Map<Integer,List<Double>> wedge_map =new HashMap<>();
            UBFC.Node u_node =UBFC.node_data.get(ux);

            for(Pair<Integer,Double> v: u_node.neighbors)
            {
                int vx =v.first;
                UBFC.Node v_node =UBFC.node_data.get(vx);

                if(UBFC.compare_vertex_priority(u_node, v_node))
                {
                    double uv_prob =v.second;

                    for(Pair<Integer,Double> w: v_node.neighbors)
                    {
                        int wx =w.first;
                        UBFC.Node w_node =UBFC.node_data.get(wx);
                        
                        if(ux==wx) 
                        {
                            continue;
                        }

                        if(UBFC.compare_vertex_priority(u_node,w_node))
                        {
                            double vw_prob =w.second;
                            double total_prob =uv_prob* vw_prob;

                            if(total_prob>=mod_threshold)
                            {
                                add_prob_wedgemap(wedge_map, wx, total_prob);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                else
                {
                    break;
                }
            }
            ib_count+=counting_wedge_map(wedge_map);
        }
        return ib_count;
    }



    public static void main(String[]args)
    {
        double [] t_val={0.20,0.30,0.40,0.50,0.60,0.70,0.80};
        //UBFC.check_vp();
        for (double t:t_val)
        {
            long begin_time = System.currentTimeMillis();
            UBFC.t =t ;
            UBFC ins =new UBFC();
            IUBFC graph =new IUBFC();
            UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/ciaodvdID.txt");
            IUBFC.edge_pruning("/home/montasir/Desktop/advanced_database_project/src/Dataset/ciaodvdEdge.txt");
            UBFC.check_vp();
            int result = graph.butterfly_counting_improved_VP();
            long end_time =System.currentTimeMillis();
            long running_time =end_time-begin_time;
            System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");
        }
     
    }
}








