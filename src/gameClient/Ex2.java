package gameClient;

import Server.Game_Server_Ex2;
import api.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

/**
 * This class represents how the game will behave: where will be the next location we will go to using methods:
 * run()
 * moveAgants(game_service game, directed_weighted_graph gg)
 * init(game_service game)
 * nextNode(directed_weighted_graph g, int src, game_service game)
 * Further explanation is down there above each function
 *
 */
public class Ex2 implements Runnable  {
    public static MyFrame _win;
    private static Arena _ar;
    public static long userId = -999;
    public static int gameLevel = -999;
    public static Thread client = new Thread(new Ex2());
    public static float time;

    public static void main(String[] a) {
        if (a.length == 2) {
            userId = Integer.valueOf(a[0]);
            gameLevel = Integer.valueOf(a[1]);
            client.start();
        } else {
            _win = new MyFrame("Pokemon game");
            _win.LoginPanel();

        }
    }
    public static void setGameLevel(int level) {
        Ex2.gameLevel = level;
    }

    /**
     * set the login id, if number is too long or id is negative won't do nothing
     * @param id
     */
    public static void setLogin_id(long id) {
        Ex2.userId = id;
    }
    /**
     *Run the ex2.java - well start game here
     *This method have a Lists for agents and pokemons and see
     *how many agent can go to different pokemons
     *they have loops for the list of Agents and well give any agent the pokemon
     *that can have short path from them
     *
     */
    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(gameLevel); // you have [0,23] games
        if (game != null) {
            long id = userId;
            game.login(id);
            int x;
            String g = game.getGraph();
            String pks = game.getPokemons();
            directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
            dw_graph_algorithms g_algo = new DWGraph_Algo();
            g_algo.init(gg);
            init(game);

            List<CL_Pokemon> ffs = Arena.json2Pokemons(pks);
            List<CL_Agent> log = new ArrayList<>();
            String agent = game.getAgents();
            log = Arena.getAgents(agent, gg);
            edge_data edge = new EdgeData();
            if (ffs.size() >= log.size())
                while (log.isEmpty()) {
                    CL_Agent c = log.get(log.size() - 1);
                    for (CL_Pokemon p : ffs) {
                        if (p.getpok() == false) {
                            Arena.updateEdge(p, gg);

                            if (p.getType() == 1) {

                                x = Math.min(edge.getSrc(), edge.getDest());
                            } else
                                x = Math.max(edge.getSrc(), edge.getDest());
                            game.addAgent(x);
                            p.setpok(true);

                            log.remove(c);
                            break;


                        }
                    }
                }
            log = Arena.getAgents(agent, gg);

            game.startGame();
            //  List<CL_Pokemon> ffs = Arena.json2Pokemons(pks);
            _win.setTitle("Pokemon game" + game.toString());
            int ind = 0;
            long dt ;
                    if(gameLevel==1)dt=80;
                    else
                        dt=120;


            while (game.isRunning()) {
                time = game.timeToEnd();
                moveAgants(game, gg);

                try {//here is for the picture pf the graph
                    if (ind % 2 == 0) {
                        _win.repaint();
                    }
                    Thread.sleep(dt);
                    ind++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String res = game.toString();

            System.out.println(res);
            System.exit(0);
        }
    }
    /**
     * by given game and graph This method decides how to agents that read while we start the game
     * this method can give moves for the game that can change the location for the Agent
     * that will be in the game
     * */
    public static void moveAgants(game_service game, directed_weighted_graph gg)  {
        dw_graph_algorithms g= new DWGraph_Algo();
        g.init(gg);
        String lg=  game.move();
        List<CL_Agent> log = Arena.getAgents(game.getAgents(), gg);
        _ar.setAgents(log);
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        for(CL_Pokemon C:ffs) {
            C.setpok(false);
            Arena.updateEdge(C,gg);

            if (game.getAgents() != null) {

                for (CL_Agent r : log) {

                    if (r.getNextNode() == -1) {
                        int dest = nextNode(gg, r.getSrcNode(), game);
                        game.chooseNextEdge(r.getID(), dest);



                        System.out.println("Agent: " + r.getID() + ", val: " + r.getValue() + "   turned to node: " + dest);
                    }

                    for (node_data n1 : gg.getV())
                        for (edge_data e : gg.getE(n1.getKey())) {
                            if (r.getSrcNode() == e.getSrc() && r.getNextNode() == e.getDest()) {
                                node_data ds = gg.getNode(e.getDest());
                                node_data sr = gg.getNode(e.getSrc());
                                geo_location Psr = sr.getLocation();
                                geo_location Pds = ds.getLocation();
                                geo_location Pro = r.getLocation();

                                double n = Pro.distance(Pds) / Psr.distance(Pds);
                                double nn = (n * e.getWeight()) / r.getSpeed();
                                int dt = 80;

                            }
                        }

                }
            }
        }
    }



    /**
     * Init the game and to init all the attributes for the game and as well init with help of
     * a graph which take given from a json file the pokemons and agents that will be
     * in the game at the time that you well init it
     * */
    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);


        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for (int a = 0; a < cl_fs.size(); a++) {
                Arena.updateEdge(cl_fs.get(a), gg);
            }
            for (int a = 0; a < rs; a++) {
                int ind = a % cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getDest();
                if (c.getType() < 0) {
                    nn = c.get_edge().getSrc();
                }

                game.addAgent(nn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
    * This is an important method in the second part of the project
    * here you can see what is the path that you will take
    * to eat the pokemon that was on the shortest path for you
    * this function will give the game the
    * next node that Agent will go to.
    * for example: if x is a max value in the start and wil take the shortest path
    * here have a temporary pokemon that will help us to store the pokemon that have the shortest path with an agent in this time
    * the function will return a key for the node that will be next and a neighbor for the node src
 */
    private static int nextNode(directed_weighted_graph g, int src, game_service game) {
        double x = Double.MAX_EXPONENT;
        List<node_data> n = new ArrayList<>();

        dw_graph_algorithms gg = new DWGraph_Algo();
        int dst = 0;
        int b = 0;
        gg.init(g);
        CL_Pokemon cc = null;

        edge_data e = new EdgeData();
        String ss = game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(ss);
        List<CL_Agent> sss = new ArrayList<>();
        String s= game.getAgents();
        sss= Arena.getAgents(s,g);
        CL_Agent ag= sss.get(0);
        System.out.println( ag.getLocation());
        System.out.println(ag.get_sg_dt());


        for (CL_Pokemon C : ffs) {
            int dt;
            if((ag.getLocation().x()>C.getLocation().x()+0.005||ag.getLocation().x()>C.getLocation().x()-0.005)||(ag.getLocation().y()>C.getLocation().y()+0.005||ag.getLocation().y()>C.getLocation().y()-0.005))
                dt=70;

            Arena.updateEdge(C, g);

            if (C.getpok() == false) {
                b = C.getType();

                edge_data edge = C.get_edge();

                if (b == 1) {
                    b = Math.min(edge.getSrc(), edge.getDest());
                    dst = Math.max(edge.getSrc(), edge.getDest());
                }
                if (b == -1) {
                    b = Math.max(edge.getSrc(), edge.getDest());
                    dst = Math.min(edge.getSrc(), edge.getDest());
                }
                if (x == gg.shortestPathDist(src, b)) {
                    if (cc.getValue() < C.getValue()) {
                        cc = C;
                        e = cc.get_edge();
                        cc.setpok(true);
                    }
                }
                if (x > gg.shortestPathDist(src, b)) {
                    x = gg.shortestPathDist(src, b);
                    e = C.get_edge();
                    cc = C;
                    cc.setpok(true);
                }
            }
        }

        if(cc.getType()==-1) {
            n = gg.shortestPath(src, Math.max(e.getSrc(), e.getDest()));
            dst = Math.min(e.getSrc(), e.getDest());
        }
        else {
            dst=Math.max(e.getSrc(), e.getDest());
            n = gg.shortestPath(src, Math.min(e.getSrc(), e.getDest()));
        }

        n.add(g.getNode(dst));


        Collection<edge_data> ee = g.getE(src);
        for (node_data nnn : n) {
            for (edge_data node : ee) {
                if (nnn.getKey() == node.getDest()) {

                    if( nnn.getKey()!=src)return nnn.getKey();
                    else
                        break;

                }

            }

        }

        return n.indexOf(1);
    }

}
