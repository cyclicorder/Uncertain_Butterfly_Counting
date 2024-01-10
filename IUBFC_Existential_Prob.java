
import java.util.Map;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

public class IUBFC_Existential_Prob {

    public void sort_existential_prob(UBFC.Node n)  //sort the neighbors by existential probability(descending order)
    {
        Collections.sort(n.neighbors, Comparator.comparingDouble((Pair<Integer,Double>p)->p.second).reversed());
    }

   public void check_existential_prob()
{
    for (Map.Entry<Integer,UBFC.Node> x:UBFC.node_data.entrySet())
    {
        sort_existential_prob(x.getValue());
    }
}

    
    //iubfc_existential probability(IUBFC-EP)
    public int butterfly_counting_improved_EP()
{
    int ib_count =0;
    //t =.50;
    double mod_threshold = UBFC.t/(UBFC.max_val*UBFC.max_val);
    for (int u :UBFC.vertex_id)
    {
        Map<Integer,List<Double>> wedge_map =new HashMap<>();
        UBFC.Node u_node =UBFC.node_data.get(u);
        for(Pair<Integer,Double> v: u_node.neighbors)
        {
            int vx =v.first;
            UBFC.Node v_node =UBFC.node_data.get(vx);

            double uv_prob =v.second;
            if(uv_prob>=mod_threshold)
            {
                
                for(Pair<Integer,Double> w: v_node.neighbors)
                {
                    int wx = w.first;
                    UBFC.Node w_node =UBFC.node_data.get(wx);
                    if(u==wx) 
                    {
                        continue;
                    }

                    double vwProb =w.second;
                    double total_prob =uv_prob*vwProb;

                    if(total_prob>=mod_threshold && UBFC.compare_vertex_priority(u_node, v_node) && UBFC.compare_vertex_priority(u_node, w_node))
                    {
                        IUBFC.add_prob_wedgemap(wedge_map, wx, total_prob);
                    }
                    else
                        {
                            break;
                        }
                }
            }
            else
            {
                break;
            }
        }
        ib_count+=IUBFC.counting_wedge_map(wedge_map);
    }
    return ib_count;
}
    



    public static void main(String[] args) {
    
        double [] t_val={0.20,0.30,0.40,0.50,0.60,0.70,0.80};
        for (double t:t_val)
        {
            long begin_time = System.currentTimeMillis();
            IUBFC_Existential_Prob ep =new IUBFC_Existential_Prob();
            IUBFC obj =new IUBFC();
            UBFC ins2 =new UBFC();
            UBFC.t =t ;
            UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/flickrID.txt");
            IUBFC.edge_pruning("/home/montasir/Desktop/advanced_database_project/src/Dataset/flickrEdge.txt");
            ep.check_existential_prob();
            int result = ep.butterfly_counting_improved_EP();
            //System.out.println("Number of Uncertain Butterflies: " + result1);
            long end_time =System.currentTimeMillis();
            long running_time =end_time-begin_time;
            System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");
}

    }
    
}
