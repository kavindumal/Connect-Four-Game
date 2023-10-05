package lk.ijse.dep.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;

public class AiPlayer extends Player{
    boolean trueOrFalse;
    Winner winner;
    Random random = new Random();
    public AiPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col){
        do {
            col = (int) Math.floor(Math.random() * 6);
            if (board.isLegelMove(col)){
                break;
            } else continue;
        }while (true);
        board.updateMove(col, Piece.GREEN);
        board.getBoardUI().update(col,trueOrFalse);
        winner = board.findWinner();
        if (winner.getWinningPiece() != Piece.EMPTY){
            board.getBoardUI().notifyWinner(winner);
        }else {
            if (!board.existLegelMoves()){
                board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
            }
        }
    }

    public abstract class MonteCarloTreeSearch<T extends Transition>{
        private Node<T> current;

        public MonteCarloTreeSearch() {
            reset();
        }

        public void reset() {
            current = new Node<>(null, null, false);
        }

        public T getBestTransition() {
            if (getPossibleTransitions().isEmpty()) {
                // no possible transition
                // isOver MUST be true.
                return null;
            }
            final int currentPlayer = getCurrentPlayer();
            Node<T> nodeToExpand;
            boolean stop = false;
            // TODO : do it in a interuptable Thread
            do {
                nodeToExpand = selection();
                if (nodeToExpand == null) {
                    break;
                }
                // the tree has not been fully explored yet
                Node<T> expandedNode = expansion(nodeToExpand);
                int winner = simulation();
                backPropagation(expandedNode, winner);
            } while (!stop);
            // state is restored
            assert currentPlayer == getCurrentPlayer();
            T best = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            // all possible transitions have been set on root node
            // see expansion(N node)
            for (Node<T> child : current.getChilds()) {
                double value = child.ratio(currentPlayer);
                if (value > bestValue) {
                    bestValue = value;
                    best = child.getTransition();
                    assert best != null;
                }
            }
            return best;
        }

        @SuppressWarnings("unchecked")
        public final void doTransition(T transition) {
            makeTransition(transition);
            current = current.getChild(transition);
            current.makeRoot();
        }

        public final void undoTransition(T transition) {
            unmakeTransition(transition);
            current = new Node<>(current);
        }

        @SuppressWarnings("unchecked")
        private Node<T> selection() {
            Node<T> n = current;
            Node<T> next;
            final int player = getCurrentPlayer();
            do {
                T transition = selectTransition(n, player);
                if (transition == null) {
                    n.setTerminal(true);
                    if (n == current) {
                        return null;
                    } else {
                        // node has parent, rewind
                        unmakeTransition(n.getTransition());
                        next = n.getParent();
                    }
                } else {
                    next = n.getChild(transition);
                    makeTransition(transition);
                    if (next == null) {
                        // this transition has never been explored
                        // create child node and expand it
                        next = new Node<>(n, transition, isOver());
                    }
                }
                n = next;
            } while (!n.isLeaf());
            return n;
        }

        private Node<T> expansion(final Node<T> leaf) {
            if (leaf.isTerminal()) {
                return leaf;
            }
            T transition = expansionTransition();
            if (transition != null) {
                // expand the path with the chosen transition
                makeTransition(transition);
                return new Node<>(leaf, transition, isOver());
            } else {
                return leaf;
            }
        }

        private int simulation() {
            LinkedList<T> transitions = new LinkedList<>();
            // do
            while (!isOver()) {
                T transition = simulationTransition();
                assert transition != null;
                makeTransition(transition);
                transitions.add(transition);
            }
            int winner = getWinner();
            // undo
            while (!transitions.isEmpty()) {
                unmakeTransition(transitions.pollLast());
            }
            return winner;
        }

        private void backPropagation(Node<T> expandedNode, final int winner) {
            Node<T> n = expandedNode;
            while (n != null) {
                n.result(winner);
                Node<T> parent = n.getParent();
                if (parent == null) {
                    // root reached
                    break;
                }
                unmakeTransition(n.getTransition());
                n = parent;
            }
        }

        public abstract T selectTransition(Node<T> node, int player);

        public abstract T simulationTransition();

        public abstract T expansionTransition();

        protected abstract void makeTransition(T transition);

        protected abstract void unmakeTransition(T transition);

        public abstract Set<T> getPossibleTransitions();

        public abstract boolean isOver();

        public abstract int getWinner();

        public abstract int getCurrentPlayer();

    }

    public interface Transition extends Serializable{

    }

    public class Node<T extends Transition>{
        private final Map<T, Node<T>> childs;
        private final Map<Integer, Integer> wins;
        private long simulations = 0;
        private boolean terminal;
        private final T transition;
        private Node<T> parent;

        public Node(Node<T> parent, T transition, boolean terminal) {
            this.terminal = terminal;
            this.parent = parent;
            this.transition = parent == null ? null : transition;
            this.childs = new HashMap<>();
            this.wins = new HashMap<>();
            if (parent != null) {
                parent.childs.put(transition, this);
            }
        }
        Node(Node<T> child) {
            this.terminal = false;
            this.parent = null;
            this.transition = null;
            this.childs = new HashMap<>();
            this.wins = new HashMap<>();
            this.simulations = child.simulations();
            // copy stats
            childs.put(child.getTransition(), child);
            for (Map.Entry<Integer, Integer> e : child.wins.entrySet()) {
                wins.put(e.getKey(), e.getValue());
            }
        }

        /**
         * A {@link Node} is terminal when there is no child to explore.
         * The sub-Tree of this {@link Node} has been fully explored or the {@link Node} correspond
         * to a configuration where {@link MonteCarloTreeSearch#//isOver()} return true.
         * @return true If the {@link Node} is a terminal {@link Node}
         */
        // TODO propagate terminal information from node to node
        public boolean isTerminal() {
            return this.terminal;
        }

        public void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }

        /**
         * Get the value of the {@link Node} for the given player.
         * The {@link Node} with the greater value will be picked
         * as the best choice for this player.
         * @param player
         * @return
         */
        public double value(int player) {
            return wins(player);
        }

        /**
         * Get the cild {@link Node} reach by the given {@link Transition}
         * @param transition The {@link Transition}
         * @return The child {@link Node} or null if there's no child known for the given {@link Transition}
         */
        public Node<T> getChild(T transition) {
            return childs.get(transition);
        }

        /**
         * Return the parent {@link Node} of this {@link Node} and the {@link Transition} that lead to this {@link Node}
         * @return
         */
        public Node<T> getParent() {
            return parent;
        }

        /**
         * Return the {@link Transition} that lead to this {@link Node}
         * @return
         */
        public T getTransition() {
            return transition;
        }

        /**
         * Make this {@link Node} a root {@link Node} by removing the reference to its parent
         */
        public void makeRoot() {
            this.parent = null;
        }

        /**
         * A leaf {@link Node} is a node with no child.
         * There's two case where a {@link Node} can be leaf :
         * <ol>
         * <li>The {@link Node} is a terminal {@link Node}</li>
         * <li>The {@link Node} has never been expanded (has no child)</li>
         * </ol>
         * @return
         * 		true if the {@link Node} is a leaf {@link Node}
         */
        public boolean isLeaf() {
            return childs.isEmpty();
        }

        /**
         * Number of simulations back-propagated to this {@link Node}
         * @return
         */
        public long simulations() {
            return simulations;
        }

        /**
         * Number of simulation back-propagated to this {@link Node} where the given player has won
         * @param player
         * @return
         */
        public double ratio(int player) {
            Integer w = wins.get(player);
            if (w == null) {
                return 0;
            } else {
                return ((double) w) / simulations;
            }
        }

        public long wins(int player) {
            Integer w = wins.get(player);
            if (w == null) {
                return 0;
            } else {
                return w;
            }
        }

        /**
         * Propagate the result of a simulation to this {@link Node}.
         * After a call to this method, {@link #simulations()} is incremented as well as
         * {@link #wins(int)} for the given winner.
         * @param winner The winner of the back-propagated simulation
         */
        public void result(int winner) {
            simulations++;
            Integer w = wins.get(winner);
            if (w == null) {
                wins.put(winner, 1);
            } else {
                wins.put(winner, w + 1);
            }
        }

        /**
         * Returns the {@link Collection} of all the child of this {@link Node}
         * @return
         * 		The return {@link Collection} MUST NOT be null
         * 		If the {@link Node} is the leaf {@link Node}, then an empty {@link Collection} is returned
         */
        public Collection<Node<T>> getChilds() {
            return childs.values();
        }

        /**
         * Get the child {@link Node} reach by the given {@link Transition}
         * @param transition The {@link Transition} to fetch the child {@link Node} from
         * @return
         */
        public Node<T> getNode(T transition) {
            return childs.get(transition);
        }
    }

    public abstract class UCT<T extends Transition> extends MonteCarloTreeSearch<T> {

        private final double C = sqrt(2);

        // TODO if node is leaf pick random transition
        @Override
        @SuppressWarnings("unchecked")
        public T selectTransition(Node<T> node, final int player) {
            double v = Double.NEGATIVE_INFINITY;
            T best = null;
            for (T transition : getPossibleTransitions()) {
                Node<T> n = node.getChild(transition);
                if (n == null) {
                    // unexplored path
                    return transition;
                }
                if (!n.isTerminal()) {
                    // child already explored and non terminal
                    long simulations = n.simulations();
                    assert simulations > 0;
                    long wins = n.wins(player);
                    // w/n + C * Math.sqrt(ln(n(p)) / n)
                    // TODO : add a random hint to avoid ex-aequo
                    double value = (simulations == 0 ? 0 : wins / simulations + C * sqrt(log(node.simulations()) / simulations));
                    if (value > v) {
                        v = value;
                        best = transition;
                    }
                }
            }
            return best;
        }

    }
}