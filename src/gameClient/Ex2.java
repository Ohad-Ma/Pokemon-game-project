package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Main game runner for Ex2. Handles the initialization, game logic, and movement of agents
 * on a directed weighted graph to capture Pokémons.
 */
public class Ex2 implements Runnable {

    public static MyFrame _win;
    private static Arena _arena;
    public static long userId = -999;
    public static int gameLevel = -999;
    public static Thread client = new Thread(new Ex2());
    public static float time;

    public static void main(String[] args) {
        if (args.length == 2) {
            userId = Long.parseLong(args[0]);
            gameLevel = Integer.parseInt(args[1]);
            client.start();
        } else {
            _win = new MyFrame("Pokemon Game");
            _win.LoginPanel();
        }
    }

    public static void setGameLevel(int level) {
        Ex2.gameLevel = level;
    }

    /**
     * Sets the user ID. Ignores invalid inputs.
     */
    public static void setLogin_id(long id) {
        if (id >= 0) {
            Ex2.userId = id;
        }
    }

    /**
     * Starts the game. Initializes agents and pokemons, then runs the game loop until time ends.
     */
    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(gameLevel);
        if (game == null) return;

        game.login(userId);

        directed_weighted_graph graph = game.getJava_Graph_Not_to_be_used();
        dw_graph_algorithms graphAlgo = new DWGraph_Algo();
        graphAlgo.init(graph);

        init(game);

        List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
        List<CL_Agent> agents = Arena.getAgents(game.getAgents(), graph);

        // Assign agents to pokemons initially
        if (pokemons.size() >= agents.size()) {
            for (CL_Agent agent : agents) {
                for (CL_Pokemon pokemon : pokemons) {
                    if (!pokemon.getpok()) {
                        Arena.updateEdge(pokemon, graph);
                        int startNode = pokemon.getType() == 1 ?
                                Math.min(pokemon.get_edge().getSrc(), pokemon.get_edge().getDest()) :
                                Math.max(pokemon.get_edge().getSrc(), pokemon.get_edge().getDest());
                        game.addAgent(startNode);
                        pokemon.setpok(true);
                        break;
                    }
                }
            }
        }

        game.startGame();
        _win.setTitle("Pokemon Game - " + game.toString());

        int frameCounter = 0;
        long frameDelay = (gameLevel == 1) ? 80 : 120;

        while (game.isRunning()) {
            time = game.timeToEnd();
            moveAgents(game, graph);

            if (frameCounter % 2 == 0) _win.repaint();
            try {
                Thread.sleep(frameDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            frameCounter++;
        }

        System.out.println(game.toString());
        System.exit(0);
    }

    /**
     * Moves agents toward their next destination node based on shortest path to nearest Pokémon.
     */
    public static void moveAgents(game_service game, directed_weighted_graph graph) {
        game.move();

        List<CL_Agent> agents = Arena.getAgents(game.getAgents(), graph);
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());

        _arena.setAgents(agents);
        _arena.setPokemons(pokemons);

        for (CL_Pokemon pokemon : pokemons) {
            pokemon.setpok(false);
            Arena.updateEdge(pokemon, graph);
        }

        for (CL_Agent agent : agents) {
            if (agent.getNextNode() == -1) {
                int nextNode = nextNode(graph, agent.getSrcNode(), game);
                game.chooseNextEdge(agent.getID(), nextNode);
                System.out.println("Agent " + agent.getID() + " moves to node: " + nextNode);
            }
        }
    }

    /**
     * Initializes the game arena, GUI window, and assigns initial agents to Pokémon.
     */
    private void init(game_service game) {
        directed_weighted_graph graph = game.getJava_Graph_Not_to_be_used();
        _arena = new Arena();
        _arena.setGraph(graph);
        _arena.setPokemons(Arena.json2Pokemons(game.getPokemons()));

        _win = new MyFrame("Ex2 Window");
        _win.setSize(1000, 700);
        _win.update(_arena);
        _win.show();

        try {
            JSONObject gameInfo = new JSONObject(game.toString()).getJSONObject("GameServer");
            int numAgents = gameInfo.getInt("agents");

            List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
            for (CL_Pokemon pokemon : pokemons) {
                Arena.updateEdge(pokemon, graph);
            }

            for (int i = 0; i < numAgents; i++) {
                CL_Pokemon target = pokemons.get(i % pokemons.size());
                int startNode = (target.getType() < 0) ?
                        target.get_edge().getSrc() : target.get_edge().getDest();
                game.addAgent(startNode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the next node an agent should move to based on shortest path to closest available Pokémon.
     */
    private static int nextNode(directed_weighted_graph graph, int src, game_service game) {
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);

        double minDistance = Double.MAX_VALUE;
        CL_Pokemon closestPokemon = null;

        List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
        for (CL_Pokemon pokemon : pokemons) {
            if (!pokemon.getpok()) {
                Arena.updateEdge(pokemon, graph);
                edge_data edge = pokemon.get_edge();

                int targetNode = (pokemon.getType() == -1) ?
                        Math.max(edge.getSrc(), edge.getDest()) : Math.min(edge.getSrc(), edge.getDest());

                double distance = algo.shortestPathDist(src, targetNode);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPokemon = pokemon;
                }
            }
        }

        if (closestPokemon == null) return src;

        closestPokemon.setpok(true);
        edge_data edge = closestPokemon.get_edge();
        int targetNode = (closestPokemon.getType() == -1) ?
                Math.max(edge.getSrc(), edge.getDest()) : Math.min(edge.getSrc(), edge.getDest());

        List<node_data> path = algo.shortestPath(src, targetNode);
        if (path.size() >= 2) {
            return path.get(1).getKey(); // next node in path
        }

        return targetNode;
    }
}
