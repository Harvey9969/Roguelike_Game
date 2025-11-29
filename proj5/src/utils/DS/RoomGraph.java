package utils.DS;

import edu.princeton.cs.algs4.Stack;
import utils.DS.tilecontainer.Room;

import java.util.*;

public class RoomGraph {
    public enum Place { START, MIDDLE, END }
    public record PartitionNode(int room, Place place) {}

    public final Map<Room, Integer> roomToInd;
    public final Map<Integer, Room> indToRoom;
    private final Set<Integer>[] adjacencyList;
    private final int numRooms;

    private boolean madeDungeon;

    public int start;
    public int p1;
    public int p2;
    public int end;

    private Set<Integer> nrooms; // normal rooms

    private Random random;

    public RoomGraph(Iterable<Room> rooms, Random random) {
        this.random = random;

        int ind = 0;

        roomToInd = new HashMap<>();
        indToRoom = new HashMap<>();

        for (Room room: rooms) {
            roomToInd.put(room, ind);
            indToRoom.put(ind, room);
            ind++;
        }

        adjacencyList = new Set[ind];
        for (int i = 0; i < ind; i++) {
            adjacencyList[i] = new HashSet<>();
        }

        numRooms = ind;
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

    private Set<Integer> adj(int r) {
        if (
                r >= numRooms
                || r < 0
        ) {
            throw new IllegalArgumentException("Room not in graph");
        }

        return adjacencyList[r];
    }

    public void genDungeon(Grid grid) {
        madeDungeon = true;
        nrooms = new HashSet<>();

        start = longestFrom(random.nextInt(numRooms)).getLast();
        List<Integer> diameter = longestFrom(start);
        end = diameter.getLast();

        Integer[] sizes = subSizes(start);
        Set<Integer> path = new HashSet<>(diameter);
        List<Integer> diameterWeights = new ArrayList<>();
        for (int node: diameter) {
            int size = 1;
            for (int neighbor: adj(node)) {
                if (!path.contains(neighbor)) {
                    size += sizes[neighbor];
                }
            }
            diameterWeights.add(size);
        }

        int roomsx1by3 = numRooms / 3;
        int roomsx2by3 = roomsx1by3 * 2;

        int cumSum = 0;
        int ind = 0;
        boolean foundFirst = false;
        boolean foundSecond = false;

        while (!foundFirst || !foundSecond) {
            if (ind >= diameterWeights.size()) {
                throw new IllegalStateException("Could not be partitioned");
            }

            cumSum += diameterWeights.get(ind);

            if (cumSum > roomsx1by3 && !foundFirst) {
                p1 = diameter.get(ind);
                foundFirst = true;
            } else if (cumSum > roomsx2by3 && !foundSecond) {
                p2 = diameter.get(ind);
                foundSecond = true;
            }

            ind++;
        }

        for (int i = 0; i < numRooms; i++) {
            if (!List.of(p1, p2, start, end).contains(i)) {
                nrooms.add(i);
            }
        }
    }

    public Set<PartitionNode> princessPartition() {
        if (!madeDungeon) {
            throw new IllegalStateException("Cannot partition non-dungeon graph");
        }

        Set<PartitionNode> result = new HashSet<>();

        int[] parent = new int[numRooms];
        Set<Integer> visited = new HashSet<>();

        Stack<PartitionNode> dfs = new Stack<>();
        parent[start] = -1;
        dfs.push(new PartitionNode(start, Place.START));

        while (!dfs.isEmpty()) {
            PartitionNode node = dfs.pop();

            int room = node.room;
            Place place = node.place;

            if (!visited.add(room)) {
                throw new IllegalStateException("Cannot partition cyclic graph");
            }

            if (room == p1) {
                place = Place.MIDDLE;
            } else if (room == p2) {
                place = Place.END;
            } else if (room != start && room != end) {
                result.add(node);
            }

            for (int neighbor: adj(room)) {
                PartitionNode newNode = new PartitionNode(neighbor, place);

                if (neighbor != parent[room]) {
                    parent[neighbor] = room;
                    dfs.push(newNode);
                }
            }
        }

        return result;
    }

    private List<Integer> longestFrom(int u) {
        Integer[] nodeTo = new Integer[numRooms];
        Deque<Integer> bfsDeque = new ArrayDeque<>();

        nodeTo[u] = -1;
        bfsDeque.addLast(u);

        int node = -1;
        while (!bfsDeque.isEmpty()) {
            node = bfsDeque.removeFirst();

            for (int neighbor: adj(node)) {
                if (nodeTo[neighbor] == null) {
                    nodeTo[neighbor] = node;
                    bfsDeque.addLast(neighbor);
                }
            }
        }

        if (node == -1) {
            throw new IllegalStateException("Cannot get longest path on no edge graph");
        }

        LinkedList<Integer> result = new LinkedList<>();
        while (node != u) {
            result.addFirst(node);
            node = nodeTo[node];
        }
        result.addFirst(node);

        return result;
    }

    private Integer[] subSizes(int u) {
        Integer[] sizes = new Integer[numRooms];
        Integer[] parents = new Integer[numRooms];

        Arrays.fill(parents, -1);

        subSizes(u, sizes, parents);

        return sizes;
    }

    private void subSizes(int u, Integer[] sizes, Integer[] parents) {
        sizes[u] = 1;

        for (int neighbor: adj(u)) {
            if (neighbor != parents[u]) {
                parents[neighbor] = u;
                subSizes(neighbor, sizes, parents);
                sizes[u] += sizes[neighbor];
            }
        }
    }

}
