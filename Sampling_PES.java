import java.util.Map;
import java.util.Random;
import java.util.HashMap;

//This class has been used for counting uncertain butterfly using proportion estimation sampling.Apart from this
public class Sampling_PES{
    
    //ncr calculation
    public int calculate_Coefficient(int n, int r)   
    {
        if(r<0 ||r>n) 
        {
            return 0;
        }
        int res =1;
        for (int i=1;i<=r;i++)
        {
            res=res*(n-i+1);
            res=res/i;

        }
        return res;
    }



    //final calculation of butterfly calculation 

    public int deterministic_BFC(Map<Integer,Integer> m)
    {
        int d_count =0;
        for(int value:m.values())

        {
            //System.out.println("wValue: " +value);
            d_count+= calculate_Coefficient(value,2);
        }

        return d_count;
    }
    //deterministic(certain) butterfly calculation using Vertex
    public int db_sampling_Vertex(int n)  //targetnode
    {
        UBFC.Node target_node =UBFC.node_data.get(n);
        Map <Integer, Integer>wedge_map =new HashMap<>();
        if(target_node ==null)
        {
            return 0;
        }

        for(Pair<Integer,Double> entry:target_node.neighbors)
        {
            int neighbor_id =entry.first;
            UBFC.Node neighbor_node =UBFC.node_data.get(neighbor_id);
            //System.out.println("test: " + neighbor_node.get_nodeId());


            if(neighbor_node!=null)
            {
                for(Pair<Integer,Double> w_entry:neighbor_node.neighbors)
                {
                    int w_id =w_entry.first;
                    //System.out.println("Neighb: " +w_id);
                    if(n!=w_id)
                    {
                        wedge_map.merge(w_id, 1,Integer::sum);
                        //System.out.println("Wedge_map: " +wedge_map);
                    }
                    else
                    {
                        continue;
                    }
                    
                }
            }

        }
        //System.out.println("WedgeMap: " +wedge_map);
        
        return deterministic_BFC(wedge_map);
    }

    public int randomSample_generation(int range)
    {
        Random rand_obj =new Random();
        return rand_obj.nextInt(range);
    }


    public double proportion_estimate_sampling(int num_samples, double alpha)
    {
        Map<Integer, Boolean> sampled_nodes =new HashMap<>();
        double estimated_count =0.0;

        for(int i=0;i<num_samples;i++)
        {
            int nodeID =Sampling_Vertex.random_sample_generation_check(UBFC.vertex_id.size(), sampled_nodes); //first parameter, nodeData.size()
            //System.out.println("NodeID: "+nodeID);
            double res1 = db_sampling_Vertex(nodeID);
            //System.out.println(localCount);
            double extrapolated_res =(res1*(double)UBFC.vertex_id.size())/4.0; 
            estimated_count = (extrapolated_res+((double)i*estimated_count))/((double)(i+1));

        }
        return alpha*estimated_count;
    }

   //This function i have used to calculate the Bernoli alpha value 
    /*
    public double calculating_E4PDF()
    {
        int sample_size =edges.size();
        int temp=0;
        
        for (int i=0;i<sample_size;i++)
        {
            double product =1.0;
            Map<Integer,Boolean> check =new HashMap<>();
            for(int j=0;j<4;j++)
            {
                int r_sample =randomSample_generation_check(sample_size,check,100);
                System.out.println("Random sample: " +r_sample);
                double edge_prob =edges.get(r_sample).prob;
                System.out.println("edge prob: " +edge_prob);
                product =product*edge_prob;
            }
            if (product>=t)

            {
                temp=temp+1;
            }

            else{
                break;
            }

            
        }
        double alpha = temp/sample_size;
        System.out.println("the value of alpha is: " +alpha);
        return alpha;
            
    }
    */



    public static void main(String[]args)
    {

       //testing and checking neighbors pair
       //ins1.t =.40;
       //double alpha_prime = ins1.calculating_E4PDF();
       //double result=ins1.proportion_estimate_sampling(100,.001); //alpha =.001
       //System.out.println("Estimated Butterfly: " +result);

       double [] t_val={0.20,0.30,0.40,0.50,0.60,0.70,0.80};
       for (double t:t_val)

        {
            long begin_time = System.currentTimeMillis();
            Sampling_PES ins1 =new Sampling_PES();
            Sampling_Vertex ins2 =new Sampling_Vertex();
            UBFC ins3 =new UBFC();
            UBFC.t =t ;
            UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/dbpediaID.txt");
            UBFC.find_add_neighbor("/home/montasir/Desktop/advanced_database_project/src/Dataset/dbpediaEdge.txt");
            
            
            double result = ins1.proportion_estimate_sampling(100,.0001);
            long end_time =System.currentTimeMillis();
            long running_time =end_time-begin_time;
            System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");

 }

    }

}



