import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
//import java.io.FileWriter;


class Pair<T1, T2> {
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

public class UBFC{
        public static List<Integer>vertex_id; //node Id
        public static  Map<Integer, Node>node_data;  //id and the actual node
        public static List<Edge> edges; // all edges
        public static double t, max_val;

    public UBFC()
    {
        vertex_id =new ArrayList<>();
        node_data =new HashMap<>();
        edges = new ArrayList<>();
    }

    //id file initialization
    public static void id_to_obj(String filename)  // node_id to node_obj
    {
        try(BufferedReader nameStream = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line =nameStream.readLine())!=null)
            {
                String[] tokens =line.split("\t");
                if (tokens.length > 0 && !tokens[0].trim().isEmpty()) {
                    try {
                        int id = Integer.parseInt(tokens[0]);
                        Node n = new Node();
                        n.id = id;
                        vertex_id.add(id);
                        node_data.put(id, n);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing integer value. Check the input file format.");
                        //System.out.println("Problematic line: " + line);
                        System.exit(2);
                    }
                }
            }
            //System.out.println("Total Nodes: " +vertex_id.size());

        }
        catch (IOException e)
        {
            System.out.println("There is error in file");
            System.exit(2);
        }
        
    }

    public static void find_add_neighbor(String filename)     //find and add neighbor to nodeData
    {
        try(BufferedReader nameStream = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line =nameStream.readLine())!=null)
            {
                String[] tokens =line.split("\t");
                
                int v1 =Integer.parseInt(tokens[0]);
                int v2 =Integer.parseInt(tokens[1]);
                double p =Double.parseDouble(tokens[2]);

                Node n1 = UBFC.node_data.computeIfAbsent(v1, k -> new UBFC.Node());
                Node n2 = UBFC.node_data.computeIfAbsent(v2, k -> new UBFC.Node());
                n1 = node_data.get(v1);
                n2 =node_data.get(v2);

                if(n1 ==null || n2 ==null)
                {
                    System.out.println("Error in edge.txt file");
                    System.exit(4);
                }
                
                n1.neighbors.add(new Pair<>(v2,p));
                n2.neighbors.add(new Pair<>(v1,p));

                Edge e =new Edge();
                e.n1 =v1;
                e.n2=v2;
                e.prob =p;
                edges.add(e);

                if (p>max_val)
                {
                    max_val =p;
                }

                
            }
            //System.out.println("Edges: " +edges.size());

        }
        catch (IOException e)
        {
            System.out.println("There is no edge file");
            System.exit(3);
        }
    }


    public static class Node{
        public int id;
        public List<Pair<Integer, Double>>neighbors;
    
        
        public Node()
        {
          neighbors =new ArrayList<>();
        }
        public void addNeighbor(int n, double d)
        {
            Pair<Integer,Double> pair =new Pair<>(n,d);
            neighbors.add(pair);
        }
        public int get_nodeId()
        {
            return id;
        }
    
    }
    
    public static class Edge{
        public int n1,n2;
        public double prob;
    
    }

    
    public  static boolean compare_vertex_priority(Node n1, Node n2) //vertex priority comparing of nodes
    {
        if(n1.neighbors.size() == n2.neighbors.size())
        {
            return Integer.compare(n1.id,n2.id)>0;
        }
        else
        {
            return n1.neighbors.size()>n2.neighbors.size();
        }
    }

    

    public  static boolean  reverse_compare_vertex_priority(Node n1, Node n2) //Reverse vertex priority checking of nodes
    {
        if(n1.neighbors.size() == n2.neighbors.size())
        {
            return Integer.compare(n1.id, n2.id)<0;
        }
        else
        {
            return n1.neighbors.size()< n2.neighbors.size();
        }
    }

    

    public  static void sort_neighbor_vertex_priority(Node n)  // sort neighbor by increasing vertex priority
    {
        n.neighbors.sort(Comparator.comparing(p -> node_data.get(p.first), (n1, n2) -> reverse_compare_vertex_priority(n1, n2) ? -1 : 1));
        /* 
        n.neighbors.sort((x1,x2)->
        {
            Node n1 =node_data.get(x1.first);
            Node n2 =node_data.get(x2.first);

            int size =Integer.compare(n1.neighbors.size(),n2.neighbors.size());

            if(size == 0)
            {
                return Integer.compare(n1.id, n2.id);
            }
            else
            {
                return size*(-1);
            }

        });
        */
       
        

    }
        
    public static void check_vp() 
    {
        for (Node node : node_data.values()) 
        {
            sort_neighbor_vertex_priority(node);
        }
    }
     
      //Baseline method for calculating probability
    public double calculate_prob_base_method(int u, int w, List<Pair<Integer, Double>>edge_list)
    {
        Map<Integer,Double> prob_list =new HashMap<>();

            for (Pair<Integer,Double>edge: edge_list)
            {
                prob_list.put(edge.first,edge.second);
            }
            double u_prob =prob_list.getOrDefault(u, 0.0);
            double w_prob =prob_list.getOrDefault(w, 0.0);
            double combined_prob =u_prob*w_prob;
            return combined_prob;
    

    }


      //Baseline method of counting the number of uncertain butterflies from a list
 
    public int butterfly_counting_base(int u,int w, List <Integer>wedges)
    {
        int count =0;
        int n =wedges.size();
        double wedge1_prob, wedge2_prob;
        //System.out.println("The value of t is: " + t);
        for (int i =0; i<n;i++)
        {
            for (int j=i+1;j<n;j++)
            {
                Node n1= node_data.get(wedges.get(i));
                //System.out.println("Test N1: " +n1.get_nodeId());
                wedge1_prob =calculate_prob_base_method(u, w, n1.neighbors);

                Node n2= node_data.get(wedges.get(j));
                //System.out.println("Test N2: " +n2.get_nodeId());
                wedge2_prob =calculate_prob_base_method(u, w, n2.neighbors);

                if(wedge1_prob*wedge2_prob>=t)
                {
                    count=count+1;
                }


            }
        }
        return count;
    }

    //Function to calculate the butterfly (UBFC)
    public int uncertain_butterfly_EXACT() {
        int b_count = 0;
        Map<Integer, List<Integer>> wedge_map = new HashMap<>(); //store the middle node of wedge
        Iterator<Map.Entry<Integer, List<Integer>>> wMapIt;
    
        List<Integer> temp_w;
        //System.out.println("Vertex list: "+ vertexId);
        for (int u : vertex_id) {
            wedge_map.clear();
            Node u_node = node_data.get(u);
            //System.out.println(uVertex);
            for (Pair<Integer, Double> v : u_node.neighbors) {
                //int vVertex = vP.first;
                int vx =v.first;
                Node v_node = node_data.get(vx);
                //System.out.println("\t" + vx);
                if (compare_vertex_priority(u_node, v_node)) {
                    for (Pair<Integer, Double> w : v_node.neighbors) {
                        int wx = w.first;
                        if (u == wx) {
                            continue;
                        }
                        //System.out.println("\t\t" + wVertex);
                        if (compare_vertex_priority(u_node, node_data.get(wx))) {
                            wMapIt = wedge_map.entrySet().iterator();
                            boolean found = false;
                            while (wMapIt.hasNext()) {
                                Map.Entry<Integer, List<Integer>> entry = wMapIt.next();
                                if (entry.getKey() == wx) {
                                    entry.getValue().add(vx);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                temp_w = new ArrayList<>();
                                temp_w.add(vx);
                                wedge_map.put(wx, temp_w);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            for (Map.Entry<Integer, List<Integer>> m : wedge_map.entrySet()) {
                b_count += butterfly_counting_base(u, m.getKey(), m.getValue());
            }
        }
        return b_count;
    }
    
    

    public static void main(String[]args)
    {
        
        long begin_time = System.currentTimeMillis();
        UBFC graph =new UBFC();
        UBFC.t =0.40 ;
        UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/flickrID.txt");
        UBFC.find_add_neighbor("/home/montasir/Desktop/advanced_database_project/src/Dataset/flickrEdge.txt");
        UBFC.check_vp();
            
        int result = graph.uncertain_butterfly_EXACT();
        long end_time =System.currentTimeMillis();
        long running_time =end_time-begin_time;
        System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");
        

         /* 
       double [] t_val={0.20,0.30,0.40,0.50,0.60,0.70,0.80};
    
       for (double t:t_val)
        {
            long begin_time = System.currentTimeMillis();
            UBFC graph =new UBFC();
            UBFC.t =t ;
            UBFC.id_to_obj("/home/montasir/Desktop/advanced_database_project/src/Dataset/youtubeID.txt");
            UBFC.find_add_neighbor("/home/montasir/Desktop/advanced_database_project/src/Dataset/youtubeEdge.txt");
            UBFC.check_vp();
           
            
            int result = graph.uncertain_butterfly_EXACT();
            long end_time =System.currentTimeMillis();
            long running_time =end_time-begin_time;
            System.out.println("t : " + t + "||Number of Uncertain Butterflies: " +result+"||Runtime: "+running_time + " ms");
        //writer.write((int)running_time);
        }
        */
        /* 
       System.out.println("Primary state:");
       for(UBFC.Node node : UBFC.node_data.values())
       {
        System.out.println("Node: " + node.id +" " +"neighbors: "+node.neighbors);
       }
       System.out.println("Before sorting done");

       UBFC.check_vp();

       System.out.println("Final state:");
       for(UBFC.Node node : UBFC.node_data.values())
       {
        System.out.println("Node: " + node.id +" " +"neighbors: " +node.neighbors);
       }
        
      //long result = graph.uncertain_butterfly_EXACT();
      //System.out.println("UBFC Result: " + result);
*/
       
    }

}



