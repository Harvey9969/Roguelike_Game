package utils.DS;

import java.util.*;

public class RoomGraph {
    private Map<Room, Integer> roomToInd;
    private Set<Integer>[] adjacencyList;

    public RoomGraph(Iterable<Room> rooms) {
        int ind = 0;
        int size = 1;

        roomToInd = new HashMap<>();

        for (Room room: rooms) {
            roomToInd.put(room, ind);
            ind++;
            size++;
        }

        adjacencyList = new Set[size];
        for (int i = 0; i < size; i++) {
            adjacencyList[i] = new HashSet<>();
        }
    }

    public void connect(Room r1, Room r2) {
        if (
                !roomToInd.containsKey(r1)
                || !roomToInd.containsKey(r2)
        ) {
            throw new IllegalArgumentException("Only in graph rooms can be connected");
        }

        int v1 = roomToInd.get(r1);
        int v2 = roomToInd.get(r2);

        if (
                adjacencyList[v1].contains(v2)
                || adjacencyList[v2].contains(v1)
        ) {
            throw new IllegalArgumentException("Cannot connect already connected nodes");
        }

        adjacencyList[v1].add(v2);
        adjacencyList[v2].add(v1);
    }

    public Set<Integer> adj(Room r) {
        if (!roomToInd.containsKey(r)) {
            throw new IllegalArgumentException("Room not in graph");
        }

        return adjacencyList[roomToInd.get(r)];
    }

}
