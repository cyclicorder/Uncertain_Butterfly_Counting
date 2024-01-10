import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;


public class Sampling_Vertex{

    //Looking for how many wedges contain a particular vertex

    public int ub_sampling_Vertex(int node)
    {
        double v_prob,w_prob,two_edge_prob;
        UBFC.Node u =UBFC.node_data.get(node);  //retrieving the node from nodeID
        if( u==null)
        {
            //System.out.println("Node is null");
            return -1;
        }
        //System.out.println("Node: "+ u.get_nodeId());
        Map<Integer, List<Double>> wedge_map =new HashMap<>();

        for (Pair<Integer,Double> v:u.neighbors)
        {
            if(v==null)
            {
                return -1;
            }
            int vx =v.first;

            UBFC.Node v_node =UBFC.node_data.get(vx);
            v_prob =v.second;

            if(v_prob<UBFC.t)
            {
                continue;
            }

            for (Pair<Integer,Double> w:v_node.neighbors)
            {
                int wx=w.first;  //node id
                UBFC.Node w_node =UBFC.node_data.get(wx);

                w_prob =w.second;

                two_edge_prob =v_prob *w_prob;

                if(two_edge_prob>=UBFC.t) 
                {
                    List<Double>wedge_list =wedge_map.getOrDefault(wx, new ArrayList<>());  //retrieving the value of wx from wedge_map
                    wedge_list.add(two_edge_prob);
                    wedge_map.put(wx, wedge_list);
                }

            }

        }

        return IUBFC.counting_wedge_map(wedge_map);
    }






    public static int random_sample_generation_check(int range, Map<Integer, Boolean>sampled_value)
    {
        Random rand_obj =new Random();
        int rand_val =rand_obj.nextInt(range); 

        if(!sampled_value.containsKey(rand_val))
            {
                sampled_value.put(rand_val,true);
                return rand_val;
            }
        else
        {
            return random_sample_generation_check(range,sampled_value);
        }
    }
    
    //uncertain butterfly sampling (UBS) 

    public double uncertain_butterfly_sampling_Vertex(int num_samples)
    {
        Map<Integer, Boolean> sampled_nodes =new HashMap<>();
        double estimated_count =0.0;

        for(int i=0;i<num_samples;i++)
        {
            int node_id =random_sample_generation_check(UBFC.vertex_id.size(), sampled_nodes); //first parameter, nodeData.size()
            //System.out.println("NodeID: "+node_id);
            double res1 = ub_sampling_Vertex(node_id);
            if((double)res1==-1.0)
            {
                continue;
            } 
             //local search result
            //System.out.println(res1);
            double converted_vertex_id =(double)UBFC.vertex_id.size();
            double extrapolated_res =(res1*converted_vertex_id)/4.0;
            estimated_count = (extrapolated_res+((double)(i)*estimated_count))/((double)(i+1));

        }
       return estimated_count;
    }





    public static void main(String[]args)
    {

       double [] t_val={0.20,0.30,0.40,0.50,0.60,0.70,0.80};
       for (double t:t_val)
        {
          
            long begin_time = System.currentTimeMillis();
            IUBFC ins =new IUBFC();
            Sampling_Vertex samp =new Sampling_Vertex();
            UBFC ins2 =new UBFC();
            UBFC.t =t ;
            UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/ciaodvdID.txt");
            UBFC.find_add_neighbor("/home/montasir/Desktop/advanced_database_project/src/Dataset/ciaodvdEdge.txt");
 
            double result = samp.uncertain_butterfly_sampling_Vertex(100);
            long end_time =System.currentTimeMillis();
            long running_time =end_time-begin_time;
            System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");
}
    }

}



