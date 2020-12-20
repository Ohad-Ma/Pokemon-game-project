package gameClient;
import api.directed_weighted_graph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Arena_Test {

/*
* Make a strong connected graph
 * add some agents
 * try to add an agent on a node that does not exist
 * connect the nodes
 * make arena
 * make a list which will store the agents
 * add agents to the list
 * add the list to the arena
 * verify the arena isn't empty of agents
 * verify the list equals to the amount of agents in the arena
 * make a new empty agent list
 * verify it does not equals to amount of agents in the arena
 * set the new empty agent list as the agents in the arena
 * verify the amount of agents isn't as the previous list
 * verify the amount of agents are the same as the new list
 */
    @Test
    void agentsTest() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i<10; i++){
            graph.addNode(new NodeData(i));
        }
        Throwable exception1 = assertThrows(NullPointerException.class,() -> new CL_Agent(graph,10));
        assertNull(exception1.getMessage());
        for (int i = 1; i<10; i++){
            graph.connect(i, i+1, 2);
            graph.connect(i+1, i, 2);
        }
        Arena arena = new Arena();
        List<CL_Agent> agentList= new ArrayList<>();
        for(int i=1; i<10; i++){
            CL_Agent agent = new CL_Agent(graph,i);
            agentList.add(agent);
        }
        arena.setAgents(agentList);
        assertNotEquals(0, arena.getAgents());
        assertEquals(agentList, arena.getAgents());

        agentList = new ArrayList<>();
        assertNotEquals(agentList, arena.getAgents());

        arena.setAgents(agentList);
        assertEquals(agentList, arena.getAgents());
    }

    /*
     * Make an Arena object
     * Make a list of pokemons
     * add 999 pokemons to the list
     * add the list to the arena
     * verify arena isn't empty
     * verify it has the same amount of the pokemon list
     * add another pokemon to the list
     * update the arena
     * verify it updated
     * make a new list with no pokemons
     * match the new list with the older one see it's not equals
     * update the arena into the new list
     * verify arena doesn't hold the old lists pokemons
     * verify the arena hold the new list pokemons (zero)
     */
    @Test
    void pkmnTest() {
        Arena arena = new Arena();
        List<CL_Pokemon> pkmnList= new ArrayList<>();
        for(int i=0; i<999; i++){
            CL_Pokemon pkmn= new CL_Pokemon(null,1, 1,0, null);
            pkmnList.add(pkmn);
        }
        arena.setPokemons(pkmnList);
        assertNotEquals(0, arena.getPokemons());
        assertEquals(pkmnList, arena.getPokemons());
        pkmnList.add(new CL_Pokemon(null, -1, 2, 0, null));
        arena.setPokemons(pkmnList);
        assertEquals(pkmnList, arena.getPokemons());

        pkmnList = new ArrayList<>();
        assertNotEquals(pkmnList, arena.getPokemons());
        arena.setPokemons(pkmnList);
        assertEquals(pkmnList, arena.getPokemons());

    }
}